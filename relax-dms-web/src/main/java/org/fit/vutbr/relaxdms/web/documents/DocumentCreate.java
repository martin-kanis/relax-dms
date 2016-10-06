package org.fit.vutbr.relaxdms.web.documents;

import java.io.Serializable;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.api.CouchDbRepository;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author Martin Kanis
 */
public class DocumentCreate extends BasePage implements Serializable {
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private AuthController authController;

    @Inject
    private CouchDbRepository repo;
    
    @Inject 
    private Convert convert;
    
    private WebMarkupContainer container;
    
    private Label myScript;
    
    private Model<String> labelModel;
    
    private PackageTextTemplate ptt;
    
    public DocumentCreate(PageParameters parameters) {
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
        
        AbstractAjaxBehavior ajaxSaveBehaviour = new AbstractDefaultAjaxBehavior() {
            
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                String js = "function send(json) { " + getCallbackScript() + " return true }";
                response.render(JavaScriptHeaderItem.forScript(js, "postJson"));
            } 
            
            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.getExtraParameters().put("data", "PLACEHOLDER");
            }
            
            @Override
            public CharSequence getCallbackScript() {
              String script = super.getCallbackScript().toString();
              script = script.replace("\"PLACEHOLDER\"", "json");
              return script;
            }
            
            @Override
            protected void respond(AjaxRequestTarget target) {
                RequestCycle cycle = RequestCycle.get();
                WebRequest webRequest = (WebRequest) cycle.getRequest();
                StringValue json = webRequest.getQueryParameters().getParameterValue("data");
                
                JsonNode document = convert.stringToJsonNode(json.toString());
                documentService.storeDocument(document);
            }
        };

        add(ajaxSaveBehaviour);

        renderJsonEditor(documentService.getDocumentById(templateId));
    }
    
    private void renderJsonEditor(JsonNode schema) {
        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        
        // render json-editor script
        Map<String, Object> map = new HashMap<>();
        map.put("schema", schema);
        map.put("author", authController.getUserName((HttpServletRequest) getRequest().getContainerRequest()));
        
        ptt = new PackageTextTemplate(DocumentCreate.class, "../../../../../../editor.js");
        labelModel = Model.of(ptt.asString(map));

        myScript = new Label("jsonEditor", labelModel);
        myScript.setOutputMarkupId(true);
        myScript.setEscapeModelStrings(false);
        
        container.add(myScript);
        add(container);
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
                        setResponsePage(DocumentCreate.class, params);
                    }
                };
                item.add(templateLink.add(new Label("label", title)));
            }
        };
        add(listview);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }  
}
