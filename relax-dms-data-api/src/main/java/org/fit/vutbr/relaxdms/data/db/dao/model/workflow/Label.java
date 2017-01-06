package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Objects;
import lombok.ToString;

/**
 *
 * @author Martin Kanis
 */
@ToString
public class Label implements Serializable {
    
    @JsonProperty("labelType")
    private LabelEnum labelType;
    
    @JsonProperty("labeledBy")
    private String labeledBy;

    public Label(LabelEnum labelType) {
        this.labelType = labelType;
    }
    
    public Label(@JsonProperty("labelType") LabelEnum labelType, 
            @JsonProperty("labeledBy") String labeledBy) {
        this.labelType = labelType;
        this.labeledBy = labeledBy;
    }

    public LabelEnum getLabelType() {
        return labelType;
    }

    public void setLabelType(LabelEnum labelType) {
        this.labelType = labelType;
    }

    public String getLabeledBy() {
        return labeledBy;
    }

    public void setLabeledBy(String labeledBy) {
        this.labeledBy = labeledBy;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.labelType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Label other = (Label) obj;
        return this.labelType == other.labelType;
    }
}
