package org.fit.vutbr.relaxdms.api.security;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

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
     * Returns all roles of currently logged user.
     * @param req HttpServletRequest
     * @return User's roles as set of strings
     */
    public Set<String> getUserRoles(HttpServletRequest req);
    
    /**
     * Checks if currently logged user is authorized to admin role. 
     * @param req HttpServletRequest
     * @return boolean
     */
    public boolean isAdminAuthorized(HttpServletRequest req);
    
    /**
     * Checks if currently logged user is authorized to manager role. 
     * @param req HttpServletRequest
     * @return boolean
     */
    public boolean isManagerAuthorized(HttpServletRequest req);
}
