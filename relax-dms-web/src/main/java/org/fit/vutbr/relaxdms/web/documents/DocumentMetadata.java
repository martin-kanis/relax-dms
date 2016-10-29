package org.fit.vutbr.relaxdms.web.documents;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Martin Kanis
 */
@Data
@AllArgsConstructor
public class DocumentMetadata implements Serializable {
    
    private String id;
    
    private String rev;
    
    private String schemaId;
    
    private String schemaRev;        
}
