package org.fit.vutbr.relaxdms.data.db.dao.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;

/**
 *
 * @author Martin Kanis
 */
@Data
@AllArgsConstructor
public class Document implements Serializable {
    
    private DocumentMetadata metadata;
    
    private Workflow workflow;

    public Document(DocumentMetadata metadata) {
        this.metadata = metadata;
    }
}
