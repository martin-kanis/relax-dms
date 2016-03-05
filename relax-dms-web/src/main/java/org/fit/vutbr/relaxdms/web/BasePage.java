package org.fit.vutbr.relaxdms.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.web.admin.Admin;
import org.fit.vutbr.relaxdms.web.cp.menu.Menu;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.documents.DocumentCreate;
import org.fit.vutbr.relaxdms.web.documents.DocumentPage;
import org.fit.vutbr.relaxdms.web.user.User;

/**
 *
 * @author Martin Kanis
 */
public abstract class BasePage extends WebPage {
    
    public BasePage(final PageParameters parameters) {
        super(parameters);
        
        add(new Menu.Builder("navBar", HomePage.class, getActiveMenu())
            .addMenuItem(MenuItemEnum.ADMIN, Admin.class)
            .addMenuItem(MenuItemEnum.USER, User.class)
            .addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentCreate.class, "Create Document")
            .addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentPage.class, "Find Document")
            .addMenuItemAsDropdown(MenuItemEnum.DOCUMENT, DocumentPage.class, "Edit Document")
            .build());
    }

    public abstract MenuItemEnum getActiveMenu();
}
