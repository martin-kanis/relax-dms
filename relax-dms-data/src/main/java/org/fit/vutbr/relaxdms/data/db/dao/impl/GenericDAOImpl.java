package org.fit.vutbr.relaxdms.data.db.dao.impl;

import static java.util.Objects.requireNonNull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.ektorp.CouchDbConnector;
import org.fit.vutbr.relaxdms.data.db.connector.DBConnectorFactory;
import org.fit.vutbr.relaxdms.data.db.dao.api.GenericDAO;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 * @param <T>
 */
public abstract class GenericDAOImpl<T extends Document> implements GenericDAO<T> {
    
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
    public T read(String id) {
        return db.find(type, id);
    }
    
    @Override
    public void create(T entity) {
        db.create(entity);
    } 

    @Override
    public void delete(T entity) {
        requireNonNull(entity.getId(), "Provided entity must have non-null ID!");
        requireNonNull(entity.getRevision(), "Provided entity must have non-null revision!");
        db.delete(entity);
    }  
    
    @Override
    public void delete(String id, String rev) {
        db.delete(id, rev);
    }
}
