package org.fit.vutbr.relaxdms.data.db.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.ektorp.CouchDbConnector;
import org.fit.vutbr.relaxdms.data.db.connector.DBConnectorFactory;
import org.fit.vutbr.relaxdms.data.db.dao.api.GenericDAO;

/**
 *
 * @author Martin Kanis
 * @param <T>
 */
public abstract class GenericDAOImpl<T extends JsonNode> implements GenericDAO<T> {
    
    protected CouchDbConnector db;
    
    private final Class<T> type;
    
    @Inject
    private DBConnectorFactory dbFactory;
    
    @PostConstruct
    private void init() {
        db = dbFactory.get();
    }
    
    public GenericDAOImpl(Class<T> type) {
        this.type = type;
    }
    
    @Override
    public JsonNode read(String id) {
        return db.find(type, id);
    }
    
    @Override
    public void create(T entity) {
        db.create(entity);
    } 

    @Override
    public void delete(T entity) {
        db.delete(entity);
    }  
    
    @Override
    public void delete(String id, String rev) {
        db.delete(id, rev);
    }
}
