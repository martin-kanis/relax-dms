package org.fit.vutbr.relaxdms.data.db.dao.api;

import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Martin Kanis
 * @param <T>
 */
public interface GenericDAO<T extends CouchDbDocument> {
    
    /**
     * Finds document in the database specified by id. If no document is find, returns null.
     * @param id String
     * @return Found document from database or null
     */
    public T read(String id);
    
    /**
     * Creates entity in the database.
     * @param entity Entity to be created 
     */
    public void create(T entity);
    
    /**
     * Deletes entity from database.
     * @param entity Entity to be deleted
     */
    public void delete(T entity);
    
    /**
     * Deletes document from database specified by ID and Revision
     * @param id ID of document
     * @param rev Revision of document
     */
    public void delete(String id, String rev);
}
