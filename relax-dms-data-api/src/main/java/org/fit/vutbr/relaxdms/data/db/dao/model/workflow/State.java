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
import lombok.ToString;

/**
 * Represents current currentState of the document.
 * @author Martin Kanis
 */
@ToString
public class State implements Serializable {
    
    @JsonProperty("currentState")
    private StateEnum currentState;
    
    @JsonProperty("approval")
    private ApprovalEnum approval;
    
    @JsonProperty("approvalBy")
    private String approvalBy;

    public State() {
        currentState = StateEnum.OPEN;
        approval = ApprovalEnum.NONE;
        approvalBy = null;
    } 

    public StateEnum getCurrentState() {
        return currentState;
    }

    public void setCurrentState(StateEnum currentState) {
        this.currentState = currentState;
    }

    public ApprovalEnum getApproval() {
        return approval;
    }

    public void setApproval(ApprovalEnum approval) {
        this.approval = approval;
    }

    public String getApprovalBy() {
        return approvalBy;
    }

    public void setApprovalBy(String approvalBy) {
        this.approvalBy = approvalBy;
    }
}
