package org.fit.vutbr.relaxdms.web;

import java.io.Serializable;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.fit.vutbr.relaxdms.web.admin.Schema;
import org.fit.vutbr.relaxdms.web.admin.SchemaUpdate;
import org.fit.vutbr.relaxdms.web.documents.DocumentCreate;
import org.fit.vutbr.relaxdms.web.documents.DocumentList;
import org.fit.vutbr.relaxdms.web.documents.tabs.DocumentTabs;
import org.fit.vutbr.relaxdms.web.user.User;
import org.fit.vutbr.relaxdms.web.workflow.WorkflowPage;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class WicketApplication extends WebApplication implements Serializable {
    
    private final Logger logger = Logger.getLogger(getClass());

    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }
    
    @Override
    public void init() {
        super.init();
        
        // set CDI
        try {
            BeanManager manager = (BeanManager) new InitialContext()
                    .lookup("java:comp/BeanManager");
            new CdiConfiguration(manager).configure(this);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        
        mountPage("/user", User.class);
        mountPage("/add-schema", Schema.class);
        mountPage("/edit-schema", SchemaUpdate.class);
        mountPage("/create-document", DocumentCreate.class);
        mountPage("/find-document", DocumentList.class);
        mountPage("/document", DocumentTabs.class);
        mountPage("/workflow", WorkflowPage.class);
        
        getMarkupSettings().setStripWicketTags(true);
    }
}
