/* 
 * The MIT License
 *
 * Copyright 2017 mkanis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
