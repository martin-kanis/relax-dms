package org.fit.vutbr.relaxdms.data.db.dao.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.ektorp.Revision;

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
    
    /**
     * Finds document by provided ID
     * @param id String
     * @return Document as JsonNode
     */
    public JsonNode find(String id);
    
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
