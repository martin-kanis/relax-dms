package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.ApprovalEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Assignment;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;
import org.jboss.logging.Logger;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class WorkflowServiceImpl implements WorkflowService {
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    @Inject 
    private CouchDbRepository repo;
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    @KSession("ksession1")
    private KieSession kSession;
    
    public void fireWorkflow(Document docData) {
        FactHandle fh = kSession.getFactHandle(docData);
        if (fh == null) {
            kSession.insert(docData);
        }
        else 
            kSession.update(fh, docData);

        kSession.fireAllRules();
    }

    @Override
    public Workflow getWorkflowFromDoc(String docId) {    
        return repo.getWorkflowFromDoc(docId);
    }

    @Override
    public void approveDoc(String docId, Document docData) {
        JsonNode doc = documentService.getDocumentById(docId);
        docData.getWorkflow().getState().setApproval(ApprovalEnum.APPROVED);
        
        // set approvalBy
        String user = docData.getMetadata().getLastModifiedBy();
        docData.getWorkflow().getState().setApprovalBy(user);
        
        fireWorkflow(docData);
        
        repo.updateDoc(doc, docData);
    }

    @Override
    public void declineDoc(String docId, Document docData) {
        JsonNode doc = documentService.getDocumentById(docId);
        docData.getWorkflow().getState().setApproval(ApprovalEnum.DECLINED);
        
        // set approvalBy
        String user = docData.getMetadata().getLastModifiedBy();
        docData.getWorkflow().getState().setApprovalBy(user);
        
        fireWorkflow(docData);
        
        repo.updateDoc(doc, docData);
    }

    @Override
    public boolean isApproved(Workflow workflow) {
        return workflow.getState().getApproval() == ApprovalEnum.APPROVED;
    }

    @Override
    public boolean isDeclined(Workflow workflow) {
        return workflow.getState().getApproval() == ApprovalEnum.DECLINED;
    }

    @Override
    public Workflow getWorkflowFromJson(JsonNode doc) {
        JsonNode workflow = doc.get("workflow");
        try {
            return new ObjectMapper().treeToValue(workflow, Workflow.class);
        } catch (JsonProcessingException ex) {
            logger.error(ex);
            return null;
        }
    }
    
    @Override
    public Workflow serialize(String json) {
        try {
            return new ObjectMapper().readValue(json, Workflow.class);
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
    }

    @Override
    public JsonNode addWorkflowToDoc(JsonNode doc, Workflow workflow) {
        JsonNode workflowNode = new ObjectMapper().convertValue(workflow, JsonNode.class);
        return ((ObjectNode) doc).set("workflow", workflowNode);
    }

    @Override
    public boolean checkState(Workflow workflow, StateEnum expectedState) {
        return workflow.getState().getCurrentState() == expectedState;
    }

    @Override
    public void changeState(String docId, Document docData, StateEnum expectedState) {
        JsonNode doc = documentService.getDocumentById(docId);
        docData.getWorkflow().getState().setCurrentState(expectedState);
        
        // if doc is not assigned, assign it to user who performed change of state
        Assignment assignment = docData.getWorkflow().getAssignment();
        if ("Unassigned".equals(assignment.getAssignee())) {
            assignment.setAssignee(docData.getMetadata().getLastModifiedBy());
        }
        
        // cancel approval and approvalBy when reopen doc
        if (expectedState == StateEnum.OPEN) {
            cancelApproval(docData);
        }

        // document was submited, fire drools rules to perform steps
        if (expectedState == StateEnum.SUBMITED) {
            cancelApproval(docData);
            fireWorkflow(docData);
        }

        repo.updateDoc(doc, docData);
    }

    @Override
    public void assignDocument(String docId, Document docData, String assignee) {   
        JsonNode doc = documentService.getDocumentById(docId);
        docData.getWorkflow().getAssignment().setAssignee(assignee);

        repo.updateDoc(doc, docData);
    }
    
    private void cancelApproval(Document docData) {
        docData.getWorkflow().getState().setApproval(ApprovalEnum.NONE);
        docData.getWorkflow().getState().setApprovalBy(null);
    }
    
    private void queryWorkingMemory() {
        QueryResults results = kSession.getQueryResults( "Document" ); 
        for (QueryResultsRow row : results) {
            Document doc = (Document) row.get("$result");
            System.out.println(doc);
        }
    }
}
