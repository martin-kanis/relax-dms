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
import org.fit.vutbr.relaxdms.web.documents.tabs.DocumentTabs;

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
        
        // create title
        String user = auth.getUserName(req);
        add(new Label("user", user));
        
        createRoleList(roles);
        
        List<JsonNode> docsByAuthor = documentService.getDocumentsByAuthor(user);
        createTableHeaders("byAuthorId", "byAuthorName", docsByAuthor.size());
        createDocumentList("docListView", createListData(docsByAuthor));
        
        List<JsonNode> docsByAssignee = documentService.getDocumentsByAssignee(user);
        createTableHeaders("byAssigneeId", "byAssigneeName", docsByAssignee.size());
        createDocumentList("docListViewByAssignee", createListData(docsByAssignee));
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
    
    private void createDocumentList(String id, List<DocumentListData> docList) {
        ListView listview = new ListView(id, docList) {
            @Override
            protected void populateItem(ListItem item) {
                DocumentListData doc = (DocumentListData) item.getModelObject();
                
                PageParameters pp = new PageParameters();
                pp.add("id", doc.getId());
                
                BookmarkablePageLink<Void> docLink = new BookmarkablePageLink("docLink", DocumentTabs.class, pp);
                item.add(docLink.add(new Label("id", doc.getId())));
                item.add(new Label("title", doc.getTitle()));
            }
        };
        add(listview);
    }
    
    private List<DocumentListData> createListData(List<JsonNode> data) {
        return data.stream().map(e -> new DocumentListData(
                e.get("_id").textValue(), 
                e.get("data").get("Title").textValue(), 
                e.get("metadata").get("author").textValue())).collect(Collectors.toList());
    }
    
    private void createTableHeaders(String docId, String docTitle, int docsCount) {
        Label idLabel = new Label(docId, "Id");
        Label nameLabel = new Label(docTitle, "Title");
        
        if (docsCount == 0) {
            idLabel.setVisible(false);
            nameLabel.setVisible(false);
        }
        
        add(idLabel, nameLabel);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.USER;
    }
}
