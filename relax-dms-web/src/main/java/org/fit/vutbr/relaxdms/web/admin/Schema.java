package org.fit.vutbr.relaxdms.web.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class Schema extends BasePage {
    
    @Inject
    private Convert convert;
    
    @Inject
    private CouchDbRepository repo;
    
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
                    repo.storeJsonNode(node);
                    
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
