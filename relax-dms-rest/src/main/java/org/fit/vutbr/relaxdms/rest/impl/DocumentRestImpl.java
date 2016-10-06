package org.fit.vutbr.relaxdms.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.api.DocumentDAO;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.rest.api.DocumentRest;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentRestImpl implements DocumentRest {
    
    @Inject
    private DocumentDAO documentDAO;
    
    @Inject 
    private Convert convert;

    @Override
    public List<String> getAllDocIds() {
        return documentDAO.getAllDocIds();
    }

    @Override
    public String getCurrentRevision(String id) {
        // create valid JSON string by adding ""
        return "\"" + documentDAO.getCurrentRevision(id) + "\"";
    };

    @Override
    public List<String> getRevisions(String id) {
        return convert.revisionToString(documentDAO.getRevisions(id));
    }

    @Override
    public JsonNode read(String id) {
        return documentDAO.read(id);
    }

    @Override
    public Response create(String json) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(json, JsonNode.class);
            documentDAO.create(jsonNode);
        } catch (IOException ex) {
            return Response.status(500).entity("Jackson error: Could not serialize provided object!").build();
        }
        
        return Response.ok().build();
    }

    @Override
    public Response delete(String json) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(json, JsonNode.class);
            documentDAO.delete(jsonNode);
        } catch (IOException ex) {
            return Response.status(500).entity("Jackson error: Could not serialize provided object!").build();
        } catch (Exception ex) {
            return Response.status(404).entity(ex.getMessage()).build();
        }
        
        return Response.ok().build();
    }

    @Override
    public Response deleteByIdAndRev(String idRev) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
