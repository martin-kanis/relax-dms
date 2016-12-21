package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

/**
 *
 * @author Martin Kanis
 */
public enum StateEnum implements Serializable {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    SUBMITED("Submited"),
    CLOSED("Closed");
    
    private final String name;
    
    private StateEnum(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
