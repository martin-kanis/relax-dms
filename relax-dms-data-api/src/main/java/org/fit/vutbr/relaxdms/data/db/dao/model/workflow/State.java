package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Represents current currentState of the document.
 * @author Martin Kanis
 */
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
