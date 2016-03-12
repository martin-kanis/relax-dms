package org.fit.vutbr.relaxdms.web.documents;

import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class DocumentPage extends BasePage {
    
    private final Logger log = Logger.getLogger(getClass());
    
    private final String id;

    @Inject
    private CouchDbRepository repo;

    public DocumentPage(PageParameters parameters) {
        super(parameters);
 
        StringValue sv = parameters.get("id");
        
        if (sv.isNull() || sv.isEmpty()) {
            log.info("ID is null or empty");
            // TODO error handler
        }

        id = sv.toString();
        Label label = new Label("label", repo.firstShow(id));
        label.setEscapeModelStrings(false);
        add(label);
    }
    

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }
}
