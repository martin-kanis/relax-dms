package org.fit.vutbr.relaxdms.web;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.fit.vutbr.relaxdms.web.admin.Admin;
import org.fit.vutbr.relaxdms.web.documents.DocumentCreate;
import org.fit.vutbr.relaxdms.web.documents.DocumentPage;
import org.fit.vutbr.relaxdms.web.user.User;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class WicketApplication extends WebApplication {
    
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
        mountPage("/admin", Admin.class);
        mountPage("/create-document", DocumentCreate.class);
        mountPage("/find-document", DocumentPage.class);
        
        getMarkupSettings().setStripWicketTags(true);
    }
}
