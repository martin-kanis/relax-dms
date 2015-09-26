package org.fit.vutbr.relaxdms.data.db.dao.impl;

import java.util.List;
import javax.ejb.Stateless;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.api.DocumentDAO;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentDAOImpl extends GenericDAOImpl<Document> implements DocumentDAO {

    public DocumentDAOImpl() {
        super(Document.class);
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
