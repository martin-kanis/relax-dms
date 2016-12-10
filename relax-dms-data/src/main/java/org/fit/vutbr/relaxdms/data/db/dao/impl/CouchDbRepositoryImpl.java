package org.fit.vutbr.relaxdms.data.db.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
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
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
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
    
    private Logger logger;
    
    private final RestTemplate restTemplate;
    
    @Inject
    public CouchDbRepositoryImpl(DBConnectorFactory dbFactory) {
        super(JsonNode.class, dbFactory.get());
        
        // generates standart views
        initStandardDesignDocument();

        restTemplate = dbFactory.getRestTemplate();
    }

    @Override
    @View(name = "all", map = "function(doc) { if (doc.author && doc.name ) emit(doc.author, doc.name)}")
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
    @View(name = "by_author", map = "function(doc) { emit(doc.author, doc)}")
    public List<JsonNode> findByAuthor(String author) {
        ViewQuery q = new ViewQuery()
                .viewName("by_author")
                .designDocId("_design/JsonNode")
                .key(author);
        
        return db.queryView(q, JsonNode.class);
    }
    
    @Override
    @View(name = "get_metadata", map = "function(doc) { emit(doc._id, "
            + "{author:doc.author, creationDate:doc.creationDate, "
            + "lastModifiedDate:doc.lastModifiedDate, lastModifiedBy:doc.lastModifiedBy})}")
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
    public JsonNode getWorkflowFromDoc(String id) {
        ViewQuery q = new ViewQuery()
                .viewName("get_workflow")
                .designDocId("_design/JsonNode")
                .key(id);
        
        ViewResult result = db.queryView(q);
        String json = result.getRows().get(0).getValue();
        return convert.stringToJsonNode(json);
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
    public void storeJsonNode(JsonNode json) {
        JsonNode doc = addMetadataToDocument(json, createDocMetadata(json));
        db.create(doc);
    }

    @Override
    public JsonNode find(String id) {
        return db.find(type, id);
    }
    
    @Override
    public JsonNode updateDoc(JsonNode json, String user) {
        try {
            JsonNode doc = addMetadataToDocument(json, updateDocMetadata(user, LocalDateTime.now()));
            db.update(doc);
            
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
    
    /**
     * Creates document metadata when document is created.
     * @param document JsonNode
     * @return DocumentMetadata
     */
    private DocumentMetadata createDocMetadata(JsonNode document) {
        LocalDateTime time = LocalDateTime.now();
        String author = getAuthor(document);
        return new DocumentMetadata(author, author, time, time);
    }
    
    private DocumentMetadata updateDocMetadata(String user, LocalDateTime time) {
        return new DocumentMetadata(null, user, null, time);
    }
    
    private JsonNode addMetadataToDocument(JsonNode doc, DocumentMetadata metadata) {
        ObjectNode node = (ObjectNode) doc;
        Map<String, Object> propertyMap = getFieldNamesWithValues(metadata);
        propertyMap.keySet().stream().forEach((key) -> {
            node.put(key, propertyMap.get(key).toString());
        });
        return (JsonNode) node;
    }
    
    private JsonNode removeMetadataFromDiff(JsonNode doc) {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        List<String> skipList = Arrays.asList("/_rev", "/lastModifiedDate", "/lastModifiedBy");

        for (JsonNode node : doc) {
            String path = node.get("path").textValue();
            if (!skipList.contains(path)) {
                result.add(node);
            }
        }
        return result;
    }
    
    private Map<String, Object> getFieldNamesWithValues(Object obj) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            if (!"author".equals(fieldName)) {
                String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    Method method = obj.getClass().getMethod(methodName);
                    Object fieldValue = method.invoke(obj);
                    
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
        return resultMap;
    }
    
    private String getAuthor(JsonNode document) {
        return document.get("author").textValue();
    }
}
