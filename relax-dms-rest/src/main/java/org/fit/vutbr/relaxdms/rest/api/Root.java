package org.fit.vutbr.relaxdms.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Martin Kanis
 */
@Path("")
public interface Root {
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    String getDescription();
}
