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
