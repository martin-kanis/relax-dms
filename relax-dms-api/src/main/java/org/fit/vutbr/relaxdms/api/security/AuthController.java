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
package org.fit.vutbr.relaxdms.api.security;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Martin Kanis
 */
public interface AuthController {
    
    /**
     * Checks if there is a logged user in the application
     * @param req HttpServletRequest
     * @return True if there is a logged user, false otherwise
     */
    public boolean isLoggedIn(HttpServletRequest req);
    
    /**
     * Logout the current user from the application
     * @param req HttpServletRequest
     */
    public void logout(HttpServletRequest req);
    
    /**
     * Gets user's account URI from provided HTTP request.
     * @param req HttpServletRequest
     * @return User's account URI as string
     */
    public String getAccountURI(HttpServletRequest req);
    
    
    /**
     * Gets username from provided HTTP request.
     * @param req HttpServletRequest
     * @return username as string
     */
    public String getUserName(HttpServletRequest req);
    
    /**
     * Gets username from provided security context.
     * @param sc SecurityContext
     * @return username as string
     */
    public String getUserName(SecurityContext sc);
    
    /**
     * Returns all roles of currently logged user.
     * @param req HttpServletRequest
     * @return User's roles as set of strings
     */
    public Set<String> getUserRoles(HttpServletRequest req);
    
    /**
     * Checks if currently logged user is authorized to provided role. 
     * @param req HttpServletRequest
     * @param role Role that user should have
     * @return boolean
     */
    public boolean isUserAuthorized(HttpServletRequest req, String role);
    
    /**
     * Checks if user from security context is authorized to provided role.
     * @param sc SecurityContext
     * @param role String
     * @return True if user is authorized to provided role
     */
    public boolean isUserAuthorized(SecurityContext sc, String role);
}
