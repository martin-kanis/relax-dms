package org.fit.vutbr.relaxdms.web.cp.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

/**
 *
 * @author Martin Kanis
 */
public class Menu extends Panel {

    public Menu(final Builder builder) {
        super(builder.id);
        
        BookmarkablePageLink<Void> homePageLink = new BookmarkablePageLink<>("homePageLink", builder.homePage);
        homePageLink.add(new Label("label", builder.applicationName));
        add(homePageLink);
        
        RepeatingView repeatingView = new RepeatingView("menuItems");
        
        for (MenuItemEnum item : builder.linksMap.keySet()) {
            boolean shouldBeActive = item.equals(builder.activeMenuItem);

            List<BookmarkablePageLink<?>> pageLinks = builder.linksMap.get(item);

            if (pageLinks.size() == 1) {
                BookmarkablePageLink<?> pageLink = pageLinks.get(0);
                MenuLinkItem menuLinkItem = new MenuLinkItem(repeatingView.newChildId(), pageLink, shouldBeActive);
                repeatingView.add(menuLinkItem);
            } else {
                repeatingView.add(new MenuDropdownItem(repeatingView.newChildId(), item, pageLinks,
                        shouldBeActive));
            }
        }

        add(repeatingView);
    }
    
    public static class Builder implements Serializable {

        private final String id;
        
        private final Class<? extends Page> homePage;
        
        private final String applicationName = "Relax DMS";
        
        private final MenuItemEnum activeMenuItem;

        private final Map<MenuItemEnum, List<BookmarkablePageLink<?>>> linksMap = new HashMap<>();

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
