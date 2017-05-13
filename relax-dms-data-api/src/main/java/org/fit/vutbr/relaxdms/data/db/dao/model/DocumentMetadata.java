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
