package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;

/**
 *
 * @author Martin Kanis
 */
public interface WorkflowService {
    
    /**
     * Returns workflow from document specified by id.
     * @param docId Id of document which workflow will be returned
     * @return Workflow Workflow of document
     */
    public Workflow getWorkflowFromDoc(String docId);
    
    /**
     * Returns workflow from json document
     * @param doc Document as JsonNode
     * @return Workflow of the document
     */
    public Workflow getWorkflowFromJson(JsonNode doc);
    
    /**
     * Approves document by setting property in workflow
     * @param docId String Id of document to be approved
     * @param docData Metadata and workflow of the document
     */
    public void approveDoc(String docId, Document docData);
    
    /**
     * Declines document by setting property in workflow
     * @param docId String Id of document to be declined
     * @param docData Metadata and workflow of the document
     */
    public void declineDoc(String docId, Document docData);
    
    /**
     * Checks if document is approved 
     * @param workflow
     * @return boolean
     */
    public boolean isApproved(Workflow workflow);
    
    /**
     * Checks if document is declined
     * @param workflow
     * @return boolean
     */
    public boolean isDeclined(Workflow workflow);
    
    /**
     * 
     * @param doc
     * @param workflow
     * @return 
     */
    public JsonNode addWorkflowToDoc(JsonNode doc, Workflow workflow);
    
    /**
     * Serialize provided json to workflow object.
     * @param json String 
     * @return Workflow
     */
    public Workflow serialize(String json);
}
