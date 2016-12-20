package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Represents workflow stored in CouchDb document. This class is used as fact for 
 * Drools engine.
 * @author Martin Kanis
 */
public class Workflow implements Serializable {
    
    @JsonProperty("approved")
    private boolean isApproved;
    
    @JsonProperty("declined")
    private boolean idDeclined;

    public boolean isIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean isIdDeclined() {
        return idDeclined;
    }

    public void setIdDeclined(boolean idDeclined) {
        this.idDeclined = idDeclined;
    } 
}
