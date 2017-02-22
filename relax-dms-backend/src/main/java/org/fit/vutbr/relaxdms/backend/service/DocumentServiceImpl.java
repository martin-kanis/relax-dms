package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.client.keycloak.api.KeycloakAdminClient;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentListData;
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
    
    @Inject
    private KeycloakAdminClient authClient;
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void storeDocument(JsonNode document, Document docData) {
        repo.storeDocument(document, docData);
    }

    @Override
    public JsonNode getDocumentById(String id) {
        return repo.find(id);
    }

    @Override
    public List<DocumentListData> getAllAuthorized(String user) {
        List<DocumentListData> allDocuments = repo.getAllDocuments();
        
        // manager has rights to all documents
        List<String> managers = authClient.getManagers();
        if (managers.contains(user))
            return allDocuments;
        
        // user is not manager, filter documents
        return allDocuments.stream().filter(doc -> isUserAuthorized(doc, user)).collect(Collectors.toList());
    }
    
    @Override
    public JsonNode updateDocument(Document docData) {
        return repo.updateDoc(docData);
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
    public JsonNode getDocumentByIdAndRev(String id, String rev) {
        return repo.getDocumentByIdAndRev(id, rev);
    }

    @Override
    public List<DocumentListData> getDocumentsByAuthor(String author) {
        return repo.findByAuthor(author);
    }
    
    @Override
    public List<DocumentListData> getDocumentsByAssignee(String assignee) {
        return repo.findByAssignee(assignee);
    }

    @Override
    public DocumentMetadata getMetadataFromDoc(String id, String rev) {
        return repo.getMetadataFromDoc(id, rev);
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
        repo.storeSchema(schema);
    }

    @Override
    public List<Document> getAllDocumentsMetadata() {
        return repo.getAllDocumentsMetadata();
    }

    @Override
    public byte[] getDataFromJson(JsonNode doc) {
        try {
            return new ObjectMapper().writeValueAsBytes(doc.get("data"));
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        }
        
        return null;
    }

    @Override
    public byte[] getAttachmentsFromJson(JsonNode doc) {
        try {
            return new ObjectMapper().writeValueAsBytes(doc.get("_attachments"));
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        }
        
        return null;
    }

    @Override
    public int countDocumentVersions(String id) {
        return repo.countDocumentVersions(id);
    }
    
    @Override
    public int getRevisionIndex(String id, String rev) {
        return repo.getRevisionIndex(id, rev);
    }

    @Override
    public List<String> getAttachmentRevisions(String id) {
        return repo.getAttachmentRevisions(id);
    }

    @Override
    public boolean isUserAuthorized(DocumentListData docData, String user) {
        Set<String> permissions = docData.getPermissions();
        
        // empty set means, that document is available for anybody
        if (permissions.isEmpty())
            return true;
        
        // author has rights to his document
        if (docData.getAuthor().equals(user))
            return true;
        
        return permissions.contains(user);
    }
}
