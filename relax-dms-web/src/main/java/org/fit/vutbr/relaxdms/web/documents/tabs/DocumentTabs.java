package org.fit.vutbr.relaxdms.web.documents.tabs;

import org.fit.vutbr.relaxdms.web.documents.tabs.workflow.DocumentWorkflow;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class DocumentTabs extends BasePage implements Serializable { 
    
    private final Logger log = Logger.getLogger(getClass());
    
    private final String id;
    
    private Map diffMap;
    
    public static final String script = "var el = document.getElementsByClassName('navigationTabs');"
            + "var list = el[0].firstElementChild; list.className += 'nav nav-tabs';";

    public DocumentTabs(PageParameters parameters) {
        super(parameters);
        id = getDocId(parameters);
        prepareTabs();
    }
    
    public DocumentTabs(PageParameters parameters, Map diffMap) {
        super(parameters);
        id = getDocId(parameters);
        this.diffMap = diffMap;
        prepareTabs();
    }
    
    private void prepareTabs() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new Model<>("Document")) {
            @Override
            public Panel getPanel(String panelId) {
                return (diffMap == null) ?  new DocumentPage(panelId, id) :  new DocumentPage(panelId, id, diffMap);
            }
           
        });

        tabs.add(new AbstractTab(new Model<>("Metadata")) {
            @Override
            public Panel getPanel(String panelId) {
                return new DocumentInfo(panelId, id);
            }
        });

        tabs.add(new AbstractTab(new Model<>("Workflow")) {
            @Override
            public Panel getPanel(String panelId) {
                return new DocumentWorkflow(panelId, id);
            }
        });

        AjaxTabbedPanel ajaxTabbedPanel = new AjaxTabbedPanel("tabs", tabs) {
            @Override
            protected String getSelectedTabCssClass() {
                return "active";
            }

            @Override
            protected String getTabContainerCssClass() {
                return "navigationTabs";
            }
            
            @Override
            protected void onAjaxUpdate(final AjaxRequestTarget target) {
                target.appendJavaScript(script);
            }
            
            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                response.render(JavaScriptHeaderItem.forScript("window.onload = function () {" + script + "}", null));
            }
        };
        add(ajaxTabbedPanel);
    }
    
    private String getDocId(PageParameters parameters) {
        StringValue sv = parameters.get("id");
        
        if (sv.isNull() || sv.isEmpty()) {
            log.info("ID is null or empty");
        }
        return sv.toString();
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }
}
