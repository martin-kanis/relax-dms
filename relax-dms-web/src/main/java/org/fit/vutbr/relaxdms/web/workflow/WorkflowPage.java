package org.fit.vutbr.relaxdms.web.workflow;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Environment;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class WorkflowPage extends BasePage implements Serializable {
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private AuthController authController;
    
    private final String user;
    
    private final boolean isManager;
    
    private AjaxLink unfreezeWorkflowButton;
    
    private AjaxLink releaseWorkflowButton;
    
    private String selected = "";

    public WorkflowPage(PageParameters parameters) {
        super(parameters);
        
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        user = authController.getUserName(req);
        isManager = authController.isUserAuthorized(req, "manager");
        
        createUnfreezeWorkflowButton(isManager);
        createReleaseWorkflowButton(isManager);
        createCustomWorkflow(isManager);
    }
    
    private void createUnfreezeWorkflowButton(boolean visible) {
        Environment env = new Environment();
        
        unfreezeWorkflowButton = new AjaxLink("unfreeze") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                env.setValue(true);
                workflowService.insertAllFacts(documentService.getAllDocumentsMetadata(), env);
            }
        };
        addComponent(unfreezeWorkflowButton, visible);
    }
    
    private void createReleaseWorkflowButton(boolean visible) {
        Environment env = new Environment();
        
        releaseWorkflowButton = new AjaxLink("release") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                env.setValue(true);
                env.setFireBy(user);
                workflowService.insertAllFacts(documentService.getAllDocumentsMetadata(), env);
            }
        };
        addComponent(releaseWorkflowButton, visible);
    }
    
    private void createCustomWorkflow(boolean visible) {
        List<String> rules = workflowService.getCustomRules();
        if (!rules.isEmpty())
            selected = rules.get(0);
        
        DropDownChoice<String> workflows = new DropDownChoice<>("workflows", 
                new PropertyModel<String>(this, "selected"), rules);

        Environment env = new Environment();
        
        Form<?> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                env.setValue(true);
                env.setFireBy(user);
                env.setRule(selected);
                workflowService.insertAllFacts(documentService.getAllDocumentsMetadata(), env);
            }
        };

        addComponent(form, visible);
        form.add(workflows);
    }
    
    private void addComponent(Component c, boolean visible) {
        c.setVisible(visible);
        c.setOutputMarkupPlaceholderTag(true);
        c.setOutputMarkupId(true);
        add(c);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.WORKFLOW;
    }
}
