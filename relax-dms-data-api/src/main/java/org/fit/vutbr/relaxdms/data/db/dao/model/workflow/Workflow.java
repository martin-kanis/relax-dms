package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.ToString;

/**
 * Represents workflow stored in CouchDb document. This class is used as fact for 
 * Drools engine.
 * @author Martin Kanis
 */
@ToString
public class Workflow implements Serializable {
    
    @JsonProperty("state")
    private State state;
    
    @JsonProperty("assignment")
    private Assignment assignment;
    
    @JsonProperty("labels")
    private Set<Label> labels;
    
    @JsonProperty("permissions")
    private Set<String> permissions;

    public Workflow() {
        this.state = new State();
        this.assignment = new Assignment();
        this.labels = new HashSet<>();
        this.permissions = new HashSet<>();
    }

    @JsonIgnore
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @JsonIgnore
    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    @JsonIgnore
    public Set<Label> getLabels() {
        return labels;
    }

    @JsonDeserialize(as=HashSet.class)
    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    @JsonIgnore
    public Set<String> getPermissions() {
        return permissions;
    }

    @JsonDeserialize(as=HashSet.class)
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
