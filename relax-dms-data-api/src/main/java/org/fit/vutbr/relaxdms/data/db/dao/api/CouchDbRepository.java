package org.fit.vutbr.relaxdms.data.db.dao.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;

/**
 *
 * @author Martin Kanis
 */
public interface CouchDbRepository {
    
    /**
     * Returns id of schema from which was document created.
     * @param docId Document Id
     * @return String Id of schema
     */
    public String getSchemaIdFromDocument(String docId);
    
    /**
     * Show function that creates HTML form JSON document
     * @param docid
     * @return 
     */
    public String firstShow(String docid);
    
    /**
     * Returns all documents from database.
     * @return List of documents
     */
    public List<JsonNode> getAllDocuments();
    
    /**
     * Returns all documents from database created by specified user.
     * @param author Author of documents that will be returned
     * @return List of documents
     */
    public List<JsonNode> findByAuthor(String author);
    
    /**
     * Returns all documents from database assigned to specified user.
     * @param assignee Assignee of document
     * @return List of documents
     */
    public List<JsonNode> findByAssignee(String assignee);
    
    /**
     * Returns all templates from database in JsonNode format.
     * @return List of JsonNode that represent templates
     */    
    public List<JsonNode> getAllTemplates();
    
    /**
     * Stores json node as document.
     * @param json JsonNode
     * @param docData Metadata and workflow to be added to the document
     */
    public void storeDocument(JsonNode json, Document docData);
    
    /**
     * Stores schema to the database.
     * @param schema Schema to be stored
     */
    public void storeSchema(JsonNode schema);
    
    /**
     * Update schema to the database. New schema will saved as newest revision of the document. 
     * Old version of schema will be saved as attachment with its revision as Id.
     * Example http://localhost:5984/db/my-docid/3-a2759ea8b50d82489bce49cc733b08d1
     * @param oldSchema Previous version of the schema to be stored as attachment
     * @param newSchema Newest version of the schema to be stored as newest revision of the document
     */
    public void updateSchema(JsonNode oldSchema, JsonNode newSchema);
    
    /**
     * Get schema specified by Id and revision. If revision is older than current revision it is retrieve from attachment.
     * @param id Id of schema as string
     * @param rev Revision of schema as string
     * @return Schema as JsonNode
     */
    public JsonNode getSchema(String id, String rev);
    
    /**
     * Finds document by provided ID
     * @param id String
     * @return Document as JsonNode
     */
    public JsonNode find(String id);
    
    /**
     * Updates document to the database
     * @param json Document to be updated as JsonNode
     * @param docData Metadata and workflow to be added to the document
     * @return Diff between previous and actual version of the document.
     If there is no updateDoc conflict returns empty json.
     */
    public JsonNode updateDoc(JsonNode json, Document docData);
    
    /**
     * Deletes document from the database
     * @param json Document to be deleted as JsonNode
     */
    public void delete(JsonNode json);
    
    /**
     * Gets IDs of all documents in the database.
     * @return List of documents IDs 
     */
    public List<String> getAllDocIds();
    
    /**
     * Returns current revision of document specified by ID.
     * @param id ID of document
     * @return Current revision as string
     */
    public String getCurrentRevision(String id);
    
    /**
     * Gets all revisions of document specified by ID.
     * @param id ID of document
     * @return List of revisions
     */
    public List<Revision> getRevisions(String id);
    
    /**
     * Returns metadata from document specified by Id.
     * @param id String Id of document
     * @return Map<String, String> Metadata of the document specified by Id
     */
    public Map<String, String> getMetadataFromDoc(String id);
    
    /**
     * Returns workflow from document specified by Id.
     * @param id String Id of document
     * @return Workflow Workflow of the document specified by Id
     */
    public Workflow getWorkflowFromDoc(String id);
    
    /**
     * Returns metadata and workflow from all documents
     * @return List of Documents
     */
    public List<Document> getAllDocumentsMetadata();
}
