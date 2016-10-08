package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

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
     * Update document to the database
     * @param document Document to be update as JsonNode 
     */
    public void updateDocument(JsonNode document);
    
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
    public List<Document> getAll();
    
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
