package org.fit.vutbr.relaxdms.web;

import java.io.Serializable;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.web.admin.Schema;
import org.fit.vutbr.relaxdms.web.admin.SchemaUpdate;
import org.fit.vutbr.relaxdms.web.cp.menu.Menu;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.documents.DocumentCreate;
import org.fit.vutbr.relaxdms.web.documents.DocumentList;
import org.fit.vutbr.relaxdms.web.user.User;

/**
 *
 * @author Martin Kanis
 */
public abstract class BasePage extends WebPage implements Serializable {
    
    public BasePage(final PageParameters parameters) {
        super(parameters);
        
        add(new Menu.Builder("navBar", HomePage.class, getActiveMenu())
            .addMenuItemAsDropdown(MenuItemEnum.ADMIN, Schema.class, "Add Schema")
            .addMenuItemAsDropdown(MenuItemEnum.ADMIN, SchemaUpdate.class, "Update Schema")
            .addMenuItem(MenuItemEnum.USER, User.class)
            .addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentCreate.class, "Create Document")
            .addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentList.class, "Find Document")
            //.addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentPage.class, "Edit Document")
            .build());
    }

    public abstract MenuItemEnum getActiveMenu();
}
