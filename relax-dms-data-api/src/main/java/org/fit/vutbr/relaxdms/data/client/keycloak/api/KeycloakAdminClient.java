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
package org.fit.vutbr.relaxdms.data.client.keycloak.api;

import java.util.List;
import java.util.Set;
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
     * @return Set of users that match given criteria
     */
    public Set<String> getUsers(String criteria);
    
    /**
     * Checks if given user exists in the realm.
     * @param user Username of user
     * @return True if user exists, false otherwise
     */
    public boolean userExists(String user);
    
    /**
     * Returns all users from managers group.
     * @return List of usernames with manager role.
     */
    public List<String> getManagers();
}
