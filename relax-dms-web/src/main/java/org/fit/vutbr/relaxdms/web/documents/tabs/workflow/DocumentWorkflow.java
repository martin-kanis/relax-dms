package org.fit.vutbr.relaxdms.web.documents.tabs.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.client.keycloak.api.KeycloakAdminClient;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Environment;
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
    
    @Inject
    private KeycloakAdminClient authClient;
    
    @Inject
    private Convert convert;
    
    private final boolean isManager;
    
    private AjaxLink approveLink;
    
    private AjaxLink declineLink;
    
    private AjaxLink submitLink;
    
    private AjaxLink startProgressLink;
    
    private AjaxLink closeLink;
    
    private AjaxLink reopenLink;
    
    private AjaxLink signLink;
    
    private AjaxLink freezeLink;
    
    private AjaxLink releaseLink;
    
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
    
    private StringBuilder userValues;
    
    private TextField textField;
    
    public DocumentWorkflow(String id, String docId, String docRev, DocumentTabs tabs) {
        super(id);
        this.tabs = tabs;
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        user = auth.getUserName(req);

        isManager = auth.isUserAuthorized(req, "manager");
        
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
        userValues = new StringBuilder();

        assigneeLabel = new AssigneeLabel("assignee", docId, docData, tabs);
        if (workflowService.checkLabel(workflow, LabelEnum.RELEASED)) {
            assigneeLabel.setEnabled(false);
        }

        prepareComponents();
        prepareApprovalComponents();
        
        documentLabels = new DocumentLabels("labels", docData);
        documentLabels.setOutputMarkupId(true);
        add(documentLabels);
        
        // disable components if we have older version or user don't have permissions to edit
        if ((docRev != null && !docRev.equals(documentService.getCurrentRevision(docId))) ||
                !auth.isUserAuthorized(req, "writer")) {
            disableComponents();
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
        boolean approvalVisible = isManager && workflowService.checkState(workflow, StateEnum.SUBMITED);
        createApproveButton(approvalVisible && !approved);     
        createDeclineButton(approvalVisible && !declined);
    }
    
    private void prepareComponents() {
        stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
        addComponent(stateLabel, true);

        addComponent(assigneeLabel, true);
        
        boolean submitVisible = workflowService.checkState(workflow, StateEnum.OPEN) ||
                workflowService.checkState(workflow, StateEnum.IN_PROGRESS);
        createSubmitButton(submitVisible);
        
        boolean startProgressVisible = workflowService.checkState(workflow, StateEnum.OPEN);
        createStartProgressButton(startProgressVisible);
        
        boolean closeVisible = !workflowService.checkState(workflow, StateEnum.CLOSED) &&
                !workflowService.checkLabel(workflow, LabelEnum.RELEASED);
        createCloseButton(closeVisible);
        
        boolean reopenVisible = workflowService.checkState(workflow, StateEnum.CLOSED);
        createReopenButton(reopenVisible);
        
        boolean signVisible = workflowService.canBeSigned(docData, isManager);
        createSignButton(signVisible);
        
        boolean freezeVisible = (workflowService.checkState(workflow, StateEnum.OPEN) ||
                workflowService.checkState(workflow, StateEnum.IN_PROGRESS)) && 
                !workflowService.checkLabel(workflow, LabelEnum.FREEZED);
        createFreezeButton(freezeVisible);
        
        boolean releaseVisible = workflowService.checkLabel(workflow, LabelEnum.SIGNED) &&
                !workflowService.checkLabel(workflow, LabelEnum.RELEASED);
        createReleaseButton(releaseVisible);
        
        createPermissionsForm();
    }
    
    private void createPermissionsForm() {
        final Set<String> permissions = workflowService.getPermissionsFromDoc(docData);
        userValues = convert.docPermissionsToString(permissions);
        final IModel<String> model = new IModel<String>() {
            private String value = null;

            @Override
            public String getObject() {
                return value;
            }

            @Override
            public void setObject(String object) {
                value = object;
                if (object != null) {
                    if (!permissions.contains(value)) {
                        userValues.append("\n");
                        userValues.append(value);
                    }
                }
            }

            @Override
            public void detach() {
                
            }
        };
        
        textField = new TextField("users", model);
        AutoCompleteSettings settings = new AutoCompleteSettings();		
        settings.setThrottleDelay(400);
        textField.add(new UserAutoCompleteBehavior(settings, permissions, false));
        
        final MultiLineLabel label = new MultiLineLabel("selectedUsers", new PropertyModel<>(this,
            "userValues"));
        label.setOutputMarkupId(true);
        
        Form<?> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                if (textField.getModelObject() == null)
                    return;
                workflowService.addPermissionsToDoc(docData, textField.getModelObject().toString());

                // clear the textfield
                textField.setModelObject(null);
            }
        };
        add(form);
        form.add(textField);
        form.add(label);
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
        Environment env = new Environment();
        
        List<String> managerList = authClient.getManagers();
        ListView listview = new ListView("dropdownView", managerList) {
            @Override
            protected void populateItem(ListItem item) {
                String user = (String) item.getModelObject();
                
                AjaxLink link = new AjaxLink("userLink") {
                    
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        env.setValue(true);
                        env.setAssignTo(user);
                        workflowService.submitDocument(docData, env);

                        stateLabel.setDefaultModel(new Model(workflow.getState().getCurrentState().getName()));
                        assigneeLabel.setDefaultModel(new Model(workflow.getAssignment().getAssignee()));

                        setVisibility(false, submitLink, startProgressLink, approvedLabel,
                                declinedLabel, approvalByLabel, approvalByValue, 
                                documentLabels.getApprovedlabel(), documentLabels.getFreezedLabel(),
                                freezeLink);
                        setVisibility(true, noneLabel, documentLabels.getSubmitedlabel());

                        boolean approvalVisible = isManager && workflowService.checkState(workflow, StateEnum.SUBMITED);
                        approveLink.setVisible(approvalVisible);
                        declineLink.setVisible(approvalVisible);

                        tabs.refreshTabs(docData.getMetadata().getRev());

                        target.add(stateLabel, assigneeLabel, submitLink, startProgressLink, 
                                approveLink, declineLink, approvedLabel, declinedLabel, 
                                approvalByLabel, approvalByValue, noneLabel, documentLabels,
                                freezeLink, tabs);
                    }
                };
                link.add(new Label("linkLabel", user));
                item.add(link);
            }
        };
        add(listview);
        submitLink = new AjaxLink("submit") {
            @Override
            public void onClick(AjaxRequestTarget target) {

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
                
                setVisibility(false, closeLink, submitLink, startProgressLink, signLink, releaseLink, 
                        approveLink, declineLink, documentLabels.getSubmitedlabel(),
                        documentLabels.getFreezedLabel());
                reopenLink.setVisible(true);
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(stateLabel, closeLink, submitLink, startProgressLink, signLink,
                        reopenLink, approveLink, declineLink, documentLabels, releaseLink, tabs);
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
                        approvalByValue, reopenLink, signLink, releaseLink, documentLabels.getApprovedlabel(),
                        documentLabels.getSignedLabel(), documentLabels.getFreezedLabel());
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(stateLabel, closeLink, submitLink, startProgressLink, releaseLink,
                        reopenLink, approvedLabel, declinedLabel, approvalByLabel, signLink,
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
                
                setVisibility(true, releaseLink, documentLabels.getSignedLabel());
                setVisibility(false, signLink);
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(documentLabels, signLink, releaseLink, tabs);
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
    
    private void createReleaseButton(boolean visible) {
        releaseLink = new AjaxLink("release") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.addLabel(docData, LabelEnum.RELEASED);
                
                setVisibility(false, releaseLink, closeLink);
                setVisibility(true, documentLabels.getReleasedLabel());
                assigneeLabel.setEnabled(false);
                textField.setEnabled(false);
                
                tabs.refreshTabs(docData.getMetadata().getRev());
                
                target.add(releaseLink, documentLabels, closeLink, assigneeLabel, tabs);
            }
        };
        addComponent(releaseLink, visible);
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
    
    private void disableComponents() {
        List<Component> components = Arrays.asList(approveLink, declineLink, submitLink, 
                startProgressLink, closeLink, reopenLink, releaseLink, signLink, 
                freezeLink, assigneeLabel, textField);
        components.stream().forEach((c) -> {
            c.setEnabled(false);
        });
    }
}