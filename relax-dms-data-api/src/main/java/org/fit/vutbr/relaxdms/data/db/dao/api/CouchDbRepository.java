package org.fit.vutbr.relaxdms.data.db.dao.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.ektorp.Revision;

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
     * Returns all templates from database in JsonNode format.
     * @return List of JsonNode that represent templates
     */    
    public List<JsonNode> getAllTemplates();
    
    /**
     * Stores json node as document.
     * @param json JsonNode
     */
    public void storeJsonNode(JsonNode json);
    
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
     */
    public void update(JsonNode json);
    
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
}
