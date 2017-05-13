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
package org.fit.vutbr.relaxdms.web.admin;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class Schema extends BasePage implements Serializable {
    
    @Inject
    private Convert convert;
    
    @Inject
    private DocumentService documentService;
    
    private FeedbackPanel feedback;

    public Schema(PageParameters parameters) {
        super(parameters);
        
        final TextArea<String> textArea = new TextArea<>("area", Model.of(""));
        textArea.setRequired(true);
        textArea.setOutputMarkupId(true);
        textArea.setEscapeModelStrings(false);

        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        
        Form<?> form = new Form<>("userForm");        
        form.add(feedback);
        form.add(textArea);
        form.add(new AjaxSubmitLink("save") {
            
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                String schema = textArea.getDefaultModelObjectAsString();

                feedback.setVisible(true);
                target.add(feedback);
                form.replace(feedback);
                
                if (convert.isValidJson(schema)) {
                    success("Template was successfully saved");
                    
                    JsonNode node = convert.stringToJsonNode(schema);
                    documentService.storeSchema(node);
                    
                    // clear text area
                    textArea.setModelObject("");
                    target.add(textArea);
                } else {
                    error("Not valid JSON");
                }
            }
        });
                
        add(form);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.ADMIN;
    }
}
