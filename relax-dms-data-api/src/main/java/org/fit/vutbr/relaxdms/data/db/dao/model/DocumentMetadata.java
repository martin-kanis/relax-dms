package org.fit.vutbr.relaxdms.data.db.dao.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Martin Kanis
 */
@Data
@AllArgsConstructor
public class DocumentMetadata implements Serializable {
    private String author;
    
    private String lastModifiedBy;
    
    private LocalDateTime creationDate;
    
    private LocalDateTime lastModifiedDate;
}
