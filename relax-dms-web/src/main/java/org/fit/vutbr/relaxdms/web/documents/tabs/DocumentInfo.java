package org.fit.vutbr.relaxdms.web.documents.tabs;

import java.io.Serializable;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;

/**
 *
 * @author Martin Kanis
 */
public class DocumentInfo extends Panel implements Serializable {
    
    @Inject
    private DocumentService documentService;

    public DocumentInfo(String id, String docId, String docRev) {
        super(id);
 
        DocumentMetadata metadata = documentService.getMetadataFromDoc(docId, docRev);
        
        add(new Label("id", metadata.getId()));
        add(new Label("rev", metadata.getRev()));
        add(new Label("schemaId", metadata.getSchemaId()));
        add(new Label("schemaRev", metadata.getSchemaRev()));
        add(new Label("author", metadata.getAuthor()));
        add(new Label("lastModifiedBy", metadata.getLastModifiedBy()));
        add(new Label("creationTime", metadata.getCreationDate()));
        add(new Label("lastModified", metadata.getLastModifiedDate()));
    }
}
