package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.ektorp.Revision;

/**
 *
 * @author Martin Kanis
 */
public interface DocumentService {
    
    /**
     * Service method for storing document to the database.
     * @param document as json to be stored
     */
    public void storeDocument(JsonNode document);
    
     /**
     * Update schema to the database. New schema will saved as newest revision of the document. 
     * Old version of schema will be saved as attachment with its revision as Id.
     * @param oldSchema Previous version of the schema to be stored as attachment
     * @param newSchema Newest version of the schema to be stored as newest revision of the document
     */
    public void updateSchema(JsonNode oldSchema, JsonNode newSchema);
    
    /**
     * Updates document to the database
     * @param json Document to be updated as JsonNode
     */
    public void updateDocument(JsonNode json);
    
    /**
     * Delete document from the database
     * @param document Document to be deleted as JsonNode 
     */
    public void deleteDocument(JsonNode document);
    
    /**
     * Retrieves document from the database by provided ID.
     * @param id String Id of document to be retrieved.
     * @return Document as json
     */
    public JsonNode getDocumentById(String id);
    
    /**
     * Creates JSON schema from provided entity
     * @param clazz Class
     * @return String JSON schema
     */
    public String createJSONSchema(Class clazz); 
    
    /**
     * Return all documents with real data (no metadata documents) from database.
     * @return List of documents
     */
    public List<JsonNode> getAll();
    
    /**
     * Returns all documents from database created by specified user.
     * @param author Author of documents that will be returned
     * @return List of documents
     */
    public List<JsonNode> getDocumentsByAuthor(String author);
    
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
     * Returns id of schema from which was document created.
     * @param docId Document Id
     * @return String Id of schema
     */
    public String getSchemaIdFromDocument(String docId);
    
    /**
     * Simple view that returns some of document's properties as HTML.
     * @param docId Document Id
     * @return String Document as HTML
     */
    public String getDocumentAsHtml(String docId);
    
    /**
     * Returns all templates from database in JsonNode format.
     * @return List of JsonNode that represent templates
     */    
    public List<JsonNode> getAllTemplates();
    
    /**
     * Get schema specified by Id and revision. If revision is older than current revision it is retrieve from attachment.
     * @param id Id of schema as string
     * @param rev Revision of schema as string
     * @return Schema as JsonNode
     */
    public JsonNode getSchema(String id, String rev);
}
