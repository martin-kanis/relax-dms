package org.fit.vutbr.relaxdms.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Martin Kanis
 */
@Path("workflow")
public interface WorkflowRest {
    
    @POST
    @Path("/fireUnfreeze")
    public Response fireUnfreezeWorkflow();
    
    @POST
    @Path("/fireRelease")
    public Response fireReleaseWorkflow();
    
    @GET
    @Path("/getCustomRules")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomRules();
    
    @POST
    @Path("/fireCustom/{rule}")
    public Response fireCustomWorkflow(@PathParam("rule") String rule);
}
