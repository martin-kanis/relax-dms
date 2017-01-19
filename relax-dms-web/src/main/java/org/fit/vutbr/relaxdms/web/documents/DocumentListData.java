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
public class DocumentListData implements Serializable {
    
    private final String id;
    
    private final String title;
    
    private final String author; 
}
