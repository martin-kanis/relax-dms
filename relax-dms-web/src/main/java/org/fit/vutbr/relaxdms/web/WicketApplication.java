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
import org.fit.vutbr.relaxdms.web.error.Forbidden;
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
        mountPage("/forbidden", Forbidden.class);
        
        getMarkupSettings().setStripWicketTags(true);
    }
}
