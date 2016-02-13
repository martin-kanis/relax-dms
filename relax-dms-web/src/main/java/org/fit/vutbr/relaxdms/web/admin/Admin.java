package org.fit.vutbr.relaxdms.web.admin;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class Admin extends BasePage {

    public Admin(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.ADMIN;
    }
}
