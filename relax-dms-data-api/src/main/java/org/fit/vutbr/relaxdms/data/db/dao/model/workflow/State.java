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

    public State() {
        currentState = StateEnum.OPEN;
        approval = ApprovalEnum.NONE;
    } 

    public StateEnum getState() {
        return currentState;
    }

    public void setState(StateEnum currentState) {
        this.currentState = currentState;
    }

    public ApprovalEnum getApproval() {
        return approval;
    }

    public void setApproval(ApprovalEnum approval) {
        this.approval = approval;
    }
}
