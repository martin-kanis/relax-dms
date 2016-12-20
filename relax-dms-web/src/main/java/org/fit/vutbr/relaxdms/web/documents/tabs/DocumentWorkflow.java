package org.fit.vutbr.relaxdms.web.documents.tabs;

import java.io.Serializable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.api.system.Convert;

/**
 *
 * @author Martin Kanis
 */
public class DocumentWorkflow extends Panel implements Serializable {

    private final String user;

    private final String id;
    
    private final String doc;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private Convert convert;
    
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
        
        this.doc = convert.jsonNodeToString(documentService.getDocumentById(this.id));
        prepareApprovalComponents();
    }
    
    private void prepareApprovalComponents() {
        boolean approved = workflowService.isApproved(convert.stringToJsonNode(doc));
        boolean declined = workflowService.isDeclined(convert.stringToJsonNode(doc));
        
        createApprovedLabel(approved);
        createDeclinedLabel(declined);
        
        createApproveButton(approved);     
        createDeclineButton(declined);
    }
    
    private void createApproveButton(boolean visible) {
        approveLink = new AjaxLink("approve") {       
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.approveDoc(convert.stringToJsonNode(doc), user);
                PageParameters params = new PageParameters();
                params.add("id", id);
                //setResponsePage(DocumentTabs.class, params);

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
                workflowService.declineDoc(convert.stringToJsonNode(doc), user);
                PageParameters params = new PageParameters();
                params.add("id", id);
                //setResponsePage(DocumentTabs.class, params);
                
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