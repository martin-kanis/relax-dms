package org.fit.vutbr.relaxdms.web.user;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class User extends BasePage {

    public User(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.USER;
    }
}
