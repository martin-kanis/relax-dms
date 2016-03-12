package org.fit.vutbr.relaxdms.data.db.dao.api;

import java.util.List;

/**
 *
 * @author Martin Kanis
 * @param <Document>
 */
public interface CouchDbRepository<Document> {
    
    /**
     * View function that finds documents by name field
     * @param name String
     * @return List of documents
     */
    public List<Document> findByName(String name);
    
    /**
     * Show function that creates HTML form JSON document
     * @param docid
     * @return 
     */
    public String firstShow(String docid);
    
    /**
     * Returns all documents from database that are descendant of Document class
     * @return List of documents
     */
    public List<Document> getAll();
}
