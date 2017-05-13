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
