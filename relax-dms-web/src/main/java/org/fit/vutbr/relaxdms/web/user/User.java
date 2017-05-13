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
package org.fit.vutbr.relaxdms.web.user;

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
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentListData;
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
        
        List<DocumentListData> docsByAuthor = documentService.getDocumentsByAuthor(user);
        createTableHeaders("byAuthorId", "byAuthorName", docsByAuthor.size());
        createDocumentList("docListView", docsByAuthor);
        
        List<DocumentListData> docsByAssignee = documentService.getDocumentsByAssignee(user);
        createTableHeaders("byAssigneeId", "byAssigneeName", docsByAssignee.size());
        createDocumentList("docListViewByAssignee", docsByAssignee);
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
