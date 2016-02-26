package org.fit.vutbr.relaxdms.web.documents;

import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class DocumentPage extends BasePage {
    
    private final String id;
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private CouchDbRepository repo;

    public DocumentPage(PageParameters parameters) {
        super(parameters);
 
        StringValue sv = parameters.get("id");
        
        if (sv.isNull() || sv.isEmpty()) {
            // TODO error handler
        }
        
        //id = sv.toString();
        id = "0b499d185d3da91fd3d71f4b46000010";
        Document d = documentService.getDocumentById(id);
        
        System.out.println(repo.findByName("My_bill"));
        
        
        Label label = new Label("label", repo.firstShow(id));
        label.setEscapeModelStrings(false);
        add(label);
    }
    

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }
}
