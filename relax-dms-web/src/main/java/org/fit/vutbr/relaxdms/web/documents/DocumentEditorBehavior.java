package org.fit.vutbr.relaxdms.web.documents;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.fit.vutbr.relaxdms.data.db.dao.model.DocumentMetadata;
import org.fit.vutbr.relaxdms.web.documents.tabs.DocumentTabs;

/**
 *
 * @author mkanis
 */
public class DocumentEditorBehavior extends AbstractDefaultAjaxBehavior {
    
    @Inject
    private DocumentService documentService;
    
    @Inject 
    private Convert convert;
    
    @Inject
    private AuthController auth;
    
    private final DocumentMetadata metadata;
    
    private final Document docData;
    
    private final Component component;

    public DocumentEditorBehavior(Document document, Component c) {
        metadata = document.getMetadata();
        docData = document;
        component = c;
    }

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
        HttpServletRequest req = (HttpServletRequest) component.getRequest().getContainerRequest();
        String user = auth.getUserName(req);

        // document id is empty = create new document
        if (metadata.get_id() == null) {
            metadata.setAuthor(user);
            metadata.setLastModifiedBy(user);
            documentService.storeDocument(document, docData);
        // update document
        } else {
            metadata.setLastModifiedBy(user);
            JsonNode diff = documentService.updateDocument(document, docData);
            
            Map<String, String> diffMap = new HashMap<>();
            if(!diff.isNull()) {
                for (JsonNode node : diff) {
                    String path = node.get("path").textValue();
                    String name = "root" + path.replace("/", ".");
                    String value = node.get("value").asText();
                    diffMap.put(name, value);
                }
            }

            PageParameters params = new PageParameters();
            params.add("id", metadata.get_id());
            
            component.setResponsePage(new DocumentTabs(params, diffMap));
        }
    }
}
