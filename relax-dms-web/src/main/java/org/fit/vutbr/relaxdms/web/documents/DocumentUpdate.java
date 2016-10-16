package org.fit.vutbr.relaxdms.web.documents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;

/**
 *
 * @author mkanis
 */
public class DocumentUpdate extends BasePage implements Serializable {
    
    @Inject
    private DocumentService documentService;
    
    @Inject 
    private Convert convert;
    
    private WebMarkupContainer container;
    
    private Label myScript;
    
    private Model<String> labelModel;
    
    private PackageTextTemplate ptt;

    public DocumentUpdate(PageParameters parameters, JsonNode json) {
        super(parameters);
        
        String id = json.get("_id").textValue();
        String rev = json.get("_rev").textValue();
        String schemaId = json.get("schemaId").textValue();
        String schemaRev = json.get("schemaRev").textValue();
        
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
                ((ObjectNode) document).put("_id", id).put("_rev", rev)
                        .put("schemaId", schemaId).put("schemaRev", schemaRev);
                documentService.updateDocument(document);
                
                parameters.add("id", id);
                setResponsePage(DocumentPage.class, parameters);
            }
        };

        add(ajaxSaveBehaviour);

        JsonNode schema = documentService.getSchema(schemaId, schemaRev);
        renderJsonEditor(schema, json);
    }
    
    private void renderJsonEditor(JsonNode schema, JsonNode document) {
        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        
        // render json-editor script
        Map<String, Object> map = new HashMap<>();
        map.put("schema", schema);
        ((ObjectNode) document).remove(Arrays.asList("_id", "_rev", "schemaId", "schemaRev"));
        map.put("startval", document);
        
        ptt = new PackageTextTemplate(DocumentCreate.class, "../../../../../../editor.js");
        labelModel = Model.of(ptt.asString(map));

        myScript = new Label("jsonEditor", labelModel);
        myScript.setOutputMarkupId(true);
        myScript.setEscapeModelStrings(false);
        
        container.add(myScript);
        add(container);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }  
}
