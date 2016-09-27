package org.fit.vutbr.relaxdms.web.documents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
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
    private Convert convert;

    public DocumentCreate(PageParameters parameters) {
        super(parameters);
        
        AbstractAjaxBehavior ajaxSaveBehaviour = new AbstractDefaultAjaxBehavior() {
            
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                String js = "function send(json) { " + getCallbackScript() + " }";
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
                
                Document document = convert.serialize(Document.class, json.toString());
                documentService.storeDocument(document);
            }
        };

        add(ajaxSaveBehaviour);

        // render json-editor script
        Map<String, Object> map = new HashMap<>();
        map.put("schema", getSchema());
        map.put("author", authController.getUserName((HttpServletRequest) getRequest().getContainerRequest()));

        PackageTextTemplate ptt = new PackageTextTemplate(DocumentCreate.class, "../../../../../../editor.js");
        Label myScript = new Label("jsonEditor", ptt.asString(map));
        myScript.setEscapeModelStrings(false);
        add(myScript);  
    }
    
    private String getSchema() {
        return documentService.createJSONSchema(Document.class);
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }  
}
