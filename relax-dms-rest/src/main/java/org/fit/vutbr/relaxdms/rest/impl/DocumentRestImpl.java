package org.fit.vutbr.relaxdms.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;
import org.fit.vutbr.relaxdms.rest.api.DocumentRest;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class DocumentRestImpl implements DocumentRest {
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private AuthController authController;
    
    @Context
    private SecurityContext sc;
    
    @Inject 
    private Convert convert;

    @Override
    public List<String> getAllDocIds() {
        return documentService.getAllDocIds();
    }

    @Override
    public String getCurrentRevision(String id) {
        // create valid JSON string by adding ""
        return "\"" + documentService.getCurrentRevision(id) + "\"";
    };

    @Override
    public List<String> getRevisions(String id) {
        return convert.revisionToString(documentService.getRevisions(id));
    }

    @Override
    public Response read(String id) {
        if (workflowService.isUserAuthorized(id, authController.getUserName(sc))) {
            JsonNode doc = documentService.getDocumentById(id);
            return Response.ok(doc).build();
        } else {
            return Response.status(403).entity("Can't read document due missing permissions!").build();
        }
    }

    @Override
    // secured by security-constraint in web.xml in relax-dms-web module
    public Response create(String json) {
        String author = authController.getUserName(sc);
        List<JsonNode> templates = documentService.getAllTemplates();
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(json, JsonNode.class);
            byte[] data = documentService.getDataFromJson(jsonNode);
            byte[] attachments = documentService.getAttachmentsFromJson(jsonNode);
            
            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setAuthor(author);
            metadata.setLastModifiedBy(author);
            
            List<JsonNode> matchedTemplates = templates.stream().filter((template) -> 
                    (documentService.validateJsonDataWithSchema(jsonNode, template))).collect(Collectors.toList());
            
            if (matchedTemplates.isEmpty()) {
                return Response.status(400).entity("Provided JSON doesn't match any available template!").build();
            }
            
            JsonNode schema = matchedTemplates.get(0);
            String id = schema.get("_id").asText();
            String rev = schema.get("_rev").asText();
            metadata.setSchemaId(id);
            metadata.setSchemaRev(rev);
            
            Document docData = new Document(data, attachments, metadata, new Workflow());
            documentService.storeDocument(jsonNode, docData);
        } catch (IOException ex) {
            return Response.status(400).entity("Jackson error: Could not serialize provided object!").build();
        }
        
        return Response.status(201).entity("Created").build();
    }

    @Override
    public Response delete(String json) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(json, JsonNode.class);
            
            String docId = jsonNode.get("_id").asText();
            // doc is not read-only
            if (!workflowService.isReadOnly(docId)) {
                // user is authorized to delete document
                if (authController.isUserAuthorized(sc, "writer") && 
                        workflowService.isUserAuthorized(docId, authController.getUserName(sc))) {
                    documentService.deleteDocument(jsonNode);
                    return Response.ok().build();
                } else {
                    return Response.status(403).entity("Forbidden: Can't delete document due missing permissions!").build();
                }
            }
        } catch (IOException ex) {
            return Response.status(500).entity("Jackson error: Could not serialize provided object!").build();
        } catch (Exception ex) {
            return Response.status(404).entity(ex.getMessage()).build();
        }
        
        return Response.status(403).entity("Forbidden: Can't delete document due its state!").build();
    }

    @Override
    public Response update(String json) {
        try {
            JsonNode jsonNode = new ObjectMapper().readValue(json, JsonNode.class);
            byte[] data = documentService.getDataFromJson(jsonNode);
            byte[] attachments = documentService.getAttachmentsFromJson(jsonNode);
            DocumentMetadata metadata = documentService.getMetadataFromJson(jsonNode);
            Workflow workflow = workflowService.getWorkflowFromJson(jsonNode);
            
            String docId = jsonNode.get("_id").asText();
            
            byte[] currentData = documentService.getDataFromDoc(docId);
            
            // user is authorized to edit document
            if (authController.isUserAuthorized(sc, "writer") && 
                    workflowService.isUserAuthorized(docId, authController.getUserName(sc))) {
                // if the document is read-only and we want to change data of document, return error
                if (workflowService.isReadOnly(docId)) {
                    if (!Arrays.equals(currentData, data)) {
                        return Response.status(403).entity("Forbidden: Can't update document due its state!").build();
                    }
                }
                
                JsonNode diff = documentService.updateDocument(new Document(data, attachments, metadata, workflow));
                // no diff, update was succesfull
                if (diff.isNull()) {
                    return Response.status(200).entity("Updated").build();
                } else {
                    return Response.status(409).entity("Conflict: Document has changed!").build();
                }
            } else {
                return Response.status(403).entity("Forbidden: Can't delete document due missing permissions!").build();
            }
        } catch (IOException ex) {
            return Response.status(500).entity("Jackson error: Could not serialize provided object!").build();
        }
    }

    @Override
    public Response getAllTemplates() {
        if (authController.isUserAuthorized(sc, "writer")) {
            return Response.ok(documentService.getAllTemplates()).build();
        }
        return Response.status(403).entity("Forbidden: Can't see templates due missing permissions!").build();
    }

    @Override
    public Response getDocsHeadersByAuthor() {
        String author = authController.getUserName(sc);
        return Response.ok(documentService.getDocumentsByAuthor(author)).build();
    }

    @Override
    public Response getDocsHeadersByAssignee() {
        String assignee = authController.getUserName(sc);
        return Response.ok(documentService.getDocumentsByAssignee(assignee)).build();
    }
}
