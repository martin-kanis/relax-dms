package org.fit.vutbr.relaxdms.data.db.dao.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Martin Kanis
 */
@EqualsAndHashCode
public class DatabaseEntity implements Serializable {
    
    @Getter
    @Setter
    @JsonProperty("_id")
    private String id;
    
    @Getter
    @Setter
    @JsonProperty("_rev")
    private String rev;
}
