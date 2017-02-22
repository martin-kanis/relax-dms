package org.fit.vutbr.relaxdms.data.db.dao.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

/**
 *
 * @author Martin Kanis
 */
@Data
public class DocumentListData implements Serializable {
    
    private String id;
    
    private String author;
    
    private String title;
    
    private Set<String> permissions;

    @JsonDeserialize(as=HashSet.class)
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
    
}
