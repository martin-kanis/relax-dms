package org.fit.vutbr.relaxdms.data.db.dao.api;

import org.fit.vutbr.relaxdms.data.db.dao.model.DatabaseEntity;

/**
 *
 * @author Martin Kanis
 * @param <T>
 */
public interface GenericDAO<T extends DatabaseEntity> {
    
    public void create(T entity);
}
