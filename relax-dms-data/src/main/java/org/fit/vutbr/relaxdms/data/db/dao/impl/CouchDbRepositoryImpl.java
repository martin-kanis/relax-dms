package org.fit.vutbr.relaxdms.data.db.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flipkart.zjsonpatch.JsonDiff;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.ektorp.AttachmentInputStream;
import org.ektorp.Revision;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.http.HttpResponse;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.ShowFunction;
import org.ektorp.support.View;
import org.fit.vutbr.relaxdms.data.db.connector.DBConnectorFactory;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.system.configuration.ConfigurationService;
import org.ektorp.http.RestTemplate;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentListData;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
@RequestScoped
public class CouchDbRepositoryImpl extends CouchDbRepositorySupport<JsonNode> implements CouchDbRepository {

    @Inject
    private ConfigurationService config;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private DocumentService documentService;
    
    private final ObjectMapper mapper;
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    private final RestTemplate restTemplate;
    
    @Inject
    public CouchDbRepositoryImpl(DBConnectorFactory dbFactory) {
        super(JsonNode.class, dbFactory.get());
        
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        // generates standart views
        initStandardDesignDocument();

        restTemplate = dbFactory.getRestTemplate();
    }

    @Override
    @View(name = "all", map = "function(doc) { if (!doc.doc_template) emit(doc._id, "
            + "{id:doc._id, author:doc.metadata.author, title:doc.data.Title, permissions:doc.workflow.permissions})}")
    public List<DocumentListData> getAllDocuments() {
        ViewQuery q = new ViewQuery()
                .viewName("all")
                .designDocId("_design/JsonNode");
        
        return viewToDocListData(q);
    }
    
    private List<DocumentListData> viewToDocListData(ViewQuery q) {
        List<DocumentListData> resultList = new ArrayList<>();
        ViewResult result = db.queryView(q);
        result.getRows().stream().map((row) -> row.getValueAsNode()).forEach((docAsNode) -> {
            try {
                DocumentListData listData = mapper.treeToValue(docAsNode, DocumentListData.class);
                resultList.add(listData);
            } catch (JsonProcessingException ex) {
                logger.error(ex);
            }
        });
        return resultList;
    }
    
    @Override
    @View(name = "templates", map = "function(doc) { if (doc.doc_template) emit(doc.doc_template)}")
    public List<JsonNode> getAllTemplates() {
        return queryView("templates");
    }
    
    @Override
    @ShowFunction(name = "getSchemaId",  function = "(function getSchemaId(doc, req) {"
            + "return doc.metadata.schemaId; })")
    public String getSchemaIdFromDocument(String docId) {
        return getHttpRequest("getSchemaId", docId);
    }

    @Override
    @ShowFunction(name = "my_show", file = "../js/show.js")
    public String firstShow(String docId) {
        return getHttpRequest("my_show", docId);
    }
    
    @Override
    @View(name = "by_author", map = "function(doc) { if (!doc.doc_template) emit(doc.metadata.author, "
            + "{id:doc._id, author:doc.metadata.author, title:doc.data.Title, permissions:doc.workflow.permissions})}")
    public List<DocumentListData> findByAuthor(String author) {
        ViewQuery q = new ViewQuery()
                .viewName("by_author")
                .designDocId("_design/JsonNode")
                .key(author);
        
        return viewToDocListData(q);
    }
    
    @Override
    @View(name = "by_assignee", map = "function(doc) { if (!doc.doc_template) emit(doc.workflow.assignment.assignee, "
            + "{id:doc._id, author:doc.metadata.author, title:doc.data.Title, permissions:doc.workflow.permissions})}")
    public List<DocumentListData> findByAssignee(String assignee) {
        ViewQuery q = new ViewQuery()
                .viewName("by_assignee")
                .designDocId("_design/JsonNode")
                .key(assignee);
        
        return viewToDocListData(q);
    }
    
    @Override
    @View(name = "get_metadata", map = "function(doc) { emit(doc._id, doc.metadata)}")
    public DocumentMetadata getMetadataFromDoc(String id, String rev) {
        if (rev == null) {
            ViewQuery q = new ViewQuery()
                    .viewName("get_metadata")
                    .designDocId("_design/JsonNode")
                    .key(id);

            ViewResult result = db.queryView(q);
            try {
                DocumentMetadata metadata = mapper.treeToValue(result.getRows().get(0).getValueAsNode(), DocumentMetadata.class);
                metadata.setId(id);
                metadata.setRev(getCurrentRevision(id));
                return metadata;
            } catch (JsonProcessingException ex) {
                logger.error(ex);
            }
        } else {
            JsonNode doc = getDocumentByIdAndRev(id, rev);
            try {
                DocumentMetadata metadata = mapper.treeToValue(doc.get("metadata"), DocumentMetadata.class);
                metadata.setId(id);
                metadata.setRev(rev);
                return metadata;
            } catch (JsonProcessingException ex) {
                logger.error(ex);
            }
        }
        return null;
    }
    
    @Override
    @View(name = "get_workflow", map = "function(doc) { emit(doc._id, doc.workflow)}")
    public Workflow getWorkflowFromDoc(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("get_workflow")
                .designDocId("_design/JsonNode")
                .key(id);
        
        ViewResult result = db.queryView(q);
        String json = result.getRows().get(0).getValue();
        return workflowService.serialize(json);
    }
    
    
    
    /**
     * Sends HTTP request to perform specified show function to provided document
     * @param showName Show function to be performed
     * @param docid ID of document
     * @return String 
     */
    private String getHttpRequest(String showName, String docid) {
        HttpResponse response = db.getConnection().get(createShowUri(showName, docid));
        String result = new Scanner(response.getContent()).useDelimiter("\\A").next();
        return result;
    }
    
    private String createShowUri(String showName, String docid) {
        return config.getDbHost() + config.getDbName() + config.getDbShowPath() + showName + "/" + docid;
    }

    @Override
    public void storeDocument(JsonNode json, Document docData) {
        json = addWrapperAroundData(json);
        JsonNode doc = documentService.addMetadataToJson(json, updateDocMetadata(docData.getMetadata()));
        doc = workflowService.addWorkflowToDoc(doc, new Workflow());
        db.create(doc);
    }

    @Override
    public JsonNode find(String id) {
        return db.find(type, id);
    }
    
    @Override
    public JsonNode updateDoc(Document docData) {
        final ObjectReader reader = mapper.reader();
        JsonNode json, attachments;
        try {
            json = reader.readTree(new ByteArrayInputStream(docData.getData()));
            attachments = reader.readTree(new ByteArrayInputStream(docData.getAttachments()));
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        // if wrapper is already there, don't add it again
        if (json.get("data") == null)
            json = addWrapperAroundData(json);
        
        // add attachments
        if (!attachments.isNull()) {
            ((ObjectNode) json).set("_attachments", attachments);
        }
        
        DocumentMetadata metadata = docData.getMetadata();
        JsonNode doc = documentService.addMetadataToJson(json, updateDocMetadata(metadata));
        doc = workflowService.addWorkflowToDoc(doc, docData.getWorkflow());
        String id = metadata.getId();
        
        // get old version of document
        JsonNode oldDoc = find(id);
        byte[] bytes = jsonNodeToBytes(oldDoc);
        InputStream stream = new ByteArrayInputStream(bytes);
        String oldRev = oldDoc.get("_rev").textValue();
        AttachmentInputStream data = new AttachmentInputStream(oldRev, stream, "application/json");

        try {
            db.update(doc);

            String rev = getCurrentRevision(id);
            
            // store od version of document as attachment
            rev = db.createAttachment(id, rev, data);
            // update revision in Document model
            metadata.setRev(rev);
            docData.setAttachments(getAttachments(id));
            
            try {
                data.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
            return (JsonNode) JsonNodeFactory.instance.nullNode();
        } catch (UpdateConflictException ex) {
            JsonNode diff = JsonDiff.asJson(find(id), json);
  
            return removeMetadataFromDiff(diff);
        }
    }
    
    @Override
    public void updateDocs(Set<Document> docsData) {
        docsData.stream().forEach((doc) -> {
            JsonNode result = updateDoc(doc);
            
            if (!result.isNull()) {
                logger.warn("There is a conflict during batch update");
            }
        });
    }

    @Override
    public void delete(JsonNode json) {
        db.delete(json);
    }
    
    @Override
    public List<String> getAllDocIds() {
        return db.getAllDocIds();
    }
    
    @Override
    public String getCurrentRevision(String id) {
        return db.getCurrentRevision(id);
    }

    @Override
    public List<Revision> getRevisions(String id) {
        return db.getRevisions(id);
    }

    @Override
    public void updateSchema(JsonNode oldSchema, JsonNode newSchema) {
        // store new version of the schema as newest revision of the document
        db.update(newSchema);
        
        // prepare old schema as stream
        byte[] bytes = jsonNodeToBytes(oldSchema);
        InputStream stream = new ByteArrayInputStream(bytes);
        String oldRev = oldSchema.get("_rev").textValue();
        
        // create attachment as stream 
        // id will be revision of old schema
        // data will be stored as application/json content type
        // note that this also increment revision of the document
        AttachmentInputStream data = new AttachmentInputStream(oldRev, stream, "application/json");
        String id = newSchema.get("_id").textValue();
        db.createAttachment(id, getCurrentRevision(id), data);  
        
        try {
            data.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    @Override
    public JsonNode getDocumentByIdAndRev(String id, String rev) {
        String currentRev = getCurrentRevision(id);
        JsonNode schema = null;
        
        // current and needed revisions are the same so return current version of the schema
        if (currentRev.equals(rev)) {
            return this.find(id);
        } else {
            AttachmentInputStream ais = db.getAttachment(id, rev);
            try {
                byte[] data = IOUtils.toByteArray(ais);
                ObjectReader reader = (new ObjectMapper()).reader();
                schema = reader.readTree(new ByteArrayInputStream(data));
                ais.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
            return schema;
        }
    }
    
    private byte[] jsonNodeToBytes(JsonNode doc) {
        try {
            return mapper.writeValueAsBytes(doc);
        } catch (JsonProcessingException ex) {
            logger.error(ex);
            return null;
        }
    } 
    
    private JsonNode addWrapperAroundData(JsonNode json) {
        ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
        result.set("data", JsonNodeFactory.instance.objectNode());
        ObjectNode data = (ObjectNode) result.get("data");
        
        // copy data
        Iterator<Entry<String, JsonNode>> nodes = json.fields();
        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

            data.set(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private DocumentMetadata updateDocMetadata(DocumentMetadata metadata) { 
        // create deep copy
        DocumentMetadata updatedMetadata = SerializationUtils.clone(metadata);

        LocalDateTime now = LocalDateTime.now();
        updatedMetadata.setLastModifiedDate(now);
        
        if (metadata.getCreationDate() == null)
            updatedMetadata.setCreationDate(now);
        
        return updatedMetadata;
    }
    
    private JsonNode removeMetadataFromDiff(JsonNode doc) {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        List<String> skipList = Arrays.asList("/_rev", "/metadata/lastModifiedDate", "/metadata/lastModifiedBy");

        for (JsonNode node : doc) {
            String path = node.get("path").textValue();
            if (!skipList.contains(path)) {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public void storeSchema(JsonNode schema) {
        db.create(schema);
    }

    @Override
    @View(name = "get_all_metadata", map = "function(doc) { if (!doc.doc_template) {"
    + "emit(doc._id, [doc.data, doc._attachments, "
            + "{_id:doc._id, _rev:doc._rev, schemaId:doc.metadata.schemaId, schemaRev:doc.metadata.schemaRev, author:doc.metadata.author,"
            + "creationDate:doc.metadata.creationDate, lastModifiedDate:doc.metadata.lastModifiedDate, lastModifiedBy:doc.metadata.lastModifiedBy}, "
            + "doc.workflow])}}")
    public List<Document> getAllDocumentsMetadata() {
        ViewQuery q = new ViewQuery()
                .viewName("get_all_metadata")
                .designDocId("_design/JsonNode");
        
        ViewResult result = db.queryView(q);
        List<Document> resultList = new ArrayList<>();
        result.getRows().stream().map((row) -> (ArrayNode) row.getValueAsNode()).forEach((docAsNode) -> {
            try {
                byte[] data = mapper.writeValueAsBytes(docAsNode.get(0));
                byte[] attachments = mapper.writeValueAsBytes(docAsNode.get(1));
                DocumentMetadata metadata = mapper.treeToValue(docAsNode.get(2), DocumentMetadata.class);
                Workflow workflow = mapper.treeToValue(docAsNode.get(3), Workflow.class);
                Document docData = new Document(data, attachments, metadata, workflow);
                resultList.add(docData);
            } catch (JsonProcessingException ex) {
                logger.error(ex);
            }
        });
        return resultList;
    }
    
    @View(name = "get_attachments", map = "function(doc) {if (doc._attachments) {"
            + "emit(doc._id, doc._attachments)}}")
    public byte[] getAttachments(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("get_attachments")
                .designDocId("_design/JsonNode")
                .key(id);

        ViewResult result = db.queryView(q);
        try {
            return mapper.writeValueAsBytes(result.getRows().get(0).getValueAsNode());
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        }
        return null;
    }

    @Override
    @View(name = "count_versions", map = "function(doc) { var count = 1; if (doc._attachments) {"
            + " for(var key in doc._attachments) {count += 1;}}"
            + "emit(doc._id, count)}")
    public int countDocumentVersions(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("count_versions")
                .designDocId("_design/JsonNode")
                .key(id);
        
        ViewResult result = db.queryView(q);
        return Integer.parseInt(result.getRows().get(0).getValue());
    }
    
    @Override
    @View(name = "get_attachments_revs", map = "function(doc) {if (doc._attachments) { var revs = [];"
            + " for(var key in doc._attachments) {revs.push(key);}"
            + "emit(doc._id, revs)}}")
    public List<String> getAttachmentRevisions(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("get_attachments_revs")
                .designDocId("_design/JsonNode")
                .key(id);

        ViewResult result = db.queryView(q);
        try {
            return mapper.treeToValue(result.getRows().get(0).getValueAsNode(), List.class);
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        }
        return null;
    }

    @Override
    public int getRevisionIndex(String id, String rev) {
        if (rev == null) {
            return countDocumentVersions(id);
        } else {
            List<String> attachmentRevs = getAttachmentRevisions(id);
            return attachmentRevs.size() - attachmentRevs.indexOf(rev);
        }
    }

    @Override
    @View(name = "get_permissions", map = "function(doc) {if (doc.workflow) { "
            + "emit(doc._id, doc.workflow.permissions)}}")
    public Set<String> getPermissionsFromDoc(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("get_permissions")
                .designDocId("_design/JsonNode")
                .key(id);

        ViewResult result = db.queryView(q);
        try {
            return mapper.treeToValue(result.getRows().get(0).getValueAsNode(), Set.class);
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        }
        return null;
    }

    @Override
    @View(name = "get_author", map = "function(doc) { emit(doc._id, doc.metadata.author)}")
    public String getAuthor(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("get_author")
                .designDocId("_design/JsonNode")
                .key(id);
        
        ViewResult result = db.queryView(q);
        return result.getRows().get(0).getValue();
    }
}
