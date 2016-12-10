package org.fit.vutbr.relaxdms.web.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData.EditorUseCase;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class DocumentEditor extends WebMarkupContainer {
    
    @Inject
    private AuthController authController;
    
    private Logger log;
    
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
            map.put("author", authController.getUserName((HttpServletRequest) getRequest().getContainerRequest()));
        // document update
        } else {
            JsonNode document = editorData.getDocument();
            ((ObjectNode) document).remove(Arrays.asList("_id", "_rev", "schemaId", "schemaRev",
                    "lastModifiedBy", "creationDate", "lastModifiedDate", "workflow"));

            map.put("startval", document);
            map.put("usecase", EditorUseCase.UPDATE);
            try {
                String jsonMap = new ObjectMapper().writeValueAsString(editorData.getDiffMap());
                map.put("diffData", jsonMap);
            } catch (JsonProcessingException ex) {
                log.error(ex);
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
