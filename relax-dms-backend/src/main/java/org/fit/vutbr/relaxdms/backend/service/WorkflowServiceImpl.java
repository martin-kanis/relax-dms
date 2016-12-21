package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.ApprovalEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;
import org.jboss.logging.Logger;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

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
    
    public void fireWorkflow(Workflow wf) {
        kSession.insert(wf);
        kSession.fireAllRules();
    }

    @Override
    public Workflow getWorkflowFromDoc(String docId) {    
        return repo.getWorkflowFromDoc(docId);
    }

    @Override
    public void approveDoc(String docId, String user) {
        JsonNode doc = documentService.getDocumentById(docId);
        ObjectNode state = (ObjectNode) doc.get("workflow").get("state");
        state.replace("approval", JsonNodeFactory.instance.textNode(ApprovalEnum.APPROVED.getName()));
        repo.updateDoc(doc, user);
        // TODO remove test
        fireWorkflow(getWorkflowFromDoc(doc.get("_id").asText()));
    }

    @Override
    public void declineDoc(String docId, String user) {
        JsonNode doc = documentService.getDocumentById(docId);
        ObjectNode state = (ObjectNode) doc.get("workflow").get("state");
        state.replace("approval", JsonNodeFactory.instance.textNode(ApprovalEnum.DECLINED.getName()));
        repo.updateDoc(doc, user);
    }

    @Override
    public boolean isApproved(Workflow workflow) {
        return workflow.getState().getApproval() == ApprovalEnum.APPROVED;
    }

    @Override
    public boolean isDeclined(Workflow workflow) {
        return workflow.getState().getApproval() == ApprovalEnum.DECLINED;
    }
}
