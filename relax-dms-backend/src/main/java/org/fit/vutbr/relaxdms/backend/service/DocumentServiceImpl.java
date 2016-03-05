package org.fit.vutbr.relaxdms.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.api.DocumentDAO;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentServiceImpl implements DocumentService {
    
    @Inject
    private DocumentDAO documentDAO;
    
    private Logger logger;

    @Override
    public void storeDocument(Document document) {
        documentDAO.create(document);
    }

    @Override
    public Document getDocumentById(String id) {
        return documentDAO.read(id);
    }

    @Override
    public String createJSONSchema(Class clazz) {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        String result = null;
        
        try {
            mapper.acceptJsonFormatVisitor(mapper.constructType(clazz), visitor);
            JsonSchema jsonSchema = visitor.finalSchema(); 
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
        } catch (JsonMappingException ex) {
            logger.error(ex);
        } catch (JsonProcessingException ex) {
            logger.error(ex);
        }
        
        return result;
    }
    
}
