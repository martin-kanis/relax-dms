package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentServiceImpl implements DocumentService {
    
    @Inject 
    private CouchDbRepository repo;
    
    private Logger logger;

    @Override
    public void storeDocument(JsonNode document) {
        repo.storeJsonNode(document);
    }

    @Override
    public JsonNode getDocumentById(String id) {
        return repo.find(id);
    }

    @Override
    public String createJSONSchema(Class clazz) {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        String result = null;
        
        try {
            mapper.acceptJsonFormatVisitor(mapper.constructType(clazz), visitor);
            JsonSchema jsonSchema = visitor.finalSchema();
  
            String schema = mapper.writeValueAsString(jsonSchema);
            ObjectNode node = (ObjectNode) mapper.readTree(schema);
            
            // add title property to schema to create form title
            node.put("title", clazz.getSimpleName());
            
            // iterate over nodes to find Object node
            for (JsonNode n: node) {
                // remove id, rev, attachments
                if (n instanceof ObjectNode) {
                    ObjectNode on = (ObjectNode) n;
                    on.remove(Arrays.asList("_id", "_rev", "_attachments"));
                }
            }
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JsonMappingException ex) {
            logger.error(ex);
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }

        return result;
    }

    @Override
    public List<JsonNode> getAll() {
        return repo.getAllDocuments();
    }

    @Override
    public void updateDocument(JsonNode document) {
        repo.update(document);
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
}
