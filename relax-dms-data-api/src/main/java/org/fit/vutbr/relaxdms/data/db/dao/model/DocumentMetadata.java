package org.fit.vutbr.relaxdms.data.db.dao.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Martin Kanis
 */
@Data
public class DocumentMetadata implements Serializable {
    
    @JsonIgnore
    private String _id;
     
    @JsonIgnore
    private String _rev;
    
    private String schemaId;
    
    private String schemaRev;
    
    private String author;
    
    private String lastModifiedBy;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime creationDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;

    @JsonIgnore
    public String getId() {
        return _id;
    }

    @JsonProperty("_id")
    public void setId(String _id) {
        this._id = _id;
    }

    @JsonIgnore
    public String getRev() {
        return _rev;
    }

    @JsonProperty("_rev")
    public void setRev(String _rev) {
        this._rev = _rev;
    }
}
