package org.fit.vutbr.relaxdms.data.client.keycloak.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import org.fit.vutbr.relaxdms.data.client.keycloak.api.KeycloakAdminClient;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class KeycloakAdminClientImpl implements KeycloakAdminClient {

    @Override
    public RealmResource connectToRealm() {
        Keycloak keycloak = Keycloak.getInstance(
            "http://localhost:8180/auth",
            "master",
            "admin",
            "admin",
            "admin-cli");
        return keycloak.realm("RelaxDMS");
    }
    
    @Override
    public Set<String> getUsers(String criteria) {
        RealmResource realm = connectToRealm();

        List<UserRepresentation> users = realm.users().search(criteria, 0, 10);
        return users.stream().map(u -> u.getUsername()).collect(Collectors.toSet());
    }  

    @Override
    public boolean userExists(String user) {
        RealmResource realm = connectToRealm();
        
        List<UserRepresentation> users = realm.users().search(user, 0, 100);
        Set<String> usersSet = users.stream().map(u -> u.getUsername()).collect(Collectors.toSet());

        return usersSet.contains(user);
    }
    
    @Override
    public List<String> getManagers() {
        RealmResource realm = connectToRealm();
        
        List<UserRepresentation> users = realm.groups().group("9454b12a-37e3-4919-a5f2-6cadbb25416b").members(0, 10);
        return users.stream().map(u -> u.getUsername()).collect(Collectors.toList());
    }
}
