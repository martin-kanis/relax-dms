package org.fit.vutbr.relaxdms.web;

import java.io.Serializable;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis 
 */
public class HomePage extends BasePage implements Serializable {

    public HomePage(PageParameters parameters) {
        super(parameters);
    }
    
    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.ADMIN;
    } 
}
