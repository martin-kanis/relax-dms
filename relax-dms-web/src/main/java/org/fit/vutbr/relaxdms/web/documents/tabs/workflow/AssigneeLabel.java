package org.fit.vutbr.relaxdms.web.documents.tabs.workflow;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.StringAutoCompleteRenderer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.web.client.keycloak.api.KeycloakAdminClient;
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
        
        setAutocomplete();
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
    
    private void setAutocomplete() {
        AutoCompleteSettings settings = new AutoCompleteSettings();		
        settings.setThrottleDelay(400);
        
        autocomplete = new AutoCompleteBehavior(StringAutoCompleteRenderer.INSTANCE, settings) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = authClient.getUsers(input);

                return choices.iterator();
            }
        };
    }
}
