package org.fit.vutbr.relaxdms.web.documents.tabs.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.LabelEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;
import org.fit.vutbr.relaxdms.web.documents.tabs.DocumentTabs;

/**
 *
 * @author Martin Kanis
 */
public class DocumentWorkflow extends Panel implements Serializable {

    private final String user;
    
    private final Workflow workflow;
    
    private final DocumentMetadata metadata;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private AuthController auth;
    
    private final boolean isAdmin;
    
    private final boolean isManager;
    
    private AjaxLink approveLink;
    
    private AjaxLink declineLink;
    
    private AjaxLink submitLink;
    
    private AjaxLink startProgressLink;
    
    private AjaxLink closeLink;
    
    private AjaxLink reopenLink;
    
    private AjaxLink signLink;
    
    private AjaxLink freezeLink;
    
    private final Label approvedLabel;
    
    private final Label declinedLabel;
    
    private final Label noneLabel;
    
    private final Label stateLabel;
    
    private final Label approvalByLabel;
    
    private final Label approvalByValue;
    
    private final AjaxEditableLabel assigneeLabel;
    
    private final DocumentLabels documentLabels;
    
    private final DocumentTabs tabs;
    
    private final Document docData;
    
    public DocumentWorkflow(String id, String docId, String docRev, DocumentTabs tabs) {
        super(id);
        this.tabs = tabs;
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        user = auth.getUserName(req);
        
        isAdmin = auth.isAdminAuthorized(req);
        isManager = auth.isManagerAuthorized(req);
        
        JsonNode doc;
        if (docRev == null) {
            doc = documentService.getDocumentById(docId);
        } else {
            doc = documentService.getDocumentByIdAndRev(docId, docRev);
        }
        byte[] data = documentService.getDataFromJson(doc);
        byte[] attachments = documentService.getAttachmentsFromJson(doc);
        workflow = workflowService.getWorkflowFromJson(doc);
        metadata = documentService.getMetadataFromJson(doc);
        metadata.setLastModifiedBy(user);
        docData = new Document(data, attachments, metadata, workflow);
        
        approvedLabel = new Label("approvedLabel", "Approved");
        declinedLabel = new Label("declinedLabel", "Declined");
        noneLabel = new Label("noneLabel", "None");
        stateLabel = new Label("stateLabel");
        approvalByLabel = new Label("approvalBy");
        approvalByValue = new Label("approvalByValue");

        assigneeLabel = new AssigneeLabel("assignee", docId, docData, tabs);

        prepareComonents();
        prepareApprovalComponents();
        
        documentLabels = new DocumentLabels("labels", docData);
        documentLabels.setOutputMarkupId(true);
        add(documentLabels);
        
        // disable components if we have older version
        if (docRev != null && !docRev.equals(documentService.getCurrentRevision(docId))) {
            disableComponents(approveLink, declineLink, submitLink, startProgressLink, 
                    closeLink, reopenLink, signLink, freezeLink, assigneeLabel);
        }
    }
    
    private void prepareApprovalComponents() {
        boolean approved = workflowService.isApproved(workflow);
        boolean declined = workflowService.isDeclined(workflow);
        
        addComponent(approvedLabel, approved);
        addComponent(declinedLabel, declined);
        addComponent(noneLabel, !approved && !declined);

        String approvalLabel = "";
        if (approved)
            approvalLabel = "Approved by: ";
        if (declined)
            approvalLabel = "Declined by: ";
        
        approvalByLabel.setDefaultModel(new Model(approvalLabel));
        approvalByValue.setDefaultModel(new Model(workflow.getState().getApprovalBy()));
        addComponent(approvalByLabel, approved || declined);
        addComponent(approvalByValue, approved || declined);
        
        // approval buttons are visible if current user is admin and document is in submited state
        boolean approvalVisible = isAdmin && workflowService.checkState(workflow, StateEnum.SUBMITED);
        createApproveButton(approvalVisible && !approved);     
        createDeclineButton(approvalVisible && !declined);
    }
    
    private void prepareComonents() {
        stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
        addComponent(stateLabel, true);

        addComponent(assigneeLabel, true);
        
        boolean submitVisible = workflowService.checkState(workflow, StateEnum.OPEN) ||
                workflowService.checkState(workflow, StateEnum.IN_PROGRESS);
        createSubmitButton(submitVisible);
        
        boolean startProgressVisible = workflowService.checkState(workflow, StateEnum.OPEN);
        createStartProgressButton(startProgressVisible);
        
        boolean closeVisible = !workflowService.checkState(workflow, StateEnum.CLOSED);
        createCloseButton(closeVisible);
        
        boolean reopenVisible = workflowService.checkState(workflow, StateEnum.CLOSED);
        createReopenButton(reopenVisible);
        
        boolean signVisible = workflowService.canBeSigned(docData, isManager);
        createSignButton(signVisible);
        
        boolean freezeVisible = (workflowService.checkState(workflow, StateEnum.OPEN) ||
                workflowService.checkState(workflow, StateEnum.IN_PROGRESS)) && 
                !workflowService.checkLabel(workflow, LabelEnum.FREEZED);
        createFreezeButton(freezeVisible);
    }
    
    private void createApproveButton(boolean visible) {
        approveLink = new AjaxLink("approve") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.approveDoc(docData);

                setVisibility(false, approveLink, declineLink, declinedLabel, noneLabel,
                        documentLabels.getSubmitedlabel());
                setVisibility(true, approvedLabel, closeLink, approvalByLabel, approvalByValue,
                        documentLabels.getApprovedlabel());
                
                if (workflowService.canBeSigned(docData, isManager)) {
                    setVisibility(true, signLink);
                }
                
                // set approval by
                approvalByLabel.setDefaultModel(new Model("Approved by: "));
                approvalByValue.setDefaultModel(new Model(user));
                
                // update state label and assignee label
                stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                assigneeLabel.setDefaultModel(new Model(workflow.getAssignment().getAssignee()));
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(approveLink, declineLink, approvedLabel, declinedLabel, closeLink,
                        noneLabel, approvalByValue, approvalByLabel, stateLabel, assigneeLabel, 
                        documentLabels, signLink, tabs);
            }
        };
        addComponent(approveLink, visible);
    }
    
    private void createDeclineButton(boolean visible) {
        declineLink = new AjaxLink("decline") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.declineDoc(docData);
                
                setVisibility(false, approvedLabel, noneLabel, declineLink, approveLink,
                        documentLabels.getSubmitedlabel());
                setVisibility(true, submitLink, declinedLabel, approvalByLabel,
                        approvalByValue, startProgressLink);
                
                // set approval by
                approvalByLabel.setDefaultModel(new Model("Declined by: "));
                approvalByValue.setDefaultModel(new Model(user));
                
                // update state label and assignee label
                stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                assigneeLabel.setDefaultModel(new Model(workflow.getAssignment().getAssignee()));
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(submitLink, declineLink, approveLink, declinedLabel, tabs,
                        approvedLabel, noneLabel, approvalByValue, approvalByLabel, 
                        stateLabel, startProgressLink, assigneeLabel, documentLabels);
            }
        };
        addComponent(declineLink, visible);
    }
    
    private void createSubmitButton(boolean visible) {
        submitLink = new AjaxLink("submit") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(docData, StateEnum.SUBMITED);

                stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                assigneeLabel.setDefaultModel(new Model(workflow.getAssignment().getAssignee()));
                
                setVisibility(false, submitLink, startProgressLink, approvedLabel,
                        declinedLabel, approvalByLabel, approvalByValue, 
                        documentLabels.getApprovedlabel(), documentLabels.getFreezedLabel(),
                        freezeLink);
                setVisibility(true, noneLabel, documentLabels.getSubmitedlabel());
                
                boolean approvalVisible = isAdmin && workflowService.checkState(workflow, StateEnum.SUBMITED);
                approveLink.setVisible(approvalVisible);
                declineLink.setVisible(approvalVisible);

                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(stateLabel, assigneeLabel, submitLink, startProgressLink, 
                        approveLink, declineLink, approvedLabel, declinedLabel, 
                        approvalByLabel, approvalByValue, noneLabel, documentLabels,
                        freezeLink, tabs);
            }
        };
        addComponent(submitLink, visible);
    }
    
    private void createStartProgressButton(boolean visible) {
        startProgressLink = new AjaxLink("startProgress") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(docData, StateEnum.IN_PROGRESS);
                
                stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                setVisibility(false, startProgressLink, documentLabels.getFreezedLabel());
                setVisibility(true, freezeLink);
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(stateLabel, startProgressLink, documentLabels, freezeLink, tabs);
            }
        };
        addComponent(startProgressLink, visible);
    }
    
    private void createCloseButton(boolean visible) {
        closeLink = new AjaxLink("close") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(docData, StateEnum.CLOSED);

                stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                setVisibility(false, closeLink, submitLink, startProgressLink, 
                        approveLink, declineLink, documentLabels.getSubmitedlabel(),
                        documentLabels.getFreezedLabel());
                reopenLink.setVisible(true);
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(stateLabel, closeLink, submitLink, startProgressLink,
                        reopenLink, approveLink, declineLink, documentLabels, tabs);
            }
        };
        addComponent(closeLink, visible);
    }
    
    private void createReopenButton(boolean visible) {
        reopenLink = new AjaxLink("reopen") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.changeState(docData, StateEnum.OPEN);

                stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                
                setVisibility(true, closeLink, submitLink, startProgressLink, 
                        noneLabel, freezeLink);
                setVisibility(false, approvedLabel, declinedLabel, approvalByLabel, 
                        approvalByValue, reopenLink, documentLabels.getApprovedlabel(),
                        documentLabels.getSignedLabel(), documentLabels.getFreezedLabel());
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(stateLabel, closeLink, submitLink, startProgressLink, 
                        reopenLink, approvedLabel, declinedLabel, approvalByLabel,
                        approvalByValue, noneLabel, documentLabels, freezeLink, tabs);
            }
        };
        addComponent(reopenLink, visible);
    }
    
    private void createSignButton(boolean visible) {
        signLink = new AjaxLink("sign") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.addLabel(docData, LabelEnum.SIGNED);
                
                setVisibility(true, documentLabels.getSignedLabel());
                setVisibility(false, signLink);
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(documentLabels, signLink, tabs);
            }
        };
        addComponent(signLink, visible);
    }
    
    private void createFreezeButton(boolean visible) {
        freezeLink = new AjaxLink("freeze") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.addLabel(docData, LabelEnum.FREEZED);
                
                setVisibility(false, freezeLink);
                setVisibility(true, documentLabels.getFreezedLabel());
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(freezeLink, documentLabels, tabs);
            }
        };
        addComponent(freezeLink, visible);
    }
    
    private void addComponent(Component c, boolean visible) {
        c.setVisible(visible);
        c.setOutputMarkupPlaceholderTag(true);
        c.setOutputMarkupId(true);
        add(c);
    }
   
    private void setVisibility(boolean visible, Component... components) {
        for (Component c: components)
            c.setVisible(visible);
    }
    
    private void disableComponents(Component... components) {
        for (Component c: components)
            c.setEnabled(false);
    }
}