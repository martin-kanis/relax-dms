package org.fit.vutbr.relaxdms.web.documents.tabs.workflow;

import java.util.Set;
import javax.inject.Inject;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.client.keycloak.api.KeycloakAdminClient;
import org.fit.vutbr.relaxdms.web.documents.tabs.DocumentTabs;

/**
 *
 * @author Martin Kanis
 */
public class AssigneeLabel extends AjaxEditableLabel {
    
    private Document docData;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private KeycloakAdminClient authClient;
    
    private AutoCompleteBehavior autocomplete;
    
    private final DocumentTabs tabs;

    public AssigneeLabel(String id, String docId, Document docData, DocumentTabs tabs) {
        super(id, new Model(docData.getWorkflow().getAssignment().getAssignee()));

        this.tabs = tabs;
        this.docData = docData;
        Set<String> permissions = workflowService.getPermissionsFromDoc(docData);
        
        AutoCompleteSettings settings = new AutoCompleteSettings();		
        settings.setThrottleDelay(400);
        autocomplete = new UserAutoCompleteBehavior(settings, permissions, true);
        add(new AssigneeValidator(authClient));
    } 
    
    @Override
    protected FormComponent newEditor(MarkupContainer parent, String componentId, IModel model) { 
        FormComponent editor = super.newEditor(parent, componentId, model);
        editor.add(autocomplete);

        return editor; 
    } 
    
    @Override
    protected void onSubmit(final AjaxRequestTarget target) {
        super.onSubmit(target);

        String assignee = super.getEditor().getInput();
        
        // check if assignee was changed
        if (!assignee.equals(docData.getWorkflow().getAssignment().getAssignee())) {
            // persist
            workflowService.assignDocument(docData, assignee);
            setDefaultModel(new Model(assignee));
            
            tabs.refreshTabs(docData.getMetadata().getRev());
            
            target.add(this, tabs);
        }
    }
}
