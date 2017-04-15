package org.fit.vutbr.relaxdms.rest.api;

import javax.ws.rs.Consumes;
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
@Path("workflow/document")
public interface DocumentWorkflowRest {
    
    @POST
    @Path("/approve")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response approveDoc(String docData);
    
    @POST
    @Path("/decline")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response declineDoc(String docData);
    
    @POST
    @Path("/submit/{manager}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitDoc(String docData, @PathParam("manager") String manager);
    
    @POST
    @Path("/assign/{assignee}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assignDoc(String docData, @PathParam("assignee") String assignee);
    
    @POST
    @Path("/sign")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signDoc(String docData);
    
    @POST
    @Path("/freeze")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response freezeDoc(String docData);
    
    @POST
    @Path("/release")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response releaseDoc(String docData);
    
    @POST
    @Path("/startProgress")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response startProgress(String docData);
    
    @POST
    @Path("/close")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response closeDoc(String docData);
    
    @POST
    @Path("/reopen")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response openDoc(String docData);
    
    @POST
    @Path("/addPermission/{user}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPermissionsToDoc(String docData, @PathParam("user") String user);
    
    @POST
    @Path("/export")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    public Response exportDoc(String docData);
}
