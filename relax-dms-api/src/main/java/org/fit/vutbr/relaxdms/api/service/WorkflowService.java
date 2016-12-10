package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author Martin Kanis
 */
public interface WorkflowService {
    
    /**
     * Returns workflow from document specified by id.
     * @param docId Id of document which workflow will be returned
     * @return JsonNode Workflow of document
     */
    public JsonNode getWorkflowFromDoc(String docId);
    
    /**
     * Approves document by setting property in workflow
     * @param doc Document to be approved
     * @param user User who approves document
     */
    public void approveDoc(JsonNode doc, String user);
    
    /**
     * Declines document by setting property in workflow
     * @param doc Document to be declined
     * @param user User who declines document
     */
    public void declineDoc(JsonNode doc, String user);
    
    /**
     * Checks if document is approved
     * @param doc 
     * @return boolean
     */
    public boolean isApproved(JsonNode doc);
    
    /**
     * Checks if document is declined
     * @param doc
     * @return boolean
     */
    public boolean isDeclined(JsonNode doc);
}
