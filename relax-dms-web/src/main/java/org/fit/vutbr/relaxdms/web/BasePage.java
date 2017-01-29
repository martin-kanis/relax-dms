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
