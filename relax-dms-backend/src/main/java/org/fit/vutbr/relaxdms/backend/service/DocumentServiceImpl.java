package org.fit.vutbr.relaxdms.backend.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.api.GenericDAO;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentServiceImpl implements DocumentService {
    
    @Inject
    private GenericDAO genericDAO;

    @Override
    public void storeDocument(Document document) {
        genericDAO.create(document);
    }
    
}
