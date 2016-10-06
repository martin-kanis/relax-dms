package org.fit.vutbr.relaxdms.data.db.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import javax.ejb.Stateless;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.api.DocumentDAO;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentDAOImpl extends GenericDAOImpl<JsonNode> implements DocumentDAO {

    public DocumentDAOImpl() {
        super(JsonNode.class);
    }
    
    @Override
    public List<String> getAllDocIds() {
        return db.getAllDocIds();
    }
    
    @Override
    public String getCurrentRevision(String id) {
        return db.getCurrentRevision(id);
    }

    @Override
    public List<Revision> getRevisions(String id) {
        return db.getRevisions(id);
    }
}
