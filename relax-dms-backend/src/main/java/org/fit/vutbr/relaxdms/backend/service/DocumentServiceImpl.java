package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        
        metadata.set_id(doc.get("_id").textValue());
        metadata.set_rev(doc.get("_rev").textValue());
        metadata.setSchemaId(doc.get("schemaId").textValue());
        metadata.setSchemaRev(doc.get("schemaRev").textValue());
        
        metadata.setAuthor(doc.get("author").textValue());
        metadata.setLastModifiedBy(doc.get("lastModifiedBy").textValue());
        metadata.setCreationDate(LocalDateTime.parse(doc.get("creationDate").textValue()));
        metadata.setLastModifiedDate(LocalDateTime.parse(doc.get("lastModifiedDate").textValue()));

        return metadata;
    }

    @Override
    public JsonNode removeMetadataFromJson(JsonNode doc) {
        ObjectNode resultDoc = (ObjectNode) doc;
        for (Field field : DocumentMetadata.class.getDeclaredFields()) {
            String name = field.getName();
            if (!"author".equals(name))
                resultDoc.remove(name);
        }
        resultDoc.remove("workflow");
        return resultDoc;
    }

    @Override
    public JsonNode addMetadataToJson(JsonNode doc, DocumentMetadata metadata, Set<String> skipFields) {
        ObjectNode resultDoc = (ObjectNode) doc;
        Map<String, Object> resultMap = new HashMap<>();
        
        for (Field field : metadata.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            if (!skipFields.contains(fieldName)) {
                String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    Method method = metadata.getClass().getMethod(methodName);
                    Object fieldValue = method.invoke(metadata);
                    
                    // skip null properties
                    if (fieldValue != null)
                        resultMap.put(fieldName, fieldValue);
                } catch (NoSuchMethodException | SecurityException ex) {
                    logger.error(ex);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    logger.error(ex);
                }
            }
        }

        resultMap.keySet().stream().forEach((key) -> {
            resultDoc.put(key, resultMap.get(key).toString());
        });
        return resultDoc;
    }

    @Override
    public void storeSchema(JsonNode schema) {
        
    }

    @Override
    public List<Document> getAllDocumentsMetadata() {
        return repo.getAllDocumentsMetadata();
    }
}
