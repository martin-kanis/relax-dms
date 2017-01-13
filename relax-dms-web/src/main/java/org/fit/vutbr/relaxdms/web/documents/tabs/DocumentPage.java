package org.fit.vutbr.relaxdms.web.documents.tabs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditor;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorBehavior;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData.EditorUseCase;
import org.fit.vutbr.relaxdms.web.documents.DocumentList;

/**
 *
 * @author Martin Kanis
 */
public class DocumentPage extends Panel implements Serializable {
    
    private final String docId;

    @Inject
    private DocumentService documentService;
    
    @Inject
    private WorkflowService workflowService;
    
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
    
    private void createDeleteButton(String doc, boolean disabled) {
        AjaxLink deleteButton = new AjaxLink("delete") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                documentService.deleteDocument(convert.stringToJsonNode(doc));
                setResponsePage(DocumentList.class);
            }
        };
        if (disabled)
            deleteButton.add(new AttributeAppender("disabled", new Model<>("disabled")));
        add(deleteButton);
    }
    
    private void prepareEditor(Map diffMap) {
        JsonNode json = documentService.getDocumentById(docId);
        String doc = convert.jsonNodeToString(json);
        
        byte[] data = documentService.getDataFromJson(json);
        DocumentMetadata metadata = documentService.getMetadataFromJson(json);
        Workflow workflow = workflowService.getWorkflowFromJson(json);
        
        JsonNode schema = documentService.getSchema(metadata.getSchemaId(), metadata.getSchemaRev());
        AbstractAjaxBehavior ajaxSaveBehaviour = new DocumentEditorBehavior(new Document(data, metadata, workflow), this);
        add(ajaxSaveBehaviour);
        
        DocumentEditorData editorData = new DocumentEditorData(schema, EditorUseCase.UPDATE);
        editorData.setDocument(json.get("data"));
        editorData.setDiffMap(diffMap);
        
        StateEnum state = workflow.getState().getCurrentState();
        boolean readonly = !((state == StateEnum.OPEN) || (state == StateEnum.IN_PROGRESS));
        editorData.setReadonly(readonly);
        
        createDeleteButton(doc, readonly);
        
        add(new DocumentEditor("container", editorData));
    }
}
