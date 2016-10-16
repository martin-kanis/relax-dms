package org.fit.vutbr.relaxdms.web.documents;

import java.io.Serializable;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class DocumentPage extends BasePage implements Serializable {
    
    private final Logger log = Logger.getLogger(getClass());
    
    private final String id;

    @Inject
    private DocumentService documentService;
    
    @Inject
    private Convert convert;

    public DocumentPage(PageParameters parameters) {
        super(parameters);
 
        StringValue sv = parameters.get("id");
        
        if (sv.isNull() || sv.isEmpty()) {
            log.info("ID is null or empty");
            // TODO error handler
        }

        id = sv.toString();
        Label label = new Label("label", documentService.getDocumentAsHtml(id));
        label.setEscapeModelStrings(false);
        add(label);
        
        String doc = convert.jsonNodeToString(documentService.getDocumentById(id));
        createUpdateButton(doc);
    }
    
    private void createUpdateButton(String doc) {
        Form<?> form = new Form<>("form");        
        form.add(new AjaxSubmitLink("update") {          
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                setResponsePage(new DocumentUpdate(new PageParameters(), convert.stringToJsonNode(doc)));
            }
        });
        
        form.add(new AjaxSubmitLink("delete") {          
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                documentService.deleteDocument(convert.stringToJsonNode(doc));
                setResponsePage(DocumentList.class);
            }
        });
                
        add(form);
    }
    

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }
}
