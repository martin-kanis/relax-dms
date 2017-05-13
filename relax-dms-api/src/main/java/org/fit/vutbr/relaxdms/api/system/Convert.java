/* 
 * The MIT License
 *
 * Copyright 2017 mkanis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fit.vutbr.relaxdms.api.system;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Set;
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
    
    public StringBuilder docPermissionsToString(Set<String> permissions);
}
