package org.fit.vutbr.relaxdms.web;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class HomePage extends BasePage {

    public HomePage(PageParameters parameters) {
        super(parameters);
    }
    
    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.ADMIN;
    } 
}
