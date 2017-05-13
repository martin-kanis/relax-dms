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

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;

/**
 *
 * @author Martin Kanis
 */
@ToString
@AllArgsConstructor
public class Document implements Serializable {
    
    private byte[] data;
    
    private byte[] attachments;
    
    private DocumentMetadata metadata;
    
    private Workflow workflow;

    public Document(DocumentMetadata metadata) {
        this.metadata = metadata;
    }

    public DocumentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DocumentMetadata metadata) {
        this.metadata = metadata;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getAttachments() {
        return attachments;
    }

    public void setAttachments(byte[] attachments) {
        this.attachments = attachments;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.metadata.get_id());
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
        final Document other = (Document) obj;
        
        return this.metadata.get_id().equals(other.metadata.get_id());        
    }
}
