package org.fit.vutbr.relaxdms.api.service;

import java.util.List;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 */
public interface DocumentService {
    
    /**
     * Service method for storing document to the database.
     * @param document Document to be stored
     */
    public void storeDocument(Document document);
    
    /**
     * Retrieves document from the database by provided ID.
     * @param id String Id of document to be retrieved.
     * @return Document
     */
    public Document getDocumentById(String id);
    
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
