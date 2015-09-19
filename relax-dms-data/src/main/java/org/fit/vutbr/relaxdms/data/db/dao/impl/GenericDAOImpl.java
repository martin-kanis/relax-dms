package org.fit.vutbr.relaxdms.data.db.dao.impl;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import org.ektorp.CouchDbConnector;
import org.fit.vutbr.relaxdms.data.db.connector.DBConnectorFactory;
import org.fit.vutbr.relaxdms.data.db.dao.api.GenericDAO;
import org.fit.vutbr.relaxdms.data.db.dao.model.DatabaseEntity;

/**
 *
 * @author Martin Kanis
 * @param <T>
 */
@Stateless
public class GenericDAOImpl<T extends DatabaseEntity> implements GenericDAO<T> {
    
    protected CouchDbConnector db;
    
    @PostConstruct
    private void init() {
        db = DBConnectorFactory.build();
    }

    @Override
    public void create(T entity) {
        db.create(entity);
    } 
}
