package org.fit.vutbr.relaxdms.web.client.keycloak.api;

import java.util.List;
import org.keycloak.admin.client.resource.RealmResource;

/**
 *
 * @author Martin Kanis
 */
public interface KeycloakAdminClient {
    
    /**
     * Connects to RelaxDMS realm.
     * @return RealmResource
     */
    public RealmResource connectToRealm();
    
    /**
     * Returns first 10 users from realm that match given username criteria. 
     * @param criteria Username to be matched
     * @return List of users that match given criteria
     */
    public List<String> getUsers(String criteria);
    
    /**
     * Checks if given user exists in the realm.
     * @param user Username of user
     * @return True if user exists, false otherwise
     */
    public boolean userExists(String user);
}
