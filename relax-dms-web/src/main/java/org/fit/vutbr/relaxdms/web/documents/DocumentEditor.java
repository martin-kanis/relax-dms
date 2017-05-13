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
package org.fit.vutbr.relaxdms.web.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData.EditorUseCase;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class DocumentEditor extends WebMarkupContainer {
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    private Label myScript;
    
    private Model<String> labelModel;
    
    private PackageTextTemplate ptt;

    public DocumentEditor(String id, DocumentEditorData editorData) {
        super(id);
        this.setOutputMarkupId(true);
        createJsonEditor(editorData);
    }

    private void createJsonEditor(DocumentEditorData editorData) {    
        // render json-editor script
        Map<String, Object> map = new HashMap<>();
        map.put("schema", editorData.getSchema());
        
        // editor for document creation
        if (editorData.getUseCase() == EditorUseCase.CREATE) {
            map.put("startval", "{}");
            map.put("readonly", false);
        // document update / show
        } else {            
            map.put("startval", editorData.getDocument());
            map.put("usecase", EditorUseCase.UPDATE);
            map.put("readonly", editorData.isReadonly());
            try {
                String jsonMap = new ObjectMapper().writeValueAsString(editorData.getDiffMap());
                map.put("diffData", jsonMap);
            } catch (JsonProcessingException ex) {
                logger.error(ex);
            }
            
        }
        
        ptt = new PackageTextTemplate(DocumentCreate.class, "../../../../../../editor.js");
        labelModel = Model.of(ptt.asString(map));

        myScript = new Label("jsonEditor", labelModel);
        myScript.setOutputMarkupId(true);
        myScript.setEscapeModelStrings(false);
        
        this.add(myScript);
    }
}
