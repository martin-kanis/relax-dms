package org.fit.vutbr.relaxdms.web.cp.menu;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Martin Kanis
 */
public class MenuLinkItem extends Panel {
    public MenuLinkItem(String id, BookmarkablePageLink<?> pageLink, boolean shouldBeActive) {
        super(id);
        add(pageLink);
        if (shouldBeActive) {
            add(new AttributeAppender("class", "active"));
        }
    }
}
