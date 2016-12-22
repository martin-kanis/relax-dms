package org.fit.vutbr.relaxdms.web.documents.tabs;

import java.io.Serializable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
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
    
    private final Label approvedLabel;
    
    private final Label declinedLabel;
    
    private final Label noneLabel;
    
    private final Label statusLabel;
    
    private final Label approvalByLabel;
    
    private final Label approvalByValue;
    
    public DocumentWorkflow(String id, String docId) {
        super(id);
        this.id = docId;
        
        approvedLabel = new Label("approvedLabel", "Approved");
        declinedLabel = new Label("declinedLabel", "Declined");
        noneLabel = new Label("noneLabel", "None");
        statusLabel = new Label("statusLabel");
        approvalByLabel = new Label("approvalBy");
        approvalByValue = new Label("approvalByValue");
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        user = auth.getUserName(req);
        
        workflow = workflowService.getWorkflowFromDoc(docId);
        prepareApprovalComponents();
    }
    
    private void prepareApprovalComponents() {
        boolean approved = workflowService.isApproved(workflow);
        boolean declined = workflowService.isDeclined(workflow);
        
        createLabel(approvedLabel, approved);
        createLabel(declinedLabel, declined);
        createLabel(noneLabel, !approved && !declined);
        statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
        createLabel(statusLabel, true);
        
        String approvalLabel = "";
        if (approved)
            approvalLabel = "Approved by: ";
        if (declined)
            approvalLabel = "Declined by: ";
        
        approvalByLabel.setDefaultModel(new Model(approvalLabel));
        approvalByValue.setDefaultModel(new Model(workflow.getState().getApprovalBy()));
        createLabel(approvalByLabel, approved || declined);
        createLabel(approvalByValue, approved || declined);
        
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
                noneLabel.setVisible(false);
                approvalByLabel.setDefaultModel(new Model("Approved by: "));
                approvalByValue.setDefaultModel(new Model(user));
                target.add(approveLink, declineLink, approvedLabel, declinedLabel, 
                        noneLabel, approvalByValue, approvalByLabel);
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
                noneLabel.setVisible(false);
                approvalByLabel.setDefaultModel(new Model("Declined by: "));
                approvalByValue.setDefaultModel(new Model(user));
                target.add(approveLink, declineLink, declinedLabel, approvedLabel, 
                        noneLabel, approvalByValue, approvalByLabel);
            }
        };
        declineLink.setVisible(!visible);
        declineLink.setOutputMarkupPlaceholderTag(true);
        declineLink.setOutputMarkupId(true);
        add(declineLink);
    }
    
    private void createLabel(Label label, boolean visible) {
        label.setVisible(visible);
        label.setOutputMarkupPlaceholderTag(true);
        label.setOutputMarkupId(true);
        add(label);
    }
}