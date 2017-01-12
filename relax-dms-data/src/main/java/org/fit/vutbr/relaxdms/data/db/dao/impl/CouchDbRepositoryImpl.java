package org.fit.vutbr.relaxdms.data.db.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flipkart.zjsonpatch.JsonDiff;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
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
    private Convert convert;
    
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
    @View(name = "all", map = "function(doc) { if (!doc.doc_template) emit(doc.metadata.author, doc.name)}")
    public List<JsonNode> getAllDocuments() {
        return queryView("all");
    }
    
    @Override
    @View(name = "templates", map = "function(doc) { if (doc.doc_template) emit(doc.doc_template)}")
    public List<JsonNode> getAllTemplates() {
        return queryView("templates");
    }
    
    @Override
    @ShowFunction(name = "getSchemaId",  function = "(function getSchemaId(doc, req) {"
            + "return doc.schemaId; })")
    public String getSchemaIdFromDocument(String docId) {
        return getHttpRequest("getSchemaId", docId);
    }

    @Override
    @ShowFunction(name = "my_show", file = "../js/show.js")
    public String firstShow(String docId) {
        return getHttpRequest("my_show", docId);
    }
    
    @Override
    @View(name = "by_author", map = "function(doc) { emit(doc.metadata.author, doc)}")
    public List<JsonNode> findByAuthor(String author) {
        ViewQuery q = new ViewQuery()
                .viewName("by_author")
                .designDocId("_design/JsonNode")
                .key(author);
        
        return db.queryView(q, JsonNode.class);
    }
    
    @Override
    @View(name = "by_assignee", map = "function(doc) { emit(doc.workflow.assignment.assignee, doc)}")
    public List<JsonNode> findByAssignee(String assignee) {
        ViewQuery q = new ViewQuery()
                .viewName("by_assignee")
                .designDocId("_design/JsonNode")
                .key(assignee);
        
        return db.queryView(q, JsonNode.class);
    }
    
    @Override
    @View(name = "get_metadata", map = "function(doc) { emit(doc._id, "
            + "{author:doc.metadata.author, creationDate:doc.metadata.creationDate, "
            + "lastModifiedDate:doc.metadata.lastModifiedDate, lastModifiedBy:doc.metadata.lastModifiedBy})}")
    public Map<String, String> getMetadataFromDoc(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("get_metadata")
                .designDocId("_design/JsonNode")
                .key(id);
        
        ViewResult result = db.queryView(q);
        String json = result.getRows().get(0).getValue();
        
        Map<String, String> resultMap = new HashMap<>();
        try {
            resultMap = new ObjectMapper().readValue(json, HashMap.class);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return resultMap;
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
        JsonNode doc = documentService.addMetadataToJson(json, updateDocMetadata(docData.getMetadata()));
        doc = workflowService.addWorkflowToDoc(doc, new Workflow());
        db.create(doc);
    }

    @Override
    public JsonNode find(String id) {
        return db.find(type, id);
    }
    
    @Override
    public JsonNode updateDoc(JsonNode json, Document docData) {
        DocumentMetadata metadata = docData.getMetadata();
        JsonNode doc = documentService.addMetadataToJson(json, updateDocMetadata(metadata));
        doc = workflowService.addWorkflowToDoc(doc, docData.getWorkflow());
        
        try {
            db.update(doc);
            
            // update revision in Document model
            String rev = getCurrentRevision(metadata.get_id());
            metadata.set_rev(rev);
            
            return (JsonNode) JsonNodeFactory.instance.nullNode();
        } catch (UpdateConflictException ex) {
            String id = json.get("_id").textValue();
            JsonNode diff = JsonDiff.asJson(find(id), json);
  
            return removeMetadataFromDiff(diff);
        }
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
        byte[] bytes = convert.jsonNodeToString(oldSchema).getBytes(StandardCharsets.UTF_8);
        InputStream stream = new ByteArrayInputStream(bytes);
        String oldRev = oldSchema.get("_rev").textValue();
        
        // create attachment as stream 
        // id will be revision of old schema
        // data will be stored as application/json content type
        // note that this also increment revision of the document
        AttachmentInputStream data = new AttachmentInputStream(oldRev, stream, "application/json");
        db.createAttachment(newSchema.get("_id").textValue(), newSchema.get("_rev").textValue(), data);  
        
        try {
            data.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    @Override
    public JsonNode getSchema(String id, String rev) {
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
    + "emit(doc._id, [{_id:doc._id, _rev:doc._rev, schemaId:doc.metadata.schemaId, schemaRev:doc.metadata.schemaRev, author:doc.metadata.author,"
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
                DocumentMetadata metadata = mapper.treeToValue(docAsNode.get(0), DocumentMetadata.class);
                Workflow workflow = mapper.treeToValue(docAsNode.get(1), Workflow.class);
                Document docData = new Document(metadata, workflow);
                resultList.add(docData);
            } catch (JsonProcessingException ex) {
                logger.error(ex);
            }
        });
        return resultList;
    }
}
