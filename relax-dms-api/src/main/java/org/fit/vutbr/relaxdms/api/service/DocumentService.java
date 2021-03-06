/* 
 * The MIT License
 *
 * Copyright 2017 mkanis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fit.vutbr.relaxdms.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentListData;
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
     * Return all documents from database that user can see.
     * @param user Logged user
     * @return List of documents
     */
    public List<DocumentListData> getAllAuthorized(String user);
    
    /**
     * Returns all documents list data from database created by specified user.
     * @param author Author of documents that will be returned
     * @return List of documents data
     */
    public List<DocumentListData> getDocumentsByAuthor(String author);
    
    /**
     * Returns all documents from database assigned to specified user.
     * @param assignee Assignee of document
     * @return List of documents data
     */
    public List<DocumentListData> getDocumentsByAssignee(String assignee);
    
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
     * Get document specified by Id and revision. If revision is older than current revision it is retrieve from attachment.
     * @param id Id of document as string
     * @param rev Revision of document as string
     * @return document as JsonNode
     */
    public JsonNode getDocumentByIdAndRev(String id, String rev);
    
    /**
     * Returns metadata from document specified by Id and Rev.
     * @param id String Id of document
     * @param rev String Rev of document
     * @return DocumentMetadata Metadata of the document specified by Id and rev
     */
    public DocumentMetadata getMetadataFromDoc(String id, String rev);
    
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
     * Returns attachments from provided json document.
     * @param doc JsonNode
     * @return byte[] attachments from json
     */
    public byte[] getAttachmentsFromJson(JsonNode doc);
    
    /**
     * Returns data from document specified by Id.
     * @param id Id of document
     * @return Byte array of document's data
     */
    public byte[] getDataFromDoc(String id);
    
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
     * Checks if user is authorized to see/write document.
     * @param docData
     * @param user User 
     * @return boolean
     */
    public boolean isUserAuthorized(DocumentListData docData, String user);
    
    /**
     * Validates provided JSON data with provided JSON schema.
     * @param data JsonNode
     * @param schema JsonNode
     * @return True if JSON data matches provided schema.
     */
    public boolean validateJsonDataWithSchema(JsonNode data, JsonNode schema);
    
    /**
     * Converts provided Json to XML.
     * Attachments are removed and XML root element called document is added.
     * @param json JsonNode
     * @return XML as string
     */
    public String jsonToXml(JsonNode json);
    
    /**
     * Exports provided json document to pdf represented as ByteArrayOutputStream.
     * @param json JsonNode
     * @return ByteArrayOutputStream with created pdf
     */
    public ByteArrayOutputStream convertToPdf(JsonNode json);
}
