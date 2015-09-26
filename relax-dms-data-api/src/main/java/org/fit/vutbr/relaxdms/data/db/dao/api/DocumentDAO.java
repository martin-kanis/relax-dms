package org.fit.vutbr.relaxdms.data.db.dao.api;

import java.util.List;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 */
public interface DocumentDAO extends GenericDAO<Document> {
    
    /**
     * Gets IDs of all documents in the database.
     * @return List of documents IDs 
     */
    public List<String> getAllDocIds();
    
    /**
     * Returns current revision of document specified by ID.
     * @param id ID of document
     * @return Current revision as string
     */
    public String getCurrentRevision(String id);
    
    /**
     * Gets all revisions of document specified by ID.
     * @param id ID of document
     * @return List of revisions
     */
    public List<Revision> getRevisions(String id);
}
