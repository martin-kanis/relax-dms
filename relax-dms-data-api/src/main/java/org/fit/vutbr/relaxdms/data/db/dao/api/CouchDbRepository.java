package org.fit.vutbr.relaxdms.data.db.dao.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 *
 * @author Martin Kanis
 * @param <Document>
 */
public interface CouchDbRepository<Document> {
    
    /**
     * View function that finds documents by name field
     * @param name String
     * @return List of documents
     */
    public List<Document> findByName(String name);
    
    /**
     * Show function that creates HTML form JSON document
     * @param docid
     * @return 
     */
    public String firstShow(String docid);
    
    /**
     * Returns all documents from database that are descendant of Document class
     * @return List of documents
     */
    public List<Document> getAllDocuments();
    
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
     * Update document to the database
     * @param json 
     */
    public void update(JsonNode json);
}
