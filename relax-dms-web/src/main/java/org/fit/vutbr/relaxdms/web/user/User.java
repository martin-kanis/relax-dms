package org.fit.vutbr.relaxdms.web.user;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.documents.DocumentListData;
import org.fit.vutbr.relaxdms.web.documents.DocumentPage;

/**
 *
 * @author Martin Kanis
 */
public class User extends BasePage implements Serializable {
    
    @Inject
    private AuthController auth;
    
    @Inject
    private DocumentService documentService;

    public User(PageParameters parameters) {
        super(parameters);
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        Set<String> roles = auth.getUserRoles(req);
        
        String user = auth.getUserName(req);
        add(new Label("user", user));
        createRoleList(roles);
        
        List<JsonNode> documents = documentService.getDocumentsByAuthor(user);
        
        String docId, docName;
        docId = docName = "";
        // table header
        if (documents.size() > 0) {
            docId = "Id";
            docName = "Name";
        } 
        add(new Label("docId", docId));
        add(new Label("docName", docName));
        
        List<DocumentListData> docList = documents.stream().map(e -> new DocumentListData(
                e.get("_id").textValue(), 
                e.get("name").textValue(), 
                e.get("author").textValue())).collect(Collectors.toList());
        createDocumentList(docList);
    }
    
    private void createRoleList(Set<String> roles) {  
        List<String> roleList = roles.stream().collect(Collectors.toList());
        ListView listview = new ListView("roleListView", roleList) {
            @Override
            protected void populateItem(ListItem item) {
                final String role = (String) item.getModelObject();

                item.add(new Label("label", role));
            }
        };
        add(listview);
    }
    
    private void createDocumentList(List<DocumentListData> docList) {
        ListView listview = new ListView("docListView", docList) {
            @Override
            protected void populateItem(ListItem item) {
                DocumentListData doc = (DocumentListData) item.getModelObject();
                
                PageParameters pp = new PageParameters();
                pp.set("id", doc.getId());
                
                BookmarkablePageLink<Void> docLink = new BookmarkablePageLink("docLink", DocumentPage.class, pp);
                item.add(docLink.add(new Label("id", doc.getId())));
                item.add(new Label("name", doc.getName()));
            }
        };
        add(listview);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.USER;
    }
}
