package org.fit.vutbr.relaxdms.rest.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Environment;
import org.fit.vutbr.relaxdms.rest.api.WorkflowRest;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class WorkflowRestImpl implements WorkflowRest {

    @Inject
    private AuthController authController;
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private WorkflowService workflowService;
    
    @Context
    private SecurityContext sc;
    
    @Override
    public Response fireUnfreezeWorkflow() {
        if (authController.isUserAuthorized(sc, "manager")) {
            Environment env = new Environment();
            env.setValue(true);
            workflowService.insertAllFacts(documentService.getAllDocumentsMetadata(), env);
            return Response.ok().build();  
        } else {
            return Response.status(403).entity("Forbidden: Can't fire unfreeze workflow due missing permissions!").build();
        }
    }

    @Override
    public Response fireReleaseWorkflow() {
        if (authController.isUserAuthorized(sc, "manager")) {
            Environment env = new Environment();
            env.setValue(true);
            env.setFireBy(authController.getUserName(sc));
            workflowService.insertAllFacts(documentService.getAllDocumentsMetadata(), env);
            return Response.ok().build();  
        } else {
            return Response.status(403).entity("Forbidden: Can't fire release workflow due missing permissions!").build();
        }
    }

    @Override
    public Response getCustomRules() {
        if (authController.isUserAuthorized(sc, "manager")) {
            return Response.ok(workflowService.getCustomRules()).build();  
        } else {
            return Response.status(403).entity("Forbidden: Can't get custom rules due missing permissions!").build();
        }
    }

    @Override
    public Response fireCustomWorkflow(String rule) {
        if (authController.isUserAuthorized(sc, "manager")) {
            if (workflowService.getCustomRules().contains(rule)) {
                Environment env = new Environment();
                env.setValue(true);
                env.setFireBy(authController.getUserName(sc));
                env.setRule(rule);
                workflowService.insertAllFacts(documentService.getAllDocumentsMetadata(), env);
                return Response.ok().build();
            } else {
                return Response.status(400).entity("Provided rule does not exist!").build();
            }
        } else {
            return Response.status(403).entity("Forbidden: Can't get custom rules due missing permissions!").build();
        }
    }
}
