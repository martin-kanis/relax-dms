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

import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentListData;
import java.io.Serializable;
import java.util.List;
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
import org.fit.vutbr.relaxdms.web.documents.tabs.DocumentTabs;

/**
 *
 * @author Martin Kanis
 */
public class DocumentList extends BasePage implements Serializable {
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private AuthController auth;

    public DocumentList(PageParameters parameters) {
        super(parameters);
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        String user = auth.getUserName(req);

        List<DocumentListData> docList = documentService.getAllAuthorized(user);

        add(new Label("user", user));
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
                item.add(new Label("title", doc.getTitle()));
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
