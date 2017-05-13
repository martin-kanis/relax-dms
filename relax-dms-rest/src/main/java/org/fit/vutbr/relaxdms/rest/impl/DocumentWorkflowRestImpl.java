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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.client.keycloak.api.KeycloakAdminClient;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Environment;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.LabelEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;
import org.fit.vutbr.relaxdms.rest.api.DocumentWorkflowRest;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentWorkflowRestImpl implements DocumentWorkflowRest {

    @Inject
    private AuthController authController;
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private KeycloakAdminClient authClient;
    
    @Context
    private SecurityContext sc;
    
    @Override
    public Response approveDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "manager")) {
                if (workflowService.isSubmitted(doc.getWorkflow()) && !workflowService.isApproved(doc.getWorkflow())) {
                    workflowService.approveDoc(doc, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't approve document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't approve document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    } 
    
    @Override
    public Response declineDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "manager")) {
                if (workflowService.isSubmitted(doc.getWorkflow()) && !workflowService.isDeclined(doc.getWorkflow())) {
                    workflowService.declineDoc(doc, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't decline document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't decline document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response assignDoc(String docData, String assignee) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String docId = jsonNode.get("_id").asText();
            
            if (authController.isUserAuthorized(sc, "writer") &&
                    workflowService.isUserAuthorized(docId, authController.getUserName(sc))) {
                if (authClient.userExists(assignee)) {
                    workflowService.assignDocument(doc, assignee);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Provided user does not exist!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't assign document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response submitDoc(String docData, String manager) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String docId = jsonNode.get("_id").asText();
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "writer") &&
                    workflowService.isUserAuthorized(docId, user)) {
                if (authClient.getManagers().contains(manager)) {
                    if (workflowService.checkState(doc.getWorkflow(), StateEnum.OPEN) ||
                        workflowService.checkState(doc.getWorkflow(), StateEnum.IN_PROGRESS)) {
                        Environment env = new Environment();
                        env.setValue(true);
                        env.setAssignTo(manager);
                        env.setFireBy(user);
                        workflowService.submitDocument(doc, env);
                        return Response.ok().build();
                    } else {
                        return Response.status(403).entity("Forbidden: Can't sign document due wrong document's state!").build();
                    }
                } else {
                    return Response.status(403).entity("Forbidden: Provided user does not exist or is not manager!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't submit document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }  

    @Override
    public Response signDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "manager")) {
                if (workflowService.canBeSigned(doc, true)) {
                    workflowService.addLabel(doc, LabelEnum.SIGNED, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't sign document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't sign document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response freezeDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            StateEnum state = doc.getWorkflow().getState().getCurrentState();
            
            if (authController.isUserAuthorized(sc, "writer")) {
                if (state == StateEnum.OPEN || state == StateEnum.IN_PROGRESS && 
                        !workflowService.checkLabel(doc.getWorkflow(), LabelEnum.FREEZED)) {
                    workflowService.addLabel(doc, LabelEnum.FREEZED, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't freeze document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't freeze document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response releaseDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "writer")) {
                if (workflowService.checkLabel(doc.getWorkflow(), LabelEnum.SIGNED) &&
                    !workflowService.checkLabel(doc.getWorkflow(), LabelEnum.RELEASED)) {
                    workflowService.addLabel(doc, LabelEnum.RELEASED, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't release document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't release document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response startProgress(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "writer")) {
                if (workflowService.checkState(doc.getWorkflow(), StateEnum.OPEN)) {
                    workflowService.changeState(doc, StateEnum.IN_PROGRESS, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't start progress on document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't start progress on document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response closeDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "writer")) {
                if (!workflowService.checkState(doc.getWorkflow(), StateEnum.CLOSED) &&
                    !workflowService.checkLabel(doc.getWorkflow(), LabelEnum.RELEASED)) {
                    workflowService.changeState(doc, StateEnum.CLOSED, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't close document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't close document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response openDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String user = authController.getUserName(sc);
            
            if (authController.isUserAuthorized(sc, "writer")) {
                if (workflowService.checkState(doc.getWorkflow(), StateEnum.CLOSED)) {
                    workflowService.changeState(doc, StateEnum.OPEN, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't reopen document due wrong document's state!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't reopen document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response addPermissionsToDoc(String docData, String user) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String docId = jsonNode.get("_id").asText();
            
            if (authController.isUserAuthorized(sc, "writer") &&
                    workflowService.isUserAuthorized(docId, authController.getUserName(sc))) {
                if (authClient.userExists(user)) {
                    workflowService.addPermissionsToDoc(doc, user);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Provided user does not exist!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't add permissions to document due missing permissions!").build();
            }    
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }
    
    private Document jsonNodeToDocument(JsonNode jsonNode) {
        byte[] data = documentService.getDataFromJson(jsonNode);
        byte[] attachments = documentService.getAttachmentsFromJson(jsonNode);
        DocumentMetadata metadata = documentService.getMetadataFromJson(jsonNode);
        Workflow workflow = workflowService.getWorkflowFromJson(jsonNode);
        return new Document(data, attachments, metadata, workflow);
    }

    @Override
    public Response exportDoc(String docData) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(docData, JsonNode.class);
            Document doc = jsonNodeToDocument(jsonNode);
            String filename = jsonNode.get("data").get("Title").asText() + ".pdf";
            
            if (workflowService.checkLabel(doc.getWorkflow(), LabelEnum.RELEASED)) {
                ByteArrayOutputStream baos = documentService.convertToPdf(jsonNode);
                ResponseBuilder response = Response.ok(baos.toByteArray());
                response.type("application/pdf");
                response.header("Content-Disposition", "filename=" + filename);
                return response.build();
            } else {
                return Response.status(403).entity("Forbidden: Can't release document due wrong document's state!").build();
            }   
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
    }
}
