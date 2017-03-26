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
