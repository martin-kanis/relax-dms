package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;

/**
 *
 * @author Martin Kanis
 */
public interface DocumentService {
    
    /**
     * Service method for storing document to the database.
     * @param document as json to be stored
     * @param docData Metadata and workflow to be added to the document
     */
    public void storeDocument(JsonNode document, Document docData);
    
     /**
     * Update schema to the database. New schema will saved as newest revision of the document. 
     * Old version of schema will be saved as attachment with its revision as Id.
     * @param oldSchema Previous version of the schema to be stored as attachment
     * @param newSchema Newest version of the schema to be stored as newest revision of the document
     */
    public void updateSchema(JsonNode oldSchema, JsonNode newSchema);
    
    /**
     * Updates document to the database
     * @param docData Metadata and workflow to be added to the document
     @return Diff between previous and actual version of the document.
     If there is no updateDoc conflict returns empty json.
     */
    public JsonNode updateDocument(Document docData);
    
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
     * Returns all documents from database assigned to specified user.
     * @param assignee Assignee of document
     * @return List of documents
     */
    public List<JsonNode> getDocumentsByAssignee(String assignee);
    
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
     * Service method for storing schema to the database.
     * @param schema Schema to be stored
     */
    public void storeSchema(JsonNode schema);
    
    /**
     * Get schema specified by Id and revision. If revision is older than current revision it is retrieve from attachment.
     * @param id Id of schema as string
     * @param rev Revision of schema as string
     * @return Schema as JsonNode
     */
    public JsonNode getSchema(String id, String rev);
    
    /**
     * Returns metadata from document specified by Id.
     * @param id String Id of document
     * @return Map<String, String> Metadata of the document specified by Id
     */
    public Map<String, String> getMetadataFromDoc(String id);
    
    /**
     * Returns metadata from provided json document.
     * @param doc JsonNode
     * @return Metadata from json
     */
    public DocumentMetadata getMetadataFromJson(JsonNode doc);
    
    /**
     * Returns data from provided json document.
     * @param doc JsonNode
     * @return byte[] data from json
     */
    public byte[] getDataFromJson(JsonNode doc);
    
    /**
     * Removes all data we don't want to show in JSON editor.
     * For example metadata and workflow.
     * @param doc Json from where data will be removed
     * @return Json without unwanted data
     */
    public JsonNode removeMetadataFromJson(JsonNode doc);
    
    /**
     * Adds metadata to provided json document.
     * @param doc Json where metadata will be added
     * @param metadata Metadata to be added
     * @return JsonNode with metadata
     */
    public JsonNode addMetadataToJson(JsonNode doc, DocumentMetadata metadata);
    
    /**
     * Returns metadata and workflow from all documents
     * @return List of Documents
     */
    public List<Document> getAllDocumentsMetadata();
}
