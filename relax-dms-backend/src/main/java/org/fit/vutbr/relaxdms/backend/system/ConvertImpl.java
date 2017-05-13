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
package org.fit.vutbr.relaxdms.backend.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.api.system.Convert;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class ConvertImpl implements Convert {
    
    @Override
    public List<String> revisionToString(List<Revision> revs) {
        return revs.stream().map(Revision::getRev).collect(Collectors.toList());
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

    @Override
    public String jsonNodeToString(JsonNode json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(json);
        } catch (IOException ex) {
           return null;
        }
    }

    @Override
    public JsonNode makeDocCopy(JsonNode doc) {
        String tmp = jsonNodeToString(doc);
        return stringToJsonNode(tmp);
    }

    @Override
    public StringBuilder docPermissionsToString(Set<String> permissions) {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        for (String user: permissions) {
            if (!isFirst) {
                result.append("\n");
            }
            result.append(user);
            isFirst = false;
        }
        
        return result;
    }
}
