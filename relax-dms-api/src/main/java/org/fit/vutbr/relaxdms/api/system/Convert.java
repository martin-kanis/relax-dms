package org.fit.vutbr.relaxdms.api.system;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 */
public interface Convert {
    public List<String> revisionToString(List<Revision> revs);
    
    public <T extends Document> T serialize(Class clazz, String json);
    
    /**
     * Validates provided JSON.
     * @param json String
     * @return True if JSON is valid, false otherwise
     */
    public boolean isValidJson(String json);
    
    /**
     * Converts string to Json node.
     * @param json String
     * @return JsonNode or null
     */
    public JsonNode stringToJsonNode(String json);
}
