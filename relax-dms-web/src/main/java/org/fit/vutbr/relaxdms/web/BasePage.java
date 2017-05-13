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
package org.fit.vutbr.relaxdms.web;

import java.io.Serializable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.web.admin.Schema;
import org.fit.vutbr.relaxdms.web.admin.SchemaUpdate;
import org.fit.vutbr.relaxdms.web.cp.menu.Menu;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.documents.DocumentCreate;
import org.fit.vutbr.relaxdms.web.documents.DocumentList;
import org.fit.vutbr.relaxdms.web.user.User;
import org.fit.vutbr.relaxdms.web.workflow.WorkflowPage;

/**
 *
 * @author Martin Kanis
 */
public abstract class BasePage extends WebPage implements Serializable {
    
    @Inject
    private AuthController authController;
    
    public BasePage(final PageParameters parameters) {
        super(parameters);
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        
        Menu.Builder menuBuilder = new Menu.Builder("navBar", HomePage.class, getActiveMenu());
        
        if (authController.isUserAuthorized(req, "admin")) {
            menuBuilder.addMenuItemAsDropdown(MenuItemEnum.ADMIN, Schema.class, "Add Schema")
            .addMenuItemAsDropdown(MenuItemEnum.ADMIN, SchemaUpdate.class, "Update Schema");
        }
        menuBuilder.addMenuItem(MenuItemEnum.USER, User.class);
          
        if (authController.isUserAuthorized(req, "writer")) {    
            menuBuilder.addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentCreate.class, "Create Document");
        }
        menuBuilder.addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentList.class, "Find Document");
                
        if (authController.isUserAuthorized(req, "manager"))       
            menuBuilder.addMenuItem(MenuItemEnum.WORKFLOW, WorkflowPage.class);
        
        add(menuBuilder.build());
    }

    public abstract MenuItemEnum getActiveMenu();
}
