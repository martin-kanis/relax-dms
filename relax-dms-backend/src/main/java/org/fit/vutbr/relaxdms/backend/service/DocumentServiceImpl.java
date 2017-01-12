package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentServiceImpl implements DocumentService {
    
    @Inject 
    private CouchDbRepository repo;
    
    private final Logger logger = Logger.getLogger(this.getClass().getName()); ;

    @Override
    public void storeDocument(JsonNode document, Document docData) {
        repo.storeDocument(document, docData);
    }

    @Override
    public JsonNode getDocumentById(String id) {
        return repo.find(id);
    }

    @Override
    public List<JsonNode> getAll() {
        return repo.getAllDocuments();
    }
    
    @Override
    public JsonNode updateDocument(JsonNode document, Document docData) {
        return repo.updateDoc(document, docData);
    }

    @Override
    public void updateSchema(JsonNode oldSchema, JsonNode newSchema) {
        repo.updateSchema(oldSchema, newSchema);
    }

    @Override
    public void deleteDocument(JsonNode document) {
        repo.delete(document);
    }
    
    @Override
    public List<String> getAllDocIds() {
        return repo.getAllDocIds();
    }
    
    @Override
    public String getCurrentRevision(String id) {
        return repo.getCurrentRevision(id);
    }

    @Override
    public List<Revision> getRevisions(String id) {
        return repo.getRevisions(id);
    }

    @Override
    public String getSchemaIdFromDocument(String docId) {
        return repo.getSchemaIdFromDocument(docId);
    }

    @Override
    public String getDocumentAsHtml(String docId) {
        return repo.firstShow(docId);
    }

    @Override
    public List<JsonNode> getAllTemplates() {
        return repo.getAllTemplates();
    }

    @Override
    public JsonNode getSchema(String id, String rev) {
        return repo.getSchema(id, rev);
    }

    @Override
    public List<JsonNode> getDocumentsByAuthor(String author) {
        return repo.findByAuthor(author);
    }
    
    @Override
    public List<JsonNode> getDocumentsByAssignee(String assignee) {
        return repo.findByAssignee(assignee);
    }

    @Override
    public Map<String, String> getMetadataFromDoc(String id) {
        return repo.getMetadataFromDoc(id);
    }

    @Override
    public DocumentMetadata getMetadataFromJson(JsonNode doc) {
        DocumentMetadata metadata = new DocumentMetadata();
        JsonNode md = doc.get("metadata");
        
        metadata.set_id(doc.get("_id").textValue());
        metadata.set_rev(doc.get("_rev").textValue());
        metadata.setSchemaId(md.get("schemaId").textValue());
        metadata.setSchemaRev(md.get("schemaRev").textValue());
        
        metadata.setAuthor(md.get("author").textValue());
        metadata.setLastModifiedBy(md.get("lastModifiedBy").textValue());
        metadata.setCreationDate(LocalDateTime.parse(md.get("creationDate").textValue()));
        metadata.setLastModifiedDate(LocalDateTime.parse(md.get("lastModifiedDate").textValue()));

        return metadata;
    }

    @Override
    public JsonNode removeMetadataFromJson(JsonNode doc) {
        ObjectNode resultDoc = (ObjectNode) doc;
        
        resultDoc.remove("_id");
        resultDoc.remove("_rev");
        resultDoc.remove("metadata");
        resultDoc.remove("workflow");
        
        return resultDoc;
    }

    @Override
    public JsonNode addMetadataToJson(JsonNode doc, DocumentMetadata metadata) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        JsonNode metadataNode = mapper.convertValue(metadata, JsonNode.class);
        
        if (metadata.get_id() != null) {
            ((ObjectNode) doc).put("_id", metadata.get_id());
            ((ObjectNode) doc).put("_rev", metadata.get_rev());
        }
        return ((ObjectNode) doc).set("metadata", metadataNode);
    }

    @Override
    public void storeSchema(JsonNode schema) {
        
    }

    @Override
    public List<Document> getAllDocumentsMetadata() {
        return repo.getAllDocumentsMetadata();
    }
}
