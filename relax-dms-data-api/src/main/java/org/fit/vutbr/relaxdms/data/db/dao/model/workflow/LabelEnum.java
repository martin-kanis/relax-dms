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

import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

/**
 *
 * @author Martin Kanis
 */
public enum LabelEnum implements Serializable {
    RELEASED("Released", "Document is released to available to readers"),
    SIGNED("Signed", "Document is signed by signing authority"),
    APPROVED("Approved", "Document is approved by approval authority"),
    SUBMITTED("Submitted", "Document is sent to admin for review"),
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
