package org.fit.vutbr.relaxdms.web.documents;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData.EditorUseCase;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class DocumentPage extends BasePage implements Serializable {
    
    private final Logger log = Logger.getLogger(getClass());
    
    private String id;

    @Inject
    private DocumentService documentService;
    
    @Inject
    private Convert convert;
    
    public DocumentPage(PageParameters parameters) {
        super(parameters);
        
        id = getDocId(parameters);
        
        prepareEditor(Collections.EMPTY_MAP);
    }

    public DocumentPage(PageParameters parameters, Map diffMap) {
        super(parameters);
 
        id = getDocId(parameters);
        
        prepareEditor(diffMap);
    }
    
    private void createDeleteButton(String doc) {
        add(new AjaxLink("delete") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                documentService.deleteDocument(convert.stringToJsonNode(doc));
                setResponsePage(DocumentList.class);
            }
        });
    }
    
    private void prepareEditor(Map diffMap) {
        JsonNode json = documentService.getDocumentById(id);
        JsonNode schema = documentService.getSchema(json.get("schemaId").textValue(), json.get("schemaRev").textValue());
        String doc = convert.jsonNodeToString(json);
        
        createDeleteButton(doc);
        
        DocumentMetadata docData = new DocumentMetadata(json.get("_id").textValue(),
                json.get("_rev").textValue(),
                json.get("schemaId").textValue(),
                json.get("schemaRev").textValue());
      
        AbstractAjaxBehavior ajaxSaveBehaviour = new DocumentEditorBehavior(docData, this);
        add(ajaxSaveBehaviour);
        
        DocumentEditorData editorData = new DocumentEditorData(schema, EditorUseCase.UPDATE);
        editorData.setDocument(json);
        editorData.setDiffMap(diffMap);
        add(new DocumentEditor("container", editorData));
    }
    
    private String getDocId(PageParameters parameters) {
        StringValue sv = parameters.get("id");
        
        if (sv.isNull() || sv.isEmpty()) {
            log.info("ID is null or empty");
        }
        return sv.toString();
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }
}
