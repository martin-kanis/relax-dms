package org.fit.vutbr.relaxdms.data.db.dao.model;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Martin Kanis
 */
@EqualsAndHashCode
@ToString
public class Document extends DatabaseEntity implements Serializable {
    
    @Getter
    @Setter
    private String author;
}
