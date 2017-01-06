package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.LabelEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;
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
     * Checks if state of document matches expected state. 
     * @param workflow Workflow 
     * @param expectedState StateEnum
     * @return boolean True if expected state is the same as actual state of document
     */
    public boolean checkState(Workflow workflow, StateEnum expectedState);
    
    /**
     * Checks if document's labels contain expected label. 
     * @param workflow Workflow 
     * @param expectedLabel LabelEnum
     * @return boolean True if expected label is one of the document's labels
     */
    public boolean checkLabel(Workflow workflow, LabelEnum expectedLabel);
    
    /**
     * Changes state of provided document to expected state
     * @param docId Id of document that state to be changed
     * @param docData Document metadata
     * @param expectedState StateEnum  
     */
    public void changeState(String docId, Document docData, StateEnum expectedState);
    
    /**
     * Assigns document to given user.
     * @param docId Id of document to be updated
     * @param docData Document metadata
     * @param assignee Assignee of document
     */
    public void assignDocument(String docId, Document docData, String assignee);
    
    /**
     * Adds workflow to the json document.
     * @param doc Document where to put workflow
     * @param workflow Workflow to be added to the document
     * @return Document with added workflow
     */
    public JsonNode addWorkflowToDoc(JsonNode doc, Workflow workflow);
    
    /**
     * Adds label of specified type to the document.
     * @param docId Id of document where label will be added
     * @param docData Document metadata
     * @param labelType Type of label
     */
    public void addLabel(String docId, Document docData, LabelEnum labelType); 
    
    /**
     * Removes label of specified type from the document.
     * @param docId Id of document where label will be removed
     * @param docData Document metadata
     * @param labelType Type of label
     */
    public void removeLabel(String docId, Document docData, LabelEnum labelType);
    
    /**
     * Checks if the document can be signed.
     * @param docData Document metadata
     * @param isManager Boolean Is logged user manager
     * @return boolean
     */
    public boolean canBeSigned(Document docData, boolean isManager);
    
    /**
     * Serialize provided json to workflow object.
     * @param json String 
     * @return Workflow
     */
    public Workflow serialize(String json);
}
