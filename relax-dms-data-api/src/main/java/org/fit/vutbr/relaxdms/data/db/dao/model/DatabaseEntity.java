package org.fit.vutbr.relaxdms.data.db.dao.model;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 * @author Martin Kanis
 */
@EqualsAndHashCode
public class DatabaseEntity implements Serializable {
    
    @Getter
    private Long _id;
}
