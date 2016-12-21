package org.fit.vutbr.relaxdms.web.documents;

import java.io.Serializable;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.documents.DocumentEditorData.EditorUseCase;

/**
 *
 * @author Martin Kanis
 */
public class DocumentCreate extends BasePage implements Serializable {
    
    @Inject
    private DocumentService documentService;
    
    public DocumentCreate(PageParameters parameters) {
        super(parameters);
     
        List<JsonNode> templates = documentService.getAllTemplates();
        // create map (template title) -> (template id)
        Map<String, String> templateMap = templates.stream()
                .collect(Collectors.toMap(t -> t.get("title").asText(), t -> t.get("_id").asText()));
        
        // get first template on the first render
        String tmp = parameters.get("templateId").toString();
        String templateId = (tmp == null) ? 
                templates.get(0).get("_id").textValue() : tmp;

        // get current template revision
        // new documents might be created only with newest revision of template
        String templateRev = documentService.getCurrentRevision(templateId);
        DocumentMetadata metadata = new DocumentMetadata();
        metadata.setSchemaId(templateId);
        metadata.setSchemaRev(templateRev);
             
        createTemplateList(templateMap);
        
        AbstractAjaxBehavior ajaxSaveBehaviour = new DocumentEditorBehavior(new Document(metadata), this);
        add(ajaxSaveBehaviour);

        DocumentEditorData editorData = 
                new DocumentEditorData(documentService.getDocumentById(templateId),
                        EditorUseCase.CREATE);
        add(new DocumentEditor("container", editorData));
    }
    
    private void createTemplateList(Map<String, String> templateMap) {  
        List<String> titleList = templateMap.keySet().stream().collect(Collectors.toList());
        ListView listview = new ListView("listView", titleList) {
            @Override
            protected void populateItem(ListItem item) {
                final String title = (String) item.getModelObject();
                final String id = templateMap.get(title);
                
                AjaxLink<Void> templateLink = new AjaxLink("templateLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        PageParameters params = new PageParameters();
                        params.add("templateId", id);
                        setResponsePage(DocumentCreate.class, params);
                    }
                };
                item.add(templateLink.add(new Label("label", title)));
            }
        };
        add(listview);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }  
}
