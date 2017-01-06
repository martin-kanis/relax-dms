package org.fit.vutbr.relaxdms.web.documents.tabs.workflow;

import javax.inject.Inject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.LabelEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;

/**
 *
 * @author Martin Kanis
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DocumentLabels extends Panel {
    
    @Inject
    private WorkflowService workflowService;
    
    private Document docData;
    
    private Label signedLabel;
    
    private Label approvedlabel;
    
    private Label submitedlabel;
    
    private Label freezedLabel;
    
    public DocumentLabels(String id, Document docData) {
        super(id);
        this.docData = docData;
        
        signedLabel = new Label("signed", LabelEnum.SIGNED.getName());
        signedLabel.add(new AttributeModifier("title", LabelEnum.SIGNED.getDescription()));
        
        approvedlabel = new Label("approved", LabelEnum.APPROVED.getName());
        approvedlabel.add(new AttributeModifier("title", LabelEnum.APPROVED.getDescription()));
        
        submitedlabel = new Label("submited", LabelEnum.SUBMITED.getName());
        submitedlabel.add(new AttributeModifier("title", LabelEnum.SUBMITED.getDescription()));
        
        freezedLabel = new Label("freezed", LabelEnum.FREEZED.getName());
        freezedLabel.add(new AttributeModifier("title", LabelEnum.FREEZED.getDescription()));
        
        boolean isSigned = workflowService.checkLabel(docData.getWorkflow(), LabelEnum.SIGNED);
        signedLabel.setVisible(isSigned);
        
        boolean isApproved = workflowService.isApproved(docData.getWorkflow());
        approvedlabel.setVisible(isApproved);
        
        boolean isSubmited = workflowService.checkState(docData.getWorkflow(), StateEnum.SUBMITED);
        submitedlabel.setVisible(isSubmited);
        
        boolean isFreezed = workflowService.checkLabel(docData.getWorkflow(), LabelEnum.FREEZED);
        freezedLabel.setVisible(isFreezed);
        
        add(signedLabel, approvedlabel, submitedlabel, freezedLabel);
    }

}
