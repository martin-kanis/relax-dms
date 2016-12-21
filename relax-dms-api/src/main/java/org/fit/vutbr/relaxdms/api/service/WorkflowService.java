package org.fit.vutbr.relaxdms.api.service;

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
     * Approves document by setting property in workflow
     * @param docId String Id of document to be approved
     * @param user User who approves document
     */
    public void approveDoc(String docId, String user);
    
    /**
     * Declines document by setting property in workflow
     * @param docId String Id of document to be declined
     * @param user User who declines document
     */
    public void declineDoc(String docId, String user);
    
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
}
