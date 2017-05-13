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
