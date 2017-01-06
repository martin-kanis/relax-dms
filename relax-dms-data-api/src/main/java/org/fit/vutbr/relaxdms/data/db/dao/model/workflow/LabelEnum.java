package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

/**
 *
 * @author Martin Kanis
 */
public enum LabelEnum implements Serializable {
    SIGNED("Signed", "Document is signed by signing authority"),
    APPROVED("Approved", "Document is approved by approval authority"),
    SUBMITED("Submited", "Document is sent to admin for review"),
    FREEZED("Freezed", "Document is freezed for later finishing");
    
    private final String name;
    
    private final String description;

    private LabelEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
