package org.fit.vutbr.relaxdms.web.documents.tabs;

import java.io.Serializable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;
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
    
    private final boolean isAdmin;
    
    private AjaxLink approveLink;
    
    private AjaxLink declineLink;
    
    private AjaxLink submitLink;
    
    private AjaxLink startProgressLink;
    
    private AjaxLink closeLink;
    
    private AjaxLink reopenLink;
    
    private final Label approvedLabel;
    
    private final Label declinedLabel;
    
    private final Label noneLabel;
    
    private final Label statusLabel;
    
    private final Label approvalByLabel;
    
    private final Label approvalByValue;
    
    private Document docData;
    
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
        
        isAdmin = auth.isAdminAuthorized(req);
        
        workflow = workflowService.getWorkflowFromDoc(docId);

        DocumentMetadata metadata = new DocumentMetadata();
        metadata.setLastModifiedBy(user);
        docData = new Document(metadata, workflow);
        
        prepareComonents();
        prepareApprovalComponents();
    }
    
    private void prepareApprovalComponents() {
        boolean approved = workflowService.isApproved(workflow);
        boolean declined = workflowService.isDeclined(workflow);
        
        createLabel(approvedLabel, approved);
        createLabel(declinedLabel, declined);
        createLabel(noneLabel, !approved && !declined);

        String approvalLabel = "";
        if (approved)
            approvalLabel = "Approved by: ";
        if (declined)
            approvalLabel = "Declined by: ";
        
        approvalByLabel.setDefaultModel(new Model(approvalLabel));
        approvalByValue.setDefaultModel(new Model(workflow.getState().getApprovalBy()));
        createLabel(approvalByLabel, approved || declined);
        createLabel(approvalByValue, approved || declined);
        
        // approval buttons are visible if current user is admin and document is in submited status
        boolean approvalVisible = isAdmin && workflowService.checkState(workflow, StateEnum.SUBMITED);
        createApproveButton(approvalVisible && !approved);     
        createDeclineButton(approvalVisible && !declined);
    }
    
    private void prepareComonents() {
        statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
        createLabel(statusLabel, true);
        
        boolean submitVisible = workflowService.checkState(workflow, StateEnum.OPEN) ||
                workflowService.checkState(workflow, StateEnum.IN_PROGRESS);
        createSubmitButton(submitVisible);
        
        boolean startProgressVisible = workflowService.checkState(workflow, StateEnum.OPEN);
        createStartProgressButton(startProgressVisible);
        
        boolean closeVisible = !workflowService.checkState(workflow, StateEnum.CLOSED);
        createCloseButton(closeVisible);
        
        boolean reopenVisible = workflowService.checkState(workflow, StateEnum.CLOSED);
        createReopenButton(reopenVisible);
    }
    
    private void createApproveButton(boolean visible) {
        approveLink = new AjaxLink("approve") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.approveDoc(id, docData);

                setVisibility(false, approveLink, declinedLabel, noneLabel);
                setVisibility(true, approvedLabel, closeLink);
                
                // set approval by
                approvalByLabel.setDefaultModel(new Model("Approved by: "));
                approvalByValue.setDefaultModel(new Model(user));
                
                // change state
                workflowService.changeState(id, docData, StateEnum.DONE);
                statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                target.add(approveLink, approvedLabel, declinedLabel, closeLink,
                        noneLabel, approvalByValue, approvalByLabel, statusLabel);
            }
        };
        setVisibility(approveLink, visible);
    }
    
    private void createDeclineButton(boolean visible) {
        declineLink = new AjaxLink("decline") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.declineDoc(id, docData);
                
                setVisibility(false, approvedLabel, noneLabel, declineLink);
                setVisibility(true, submitLink, declinedLabel);
                
                // set approval by
                approvalByLabel.setDefaultModel(new Model("Declined by: "));
                approvalByValue.setDefaultModel(new Model(user));
                
                // change state to In Progress when decline document
                workflowService.changeState(id, docData, StateEnum.IN_PROGRESS);
                statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                target.add(submitLink, declineLink, declinedLabel, approvedLabel, 
                        noneLabel, approvalByValue, approvalByLabel, statusLabel);
            }
        };
        setVisibility(declineLink, visible);
    }
    
    private void createSubmitButton(boolean visible) {
        submitLink = new AjaxLink("submit") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(id, docData, StateEnum.SUBMITED);
                statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                setVisibility(false, submitLink, startProgressLink);
                
                boolean approvalVisible = isAdmin && workflowService.checkState(workflow, StateEnum.SUBMITED);
                approveLink.setVisible(approvalVisible);
                declineLink.setVisible(approvalVisible);

                target.add(statusLabel, submitLink, startProgressLink, approveLink, declineLink);
            }
        };
        setVisibility(submitLink, visible);
    }
    
    private void createStartProgressButton(boolean visible) {
        startProgressLink = new AjaxLink("startProgress") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(id, docData, StateEnum.IN_PROGRESS);
                statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                startProgressLink.setVisible(false);
                
                target.add(statusLabel, startProgressLink);
            }
        };
        setVisibility(startProgressLink, visible);
    }
    
    private void createCloseButton(boolean visible) {
        closeLink = new AjaxLink("close") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(id, docData, StateEnum.CLOSED);
                statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                setVisibility(false, closeLink, submitLink, startProgressLink, approveLink, declineLink);
                reopenLink.setVisible(true);
                
                target.add(statusLabel, closeLink, submitLink, startProgressLink,
                        reopenLink, approveLink, declineLink);
            }
        };
        setVisibility(closeLink, visible);
    }
    
    private void createReopenButton(boolean visible) {
        reopenLink = new AjaxLink("reopen") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(id, docData, StateEnum.OPEN);
                statusLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                setVisibility(true, closeLink, submitLink, startProgressLink);
                reopenLink.setVisible(false);
                
                target.add(statusLabel, closeLink, submitLink, startProgressLink, reopenLink);
            }
        };
        setVisibility(reopenLink, visible);
    }
    
    private void createLabel(Label label, boolean visible) {
        label.setVisible(visible);
        label.setOutputMarkupPlaceholderTag(true);
        label.setOutputMarkupId(true);
        add(label);
    }
    
    private void setVisibility(Component c, boolean visible) {
        c.setOutputMarkupId(true);
        c.setOutputMarkupPlaceholderTag(true);
        c.setVisible(visible);
        add(c);
    }
    
    private void setVisibility(boolean visible, Component... components) {
        for (Component c: components)
            c.setVisible(visible);
    }
}