package org.fit.vutbr.relaxdms.web.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class SchemaUpdate extends BasePage implements Serializable {
    
    @Inject
    private CouchDbRepository repo;
    
    @Inject
    private Convert convert;
    
    @Inject
    private DocumentService documentService;
    
    private FeedbackPanel feedback;

    public SchemaUpdate(PageParameters parameters) {
        super(parameters);
        String templateId = parameters.get("templateId").toString();
        
        List<JsonNode> templates = repo.getAllTemplates();
        // create map (template title) -> (template id)
        Map<String, String> templateMap = templates.stream()
                .collect(Collectors.toMap(t -> t.get("title").asText(), t -> t.get("_id").asText()));
        
        // get first template on the first render
        if (templateId == null)
            templateId = templates.get(0).get("_id").textValue();
        
        createTemplateList(templateMap);
        
        JsonNode schema = documentService.getDocumentById(templateId);
        createTextArea(schema);
    }
    
    private void createTemplateList(Map<String, String> templateMap) {  
        List<String> titleList = templateMap.keySet().stream().collect(Collectors.toList());
        ListView listview = new ListView("listView", titleList) {
            @Override
            protected void populateItem(ListItem item) {
                final String title = (String) item.getModelObject();
                final String id = templateMap.get(title);
                
                AjaxLink<Void> templateLink = new AjaxLink("templateLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        PageParameters params = new PageParameters();
                        params.add("templateId", id);
                        setResponsePage(SchemaUpdate.class, params);
                    }
                };
                item.add(templateLink.add(new Label("label", title)));
            }
        };
        add(listview);
    }
    
    private void createTextArea(JsonNode schema) {
        // save id, rev and attachment
        String id = schema.get("_id").textValue();
        String rev = schema.get("_rev").textValue();
        String attachment = convert.jsonNodeToString(schema.get("_attachments"));
        String oldSchema = convert.jsonNodeToString(schema);
        
        // remove id, rev and attachment from oldSchema to avoid users to modify it
        ((ObjectNode) schema).remove(Arrays.asList("_id", "_rev", "_attachments"));
        String prettySchema = createPrettyJson(schema);
        
        final TextArea<String> textArea = new TextArea<>("area", Model.of(prettySchema));
        textArea.setRequired(true);
        textArea.setOutputMarkupId(true);
        textArea.setEscapeModelStrings(false);

        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        
        Form<?> form = new Form<>("userForm");        
        form.add(feedback);
        form.add(textArea);
        form.add(new AjaxSubmitLink("update") {          
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                String schema = textArea.getDefaultModelObjectAsString();

                feedback.setVisible(true);
                target.add(feedback);
                form.replace(feedback);

                if (convert.isValidJson(schema)) {
                    JsonNode newSchema = convert.stringToJsonNode(schema);
                    // remove id, rev and attachment for case, that user add it
                    ((ObjectNode) newSchema).remove(Arrays.asList("_id", "_rev", "_attachments"));
                    // put id, rev and attachment back
                    ((ObjectNode) newSchema).put("_id", id).put("_rev", rev).put("_attachments", convert.stringToJsonNode(attachment));

                    documentService.updateSchema(convert.stringToJsonNode(oldSchema), newSchema);

                    success("Template was update successfully");

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
    
    private String createPrettyJson(JsonNode json) {
        ObjectMapper mapper = new ObjectMapper();
        String prettySchema;
        try {
            prettySchema = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException ex) {
            // json proccesing exception, take not formated schema
            prettySchema = json.toString(); 
        }
        
        return prettySchema;
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.ADMIN;
    } 
}
