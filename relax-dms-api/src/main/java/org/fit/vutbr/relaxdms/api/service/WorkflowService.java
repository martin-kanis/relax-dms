package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
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
     * @param docData Metadata and workflow of the document
     */
    public void approveDoc(Document docData);
    
    /**
     * Declines document by setting property in workflow
     * @param docData Metadata and workflow of the document
     */
    public void declineDoc(Document docData);
    
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
     * @param docData Document metadata
     * @param expectedState StateEnum  
     */
    public void changeState(Document docData, StateEnum expectedState);
    
    /**
     * Assigns document to given user.
     * @param docData Document metadata
     * @param assignee Assignee of document
     */
    public void assignDocument(Document docData, String assignee);
    
    /**
     * Adds workflow to the json document.
     * @param doc Document where to put workflow
     * @param workflow Workflow to be added to the document
     * @return Document with added workflow
     */
    public JsonNode addWorkflowToDoc(JsonNode doc, Workflow workflow);
    
    /**
     * Adds label of specified type to the document.
     * @param docData Document metadata
     * @param labelType Type of label
     */
    public void addLabel(Document docData, LabelEnum labelType); 
    
    /**
     * Checks if the document can be signed.
     * @param docData Document metadata
     * @param isManager Boolean Is logged user manager
     * @return boolean
     */
    public boolean canBeSigned(Document docData, boolean isManager);
    
    /**
     * Inserts metadata and workflow from all documents to the drools engine as facts.
     * @param docDataList List of Documents
     */
    public void insertAllFacts(List<Document> docDataList);
    
    /**
     * Serialize provided json to workflow object.
     * @param json String 
     * @return Workflow
     */
    public Workflow serialize(String json);
}
