package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class WorkflowServiceImpl implements WorkflowService {
    
    @Inject 
    private CouchDbRepository repo;

    @Override
    public JsonNode getWorkflowFromDoc(String docId) {
        return repo.getWorkflowFromDoc(docId);
    }

    @Override
    public void approveDoc(JsonNode doc, String user) {
        ObjectNode workflow = (ObjectNode) doc.get("workflow");
        workflow.replace("approved", JsonNodeFactory.instance.booleanNode(true));
        workflow.replace("declined", JsonNodeFactory.instance.booleanNode(false));
        repo.updateDoc(doc, user);
    }

    @Override
    public void declineDoc(JsonNode doc, String user) {
        ObjectNode workflow = (ObjectNode) doc.get("workflow");
        workflow.replace("approved", JsonNodeFactory.instance.booleanNode(false));
        workflow.replace("declined", JsonNodeFactory.instance.booleanNode(true));
        repo.updateDoc(doc, user);
    }

    @Override
    public boolean isApproved(JsonNode doc) {
        JsonNode workflow = doc.get("workflow");
        return workflow.get("approved").asBoolean();
    }

    @Override
    public boolean isDeclined(JsonNode doc) {
        JsonNode workflow = doc.get("workflow");
        return workflow.get("declined").asBoolean();
    }
}
