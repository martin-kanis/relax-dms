package org.fit.vutbr.relaxdms.data.db.dao.model.workflow;

import java.io.Serializable;

/**
 *
 * @author Martin Kanis
 */
public class Flag implements Serializable {
    
    private boolean value;

    public Flag(boolean value) {
        this.value = value;
    }

    public boolean isTrue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }        
}
