package org.fit.vutbr.relaxdms.web.workflow;

import java.io.Serializable;
import javax.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
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
    
    private AjaxLink unfreezeWorkflowButton;

    public WorkflowPage(PageParameters parameters) {
        super(parameters);
        
        createSignWorkflowButton(true);
    }
    
    private void createSignWorkflowButton(boolean visible) {
        unfreezeWorkflowButton = new AjaxLink("unfreeze") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                workflowService.insertAllFacts(documentService.getAllDocumentsMetadata());

                target.add();
            }
        };
        addComponent(unfreezeWorkflowButton, visible);
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
