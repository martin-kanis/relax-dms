package org.fit.vutbr.relaxdms.data.db.dao.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Martin Kanis
 */
@Data
public class DocumentMetadata implements Serializable {
    
    private String _id;
    
    private String _rev;
    
    private String schemaId;
    
    private String schemaRev;
    
    private String author;
    
    private String lastModifiedBy;
    
    private LocalDateTime creationDate;
    
    private LocalDateTime lastModifiedDate;
}
