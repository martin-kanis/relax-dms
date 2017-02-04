package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import java.io.Serializable;

/**
 * Helper class to provide additional information to Drools engine.
 * @author Martin Kanis
 */
public class Environment implements Serializable {
    
    private boolean value;
    
    private String assignTo;
    
    private String fireBy;

    public Environment() {
        this.value = true;
    }

    public boolean isTrue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }   

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getFireBy() {
        return fireBy;
    }

    public void setFireBy(String fireBy) {
        this.fireBy = fireBy;
    }
}
