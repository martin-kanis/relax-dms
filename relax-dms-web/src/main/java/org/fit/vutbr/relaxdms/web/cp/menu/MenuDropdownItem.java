package org.fit.vutbr.relaxdms.web.cp.menu;

import java.util.Collection;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

/**
 *
 * @author Martin Kanis
 */
public class MenuDropdownItem extends Panel {
    
    public MenuDropdownItem(String id, MenuItemEnum currentMenuItem,
			Collection<BookmarkablePageLink<?>> linksInMenuItem, boolean shouldBeActive) {
        super(id);

        WebMarkupContainer itemContainer = new WebMarkupContainer("itemContainer");
        if (shouldBeActive) {
            itemContainer.add(new AttributeAppender("class", " active "));
        }
        itemContainer.add(new Label("label", currentMenuItem.getLabel()));

        RepeatingView repeatingView = new RepeatingView("itemLinks");

        linksInMenuItem.stream().map((link) -> new MenuLinkItem(repeatingView.newChildId(), link, false)).forEach((menuLinkItem) -> {
            repeatingView.add(menuLinkItem);
        });

        itemContainer.add(repeatingView);
        add(itemContainer);
    }
}
