package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
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
    
    private final Logger logger = Logger.getLogger(this.getClass().getName()); ;
    
    @Inject 
    private CouchDbRepository repo;
    
    @Inject
    @KSession("ksession1")
    private KieSession kSession;
    
    public void fireWorkflow(Workflow wf) {
        kSession.insert(wf);
        kSession.fireAllRules();
    }

    @Override
    public Workflow getWorkflowFromDoc(String docId) {
        ObjectMapper mapper = new ObjectMapper();
        try {    
            return mapper.treeToValue(repo.getWorkflowFromDoc(docId), Workflow.class);
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        }
        
        return null;
    }

    @Override
    public void approveDoc(JsonNode doc, String user) {
        ObjectNode workflow = (ObjectNode) doc.get("workflow");
        workflow.replace("approved", JsonNodeFactory.instance.booleanNode(true));
        workflow.replace("declined", JsonNodeFactory.instance.booleanNode(false));
        repo.updateDoc(doc, user);
        // TODO remove test
        fireWorkflow(getWorkflowFromDoc(doc.get("_id").asText()));
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
