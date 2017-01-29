package org.fit.vutbr.relaxdms.backend.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.AccessToken;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class AuthControllerImpl implements AuthController {
    
    public static final Set<String> adminRoles = new HashSet<>(Arrays.asList("app-admin", "writer", "reader"));
    
    public static final Set<String> managerRoles = new HashSet<>(Arrays.asList("manager", "writer", "reader"));
    
    public static final Set<String> writerRoles = new HashSet<>(Arrays.asList("writer", "reader"));
    
    public static final Set<String> readerRoles = new HashSet<>(Arrays.asList("reader"));
    
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public boolean isLoggedIn(HttpServletRequest req) {
        return getSession(req) != null;
    } 
    
    private KeycloakSecurityContext getSession(HttpServletRequest req) {
        return (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    }
    
    private String getAuthServerBaseUrl(HttpServletRequest req) {
        AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) req.getServletContext().getAttribute(AdapterDeploymentContext.class.getName());
        KeycloakDeployment deployment = deploymentContext.resolveDeployment(null);
        return deployment.getAuthServerBaseUrl();
    }

    @Override
    public void logout(HttpServletRequest req) {
        try {
            req.logout();
        } catch (ServletException ex) {
            logger.error(ex);
        }
    }

    @Override
    public String getAccountURI(HttpServletRequest req) {
        KeycloakSecurityContext session = getSession(req);
        String baseUrl = getAuthServerBaseUrl(req);
        String realm = session.getRealm();
        return KeycloakUriBuilder.fromUri(baseUrl).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", "relax-dms").build(realm).toString();
    }

    private AccessToken getToken(HttpServletRequest req) {
        return getSession(req).getToken();
    }

    @Override
    public String getUserName(HttpServletRequest req) {
        return getToken(req).getPreferredUsername();
    }

    @Override
    public Set<String> getUserRoles(HttpServletRequest req) {
        return getToken(req).getRealmAccess().getRoles();
    }

    @Override
    public boolean isUserAuthorized(HttpServletRequest req, String role) {
        Set<String> roles = getUserRoles(req);

        String neededRole;
        switch (role) {
            case "admin":
                neededRole = "app-admin";
                break;
            case "manager":
                neededRole = role;
                break;
            case "writer":
                neededRole = role;
                break;
            case "reader":
                neededRole = role;
                break;
            default:
                neededRole = "";
        }
        
        return roles.contains(neededRole);
    }
 }
