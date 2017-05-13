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
