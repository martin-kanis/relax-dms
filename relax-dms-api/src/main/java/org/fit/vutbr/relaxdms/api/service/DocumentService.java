package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
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
     * @param document 
     */
    public void updateDocument(JsonNode document);
    
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
}
