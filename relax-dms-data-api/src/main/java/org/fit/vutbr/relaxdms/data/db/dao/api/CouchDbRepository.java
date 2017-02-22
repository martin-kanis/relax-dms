package org.fit.vutbr.relaxdms.data.db.dao.api;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Set;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentListData;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
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
    public List<DocumentListData> getAllDocuments();
    
    /**
     * Returns all documents from database created by specified user.
     * @param author Author of documents that will be returned
     * @return List of document data
     */
    public List<DocumentListData> findByAuthor(String author);
    
    /**
     * Returns all documents from database assigned to specified user.
     * @param assignee Assignee of document
     * @return List of documents data
     */
    public List<DocumentListData> findByAssignee(String assignee);
    
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
     * Get document specified by Id and revision. If revision is older than current revision it is retrieve from attachment.
     * @param id Id of document as string
     * @param rev Revision of document as string
     * @return document as JsonNode
     */
    public JsonNode getDocumentByIdAndRev(String id, String rev);
    
    /**
     * Finds document by provided ID
     * @param id String
     * @return Document as JsonNode
     */
    public JsonNode find(String id);
    
    /**
     * Updates document to the database
     * @param docData Metadata and workflow to be added to the document
     * @return Diff between previous and actual version of the document.
     If there is no updateDoc conflict returns empty json.
     */
    public JsonNode updateDoc(Document docData);
    
    /**
     * Updates set of documents.
     * @param docsData Set of documents
     */
    public void updateDocs(Set<Document> docsData);
    
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
     * Returns metadata from document specified by Id and Rev.
     * @param id String Id of document
     * @param rev String Rev of document
     * @return DocumentMetadata Metadata of the document specified by Id and rev
     */
    public DocumentMetadata getMetadataFromDoc(String id, String rev);
    
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
    
    /**
     * Returns count of document's versions. 
     * @param id Id of document
     * @return int number of versions of document
     */
    public int countDocumentVersions(String id);
    
    /**
     * Returns index of version specified by rev parameter.
     * @param id Id of document
     * @param rev Revision of document
     * @return Index of version
     */
    public int getRevisionIndex(String id, String rev);
    
    /**
     * Returns all revisions of document's attachments.
     * @param id od document
     * @return List of revisions
     */
    public List<String> getAttachmentRevisions(String id);
    
    /**
     * Returns permissions from document specified by Id.
     * @param id Id of document
     * @return Set of usernames
     */
    public Set<String> getPermissionsFromDoc(String id);
    
    /**
     * Returns document's author.
     * @param id Id of document
     * @return Author of document
     */
    public String getAuthor(String id);
}
