package org.fit.vutbr.relaxdms.web.documents.tabs;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditor;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorBehavior;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData.EditorUseCase;
import org.fit.vutbr.relaxdms.web.documents.DocumentList;
import org.fit.vutbr.relaxdms.web.documents.DocumentMetadata;

/**
 *
 * @author Martin Kanis
 */
public class DocumentPage extends Panel implements Serializable {
    
    private final String docId;

    @Inject
    private DocumentService documentService;
    
    @Inject
    private Convert convert;
    
    public DocumentPage(String id, String docId) {
        super(id);
        this.docId = docId;
        prepareEditor(Collections.EMPTY_MAP);
    }

    public DocumentPage(String id, String docId, Map diffMap) {
        super(id);
        this.docId = docId;
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
        JsonNode json = documentService.getDocumentById(docId);
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
}
