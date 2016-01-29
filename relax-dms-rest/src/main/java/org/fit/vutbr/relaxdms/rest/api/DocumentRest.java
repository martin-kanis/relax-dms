package org.fit.vutbr.relaxdms.rest.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 */
@Path("document")
public interface DocumentRest {
    
    @GET
    @Path("/ids")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAllDocIds();
    
    @GET
    @Path("/rev/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCurrentRevision(@PathParam("id") String id);
    
    @GET
    @Path("/revs/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getRevisions(@PathParam("id") String id);
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Document read(@PathParam("id") String id);
    
    @POST
    @Path("/store")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String json);
    
    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(String json);
    
    @DELETE
    @Path("/delete/{idRev}")
    public Response deleteByIdAndRev(@PathParam("idRev") String idRev);
}
