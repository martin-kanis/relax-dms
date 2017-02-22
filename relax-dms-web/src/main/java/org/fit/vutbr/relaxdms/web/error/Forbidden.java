package org.fit.vutbr.relaxdms.web.error;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

/**
 *
 * @author Martin Kanis
 */
public class Forbidden extends WebPage {

    public Forbidden() {
        add(new Label("forbidden", "Forbidden"));
    }
}
