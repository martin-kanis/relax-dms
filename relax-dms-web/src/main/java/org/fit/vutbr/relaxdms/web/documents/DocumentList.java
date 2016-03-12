package org.fit.vutbr.relaxdms.web.documents;

import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class DocumentList extends BasePage {
    
    @Inject
    private DocumentService documentService;

    public DocumentList(PageParameters parameters) {
        super(parameters);
        
        List<Document> docList = documentService.getAll();

        ListView listview = new ListView("listView", docList) {
            @Override
            protected void populateItem(ListItem item) {
                Document doc = (Document) item.getModelObject();
                
                PageParameters pp = new PageParameters();
                pp.set("id", doc.getId());
                
                BookmarkablePageLink<Void> docLink = new BookmarkablePageLink("docLink", DocumentPage.class, pp);
                item.add(docLink.add(new Label("id", doc.getId())));
                item.add(new Label("name", doc.getName()));
                item.add(new Label("author", doc.getAuthor()));
            }
        };
        add(listview);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }
}
