/* 
 * The MIT License
 *
 * Copyright 2017 mkanis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fit.vutbr.relaxdms.web.documents.tabs.workflow;

import javax.inject.Inject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.LabelEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.StateEnum;
import org.fit.vutbr.relaxdms.data.db.dao.model.workflow.Workflow;

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
    
    private Label releasedLabel;
    
    private Label signedLabel;
    
    private Label approvedlabel;
    
    private Label submittedlabel;
    
    private Label freezedLabel;
    
    private Label noLabel;
    
    public DocumentLabels(String id, Document docData) {
        super(id);
        this.docData = docData;
        
        releasedLabel = new Label("released", LabelEnum.RELEASED.getName());
        releasedLabel.add(new AttributeModifier("title", LabelEnum.RELEASED.getDescription()));
        
        signedLabel = new Label("signed", LabelEnum.SIGNED.getName());
        signedLabel.add(new AttributeModifier("title", LabelEnum.SIGNED.getDescription()));
        
        approvedlabel = new Label("approved", LabelEnum.APPROVED.getName());
        approvedlabel.add(new AttributeModifier("title", LabelEnum.APPROVED.getDescription()));
        
        submittedlabel = new Label("submitted", LabelEnum.SUBMITTED.getName());
        submittedlabel.add(new AttributeModifier("title", LabelEnum.SUBMITTED.getDescription()));
        
        freezedLabel = new Label("freezed", LabelEnum.FREEZED.getName());
        freezedLabel.add(new AttributeModifier("title", LabelEnum.FREEZED.getDescription()));
        
        boolean isReleased = workflowService.checkLabel(docData.getWorkflow(), LabelEnum.RELEASED);
        releasedLabel.setVisible(isReleased);
        
        boolean isSigned = workflowService.checkLabel(docData.getWorkflow(), LabelEnum.SIGNED);
        signedLabel.setVisible(isSigned);
        
        boolean isApproved = workflowService.isApproved(docData.getWorkflow());
        approvedlabel.setVisible(isApproved);
        
        boolean isSubmited = workflowService.checkState(docData.getWorkflow(), StateEnum.SUBMITTED);
        submittedlabel.setVisible(isSubmited);
        
        boolean isFreezed = workflowService.checkLabel(docData.getWorkflow(), LabelEnum.FREEZED);
        freezedLabel.setVisible(isFreezed);
        
        boolean isNoLabel = !isReleased && !isSigned && !isApproved && !isSubmited && !isFreezed;
        noLabel = new Label("noLabel", "No labels yet.");
        noLabel.setOutputMarkupId(true);
        noLabel.setVisible(isNoLabel);
        
        add(releasedLabel, signedLabel, approvedlabel, submittedlabel, freezedLabel, noLabel);
    }

    public void refreshLabels(Workflow workflow, AjaxRequestTarget target) {
        if (workflow.getLabels().isEmpty())
            noLabel.setVisible(true);
        else 
            noLabel.setVisible(false);
        target.add(noLabel);
    }
}
