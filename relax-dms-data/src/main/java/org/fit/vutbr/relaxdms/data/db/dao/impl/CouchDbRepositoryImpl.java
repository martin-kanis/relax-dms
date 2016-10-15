package org.fit.vutbr.relaxdms.data.db.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.ektorp.AttachmentInputStream;
import org.ektorp.Revision;
import org.ektorp.http.HttpResponse;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.ShowFunction;
import org.ektorp.support.View;
import org.fit.vutbr.relaxdms.data.db.connector.DBConnectorFactory;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.system.configuration.ConfigurationService;
import org.ektorp.http.RestTemplate;
import org.fit.vutbr.relaxdms.api.system.Convert;
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
        db.create(json);
    }

    @Override
    public JsonNode find(String id) {
        return db.find(type, id);
    }
    
    @Override
    public void update(JsonNode json) {
        db.update(json);
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
}
