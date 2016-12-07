package org.fit.vutbr.relaxdms.api.system;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.ektorp.Revision;

/**
 *
 * @author Martin Kanis
 */
public interface Convert {
    /**
     * 
     * @param revs
     * @return 
     */
    public List<String> revisionToString(List<Revision> revs);
    
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
    
    /**
     * 
     * @param json
     * @return 
     */
    public String jsonNodeToString(JsonNode json);
    
    /**
     * Creates a deep copy of document by convert it to String and back to JsonNode.
     * @param doc JsonNode Document to be copied
     * @return JsonNode A copy of document
     */
    public JsonNode makeDocCopy(JsonNode doc);
}
