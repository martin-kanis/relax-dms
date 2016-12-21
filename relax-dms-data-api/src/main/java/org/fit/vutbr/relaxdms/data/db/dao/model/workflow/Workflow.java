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

    public Workflow() {
        this.state = new State();
    }

    @JsonIgnore
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
