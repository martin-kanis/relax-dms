package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.ApprovalEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Assignment;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Environment;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Label;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.LabelEnum;
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
    @KSession("ksession1")
    private KieSession kSession;
    
    private void fireWorkflow(Document docData) {
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
    public void approveDoc(Document docData) {
        docData.getWorkflow().getState().setApproval(ApprovalEnum.APPROVED);
        
        // set approvalBy
        String user = docData.getMetadata().getLastModifiedBy();
        docData.getWorkflow().getState().setApprovalBy(user);

        fireWorkflow(docData);
        
        repo.updateDoc(docData);
    }

    @Override
    public void declineDoc(Document docData) {
        docData.getWorkflow().getState().setApproval(ApprovalEnum.DECLINED);
        
        // set approvalBy
        String user = docData.getMetadata().getLastModifiedBy();
        docData.getWorkflow().getState().setApprovalBy(user);
        
        fireWorkflow(docData);
        
        repo.updateDoc(docData);
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
    public void changeState(Document docData, StateEnum expectedState) {
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
        
        if (expectedState == StateEnum.OPEN) {
            removeLabel(docData, LabelEnum.FREEZED);
            removeLabel(docData, LabelEnum.SIGNED);
        }
        
        if (expectedState == StateEnum.CLOSED) {
            removeLabel(docData, LabelEnum.FREEZED);
        }

        repo.updateDoc(docData);
    }

    @Override
    public void assignDocument(Document docData, String assignee) {   
        docData.getWorkflow().getAssignment().setAssignee(assignee);

        repo.updateDoc(docData);
    }
    
    @Override
    public void addLabel(Document docData, LabelEnum labelType) {
        Label label = new Label(labelType, docData.getMetadata().getLastModifiedBy());
        docData.getWorkflow().getLabels().add(label);
        
        repo.updateDoc(docData);
    }

    @Override
    public boolean checkLabel(Workflow workflow, LabelEnum expectedLabel) {
        Label label = new Label(expectedLabel);
        return workflow.getLabels().contains(label);
    }
    
    @Override
    public boolean canBeSigned(Document docData, boolean isManager) {
        Workflow workflow = docData.getWorkflow();
        boolean isApproved = isApproved(workflow);
        boolean isCorrectState = checkState(workflow, StateEnum.DONE) ||
                checkState(workflow, StateEnum.CLOSED);
        boolean signed = checkLabel(workflow, LabelEnum.SIGNED);
        
        return isManager && isApproved && isCorrectState && !signed;
    }
    
    @Override
    public void insertAllFacts(List<Document> docDataList, Environment env) {
        FactHandle handle = kSession.insert(env);

        Set<Document> modifiedFacts = new HashSet<>();
        kSession.setGlobal("docSet", modifiedFacts);
        
        docDataList.stream().forEach(docData -> kSession.insert(docData));
        
        kSession.fireAllRules();
        
        // retract the async fact
        kSession.retract(handle);

        repo.updateDocs(modifiedFacts);
    }  
    
    @Override
    public void submitDocument(Document docData, Environment env) {
        FactHandle handle = kSession.insert(env);
        
        cancelApproval(docData);
        removeLabel(docData, LabelEnum.FREEZED);
        
        fireWorkflow(docData);
        
        // retract the async fact
        kSession.retract(handle);
        
        repo.updateDoc(docData);
    }
    
    private void removeLabel(Document docData, LabelEnum labelType) {
        Label label = new Label(labelType);
        docData.getWorkflow().getLabels().remove(label);
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
