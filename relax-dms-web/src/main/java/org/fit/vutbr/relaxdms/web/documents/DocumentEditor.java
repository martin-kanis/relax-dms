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
