package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Represents workflow stored in CouchDb document. This class is used as fact for 
 * Drools engine.
 * @author Martin Kanis
 */
public class Workflow implements Serializable {
    
    @JsonProperty("state")
    private State state;
    
    @JsonProperty("assignment")
    private Assignment assignment;

    public Workflow() {
        this.state = new State();
        this.assignment = new Assignment();
    }

    @JsonIgnore
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @JsonIgnore
    public Assignment getAssigment() {
        return assignment;
    }

    public void setAssigment(Assignment assignment) {
        this.assignment = assignment;
    }
}
