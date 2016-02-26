package org.fit.vutbr.relaxdms.data.db.dao.api;

import java.util.List;

/**
 *
 * @author Martin Kanis
 * @param <Document>
 */
public interface CouchDbRepository<Document> {
    
    public List<Document> findByName(String name);
    
    public String firstShow(String docid);
}
