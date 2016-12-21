package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

/**
 *
 * @author Martin Kanis
 */
public enum ApprovalEnum implements Serializable {
    NONE("None"),
    APPROVED("Approved"),
    DECLINED("Declined");
    
    private final String name;
    
    private ApprovalEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
