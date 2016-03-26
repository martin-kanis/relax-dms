package org.fit.vutbr.relaxdms.backend.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class ConverImpl implements Convert {

    private Logger logger;
    
    @Override
    public List<String> revisionToString(List<Revision> revs) {
        return revs.stream().map(Revision::getRev).collect(Collectors.toList());
    }
    
    @Override
    public <T extends Document> T serialize(Class clazz, String json) {
        try {
            return (T) new ObjectMapper().readValue(json, clazz);
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
    }

    @Override
    public boolean isValidJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(json);
            return true;
        } catch (IOException ex) {
           return false;
        }
    }

    @Override
    public JsonNode stringToJsonNode(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(json);
        } catch (IOException ex) {
           return null;
        }
    }
}
