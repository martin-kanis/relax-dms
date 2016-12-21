package org.fit.vutbr.relaxdms.web.documents.tabs;

import java.io.Serializable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;

/**
 *
 * @author Martin Kanis
 */
public class DocumentWorkflow extends Panel implements Serializable {

    private final String user;

    private final String id;
    
    private final Workflow workflow;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private AuthController auth;
    
    private AjaxLink approveLink;
    
    private AjaxLink declineLink;
    
    private Label approvedLabel;
    
    private Label declinedLabel;
    
    public DocumentWorkflow(String id, String docId) {
        super(id);
        this.id = docId;
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        user = auth.getUserName(req);
        
        workflow = workflowService.getWorkflowFromDoc(docId);
        prepareApprovalComponents();
    }
    
    private void prepareApprovalComponents() {
        boolean approved = workflowService.isApproved(workflow);
        boolean declined = workflowService.isDeclined(workflow);
        
        createApprovedLabel(approved);
        createDeclinedLabel(declined);
        
        createApproveButton(approved);     
        createDeclineButton(declined);
    }
    
    private void createApproveButton(boolean visible) {
        approveLink = new AjaxLink("approve") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                DocumentMetadata metadata = new DocumentMetadata();
                metadata.setLastModifiedBy(user);
                workflowService.approveDoc(id, new Document(metadata, workflow));

                approveLink.setVisible(false);
                declineLink.setVisible(true);
                approvedLabel.setVisible(true);
                declinedLabel.setVisible(false);
                target.add(approveLink, declineLink, approvedLabel, declinedLabel);
            }
        };
        approveLink.setVisible(!visible);
        approveLink.setOutputMarkupPlaceholderTag(true);
        approveLink.setOutputMarkupId(true);
        add(approveLink);
    }
    
    private void createDeclineButton(boolean visible) {
        declineLink = new AjaxLink("decline") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                DocumentMetadata metadata = new DocumentMetadata();
                metadata.setLastModifiedBy(user);
                workflowService.declineDoc(id, new Document(metadata, workflow));
                
                declineLink.setVisible(false);
                approveLink.setVisible(true);
                declinedLabel.setVisible(true);
                approvedLabel.setVisible(false);
                target.add(approveLink, declineLink, declinedLabel, approvedLabel);
            }
        };
        declineLink.setVisible(!visible);
        declineLink.setOutputMarkupPlaceholderTag(true);
        declineLink.setOutputMarkupId(true);
        add(declineLink);
    }
    
    private void createApprovedLabel(boolean visible) {
        approvedLabel = new Label("approvedLabel", "Document approved");
        approvedLabel.setVisible(visible);
        approvedLabel.setOutputMarkupPlaceholderTag(true);
        approvedLabel.setOutputMarkupId(true);
        add(approvedLabel);
    }
    
    private void createDeclinedLabel(boolean visible) {
        declinedLabel = new Label("declinedLabel", "Document declined");
        declinedLabel.setVisible(visible);
        declinedLabel.setOutputMarkupPlaceholderTag(true);
        declinedLabel.setOutputMarkupId(true);
        add(declinedLabel);
    }
}