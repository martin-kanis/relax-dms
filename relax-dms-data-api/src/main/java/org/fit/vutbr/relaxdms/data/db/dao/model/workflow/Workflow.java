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
