/* 
 * The MIT License
 *
 * Copyright 2017 mkanis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
