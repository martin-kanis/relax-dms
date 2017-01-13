package org.fit.vutbr.relaxdms.web.documents;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.documents.tabs.DocumentTabs;

/**
 *
 * @author Martin Kanis
 */
public class DocumentList extends BasePage implements Serializable {
    
    @Inject
    private DocumentService documentService;

    public DocumentList(PageParameters parameters) {
        super(parameters);
        
        List<DocumentListData> docList = documentService.getAll().stream().map(e -> new DocumentListData(
                e.get("_id").textValue(), 
                e.get("data").get("name").textValue(), 
                e.get("metadata").get("author").textValue())).collect(Collectors.toList());

        createDocumentList(docList);
    }
    
    private void createDocumentList(List<DocumentListData> docList) {
        ListView listview = new ListView("listView", docList) {
            @Override
            protected void populateItem(ListItem item) {
                DocumentListData doc = (DocumentListData) item.getModelObject();
                
                PageParameters pp = new PageParameters();
                pp.add("id", doc.getId());
                
                BookmarkablePageLink<Void> docLink = new BookmarkablePageLink("docLink", DocumentTabs.class, pp);
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
