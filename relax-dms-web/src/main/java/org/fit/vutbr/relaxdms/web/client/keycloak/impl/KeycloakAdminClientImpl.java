package org.fit.vutbr.relaxdms.web.client.keycloak.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import org.fit.vutbr.relaxdms.web.client.keycloak.api.KeycloakAdminClient;
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
    public List<String> getUsers(String criteria) {
        RealmResource realm = connectToRealm();
        
        List<UserRepresentation> users = realm.users().search(criteria, 0, 10);
        return users.stream().map(u -> u.getUsername()).collect(Collectors.toList());
    }  

    @Override
    public boolean userExists(String user) {
        RealmResource realm = connectToRealm();
        
        List<UserRepresentation> users = realm.users().search(user, 0, 100);
        Set<String> usersSet = users.stream().map(u -> u.getUsername()).collect(Collectors.toSet());

        return usersSet.contains(user);
    }
}
