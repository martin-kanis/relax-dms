package org.fit.vutbr.relaxdms.web.cp.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.web.HomePage;

/**
 *
 * @author Martin Kanis
 */
public class Menu extends Panel {
    
    @Inject
    private AuthController authController;

    public Menu(final Builder builder) {
        super(builder.id);
        
        createMainMenu(builder);

        createRightMenu();    
    }
    
    private void createMainMenu(final Builder builder) {
        BookmarkablePageLink<Void> homePageLink = new BookmarkablePageLink<>("homePageLink", builder.homePage);
        homePageLink.add(new Label("label", builder.applicationName));
        add(homePageLink);
        
        RepeatingView menuItems = new RepeatingView("menuItems");
        
        builder.linksMap.keySet().stream().forEach((item) -> {
            boolean shouldBeActive = item.equals(builder.activeMenuItem);

            List<BookmarkablePageLink<?>> pageLinks = builder.linksMap.get(item);

            if (pageLinks.size() == 1) {
                BookmarkablePageLink<?> pageLink = pageLinks.get(0);
                MenuLinkItem menuLinkItem = new MenuLinkItem(menuItems.newChildId(), pageLink, shouldBeActive);
                menuItems.add(menuLinkItem);
            } else {
                menuItems.add(new MenuDropdownItem(menuItems.newChildId(), item, pageLinks,
                        shouldBeActive));
            }
        });
        
        add(menuItems);
    }
    
    private void createRightMenu() {
        Link logout = new Link("logoutLink") {
            @Override
            public void onClick() {
                authController.logout(getHttpRequest());
                
                // redirect to homepage
                getRequestCycle().setResponsePage(HomePage.class);
            }
        };
        
        ExternalLink userLink;
        if (authController.isLoggedIn(getHttpRequest())) {
            String accountURI = authController.getAccountURI(getHttpRequest());
            String username = authController.getUserName(getHttpRequest());
            userLink = new ExternalLink("userLink", accountURI, username);
            
            logout.add(new Label("logout", "Logout"));
        } else {
           userLink = new ExternalLink("userLink", "", "");
           userLink.setVisible(false);
           logout.add(new Label("logout", ""));
           logout.setVisible(false);
        }
        
        add(userLink);
        add(logout);
    }
    
    private HttpServletRequest getHttpRequest() {
        return ((HttpServletRequest) getRequest().getContainerRequest());
    }

    public static class Builder implements Serializable {

        private final String id;
        
        private final Class<? extends Page> homePage;
        
        private final String applicationName = "Relax DMS";
        
        private final MenuItemEnum activeMenuItem;

        private final Map<MenuItemEnum, List<BookmarkablePageLink<?>>> linksMap = new LinkedHashMap<>();

        public Builder(String id, Class<? extends Page> homePage, MenuItemEnum activeMenuItem) {
            this.id = id;
            this.homePage = homePage;
            this.activeMenuItem = activeMenuItem;
        }
        
        public Menu build() {
            return new Menu(this);
        }
        
        public Builder addMenuItem(MenuItemEnum menuItem, Class<? extends Page> page) {
            BookmarkablePageLink<Page> link = new BookmarkablePageLink<>("link", page);
            link.setBody(new Model<>(menuItem.getLabel()));
            
            List<BookmarkablePageLink<?>> pageList = linksMap.get(menuItem);
            
            putToMultimap(pageList, link, menuItem);
            
            return this;
        }
        
        public Builder addMenuItemAsDropdown(MenuItemEnum menuItem, Class<? extends Page> page, String label) {
            BookmarkablePageLink<Page> link = new BookmarkablePageLink<>("link", page);
            link.setBody(new Model<>(label));
            
            List<BookmarkablePageLink<?>> pageList = linksMap.get(menuItem);
            
            putToMultimap(pageList, link, menuItem);
            
            return this;
        }
        
        private void putToMultimap(List<BookmarkablePageLink<?>> pageList, BookmarkablePageLink<Page> link,
                MenuItemEnum menuItem) {            
            // page list is empty, so create list 
            if (pageList == null) {
                pageList = new ArrayList<>();
                pageList.add(link);
                linksMap.put(menuItem, pageList);
            } else {
                pageList.add(link);
            }
        }
    }
}
