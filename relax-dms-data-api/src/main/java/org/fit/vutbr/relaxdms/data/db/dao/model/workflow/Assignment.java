package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.ToString;

/**
 *
 * @author Martin Kanis
 */
@ToString
public class Assignment implements Serializable {
    
    @JsonProperty("assignee")
    private String assignee;
    
    @JsonProperty("history")
    private List<String> history;

    public Assignment() {
        assignee = "Unassigned";
        history = new ArrayList<>();
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
        
        if (history.isEmpty() || !assignee.equals(history.get(history.size() - 1)))
            history.add(assignee);
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
