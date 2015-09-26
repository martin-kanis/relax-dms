package org.fit.vutbr.relaxdms.rest.impl;

import javax.annotation.ManagedBean;
import org.fit.vutbr.relaxdms.rest.api.Root;

/**
 *
 * @author Martin Kanis
 */
@ManagedBean
public class RootImpl implements Root {
    
    @Override
    public String getDescription() {
        return "<h1>relax-dms REST API</h1>" + "<ul><li><strong>Version:</strong> 1.0</li>";
    }  
}
