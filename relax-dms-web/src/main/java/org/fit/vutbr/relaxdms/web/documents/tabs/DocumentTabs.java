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
package org.fit.vutbr.relaxdms.web.documents.tabs;

import org.fit.vutbr.relaxdms.web.documents.tabs.workflow.DocumentWorkflow;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.fit.vutbr.relaxdms.api.security.AuthController;
import org.fit.vutbr.relaxdms.api.service.DocumentService;
import org.fit.vutbr.relaxdms.api.service.WorkflowService;
import org.fit.vutbr.relaxdms.web.BasePage;
import org.fit.vutbr.relaxdms.web.cp.menu.MenuItemEnum;
import org.fit.vutbr.relaxdms.web.error.Forbidden;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class DocumentTabs extends BasePage implements Serializable { 
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private WorkflowService workflowService;
    
    @Inject
    private AuthController authControler;
    
    private final Logger log = Logger.getLogger(getClass());
    
    private String id;
    
    private String rev;
    
    private Map diffMap;
    
    private AjaxLink previousLink;
    
    private AjaxLink nextLink;
    
    private Label revisionCount;
    
    @Getter
    private Label currentRevision;
    
    @Setter
    private int currentIndex;
    
    private int versionsCount;
    
    private int selectedTab;
    
    public static final String script = "var el = document.getElementsByClassName('navigationTabs');"
            + "var list = el[0].firstElementChild; list.className += 'nav nav-tabs';";

    public DocumentTabs(PageParameters parameters) {
        super(parameters);
        getDocIdAndRev(parameters);
        checkAuthorization();
        prepareComponents(parameters);
    }
    
    public DocumentTabs(PageParameters parameters, Map diffMap) {
        super(parameters);
        getDocIdAndRev(parameters);
        checkAuthorization();
        this.diffMap = diffMap;
        prepareComponents(parameters);
    }
    
    private void checkAuthorization() {
        HttpServletRequest req = (HttpServletRequest) getRequest().getContainerRequest();
        String user = authControler.getUserName(req);
        if (!workflowService.isUserAuthorized(id, user)) {
            getRequestCycle().setResponsePage(Forbidden.class);
        }
    }
    
    public void refreshTabs(String rev) {
        this.rev = rev;
        currentIndex = documentService.getRevisionIndex(id, rev);
        versionsCount = documentService.countDocumentVersions(id);
        
        currentRevision.setDefaultModel(new Model(currentIndex));
        revisionCount.setDefaultModel(new Model(versionsCount));
        add(currentRevision, revisionCount);

        if (currentIndex == 1)
            previousLink.setEnabled(false);
        else 
            previousLink.setEnabled(true);
        if (currentIndex == versionsCount)
            nextLink.setEnabled(false);
        else 
            nextLink.setEnabled(true);
        add(previousLink, nextLink);
    }
    
    private void prepareComponents(PageParameters params) {
        currentIndex = documentService.getRevisionIndex(id, rev);
        versionsCount = documentService.countDocumentVersions(id);
        selectedTab = params.get("selected").toInt(0);
        prepareTabs();
        prepareLabels();
        prepareLinks();
    }
    
    private void prepareTabs() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new Model<>("Document")) {
            @Override
            public Panel getPanel(String panelId) {
                selectedTab = 0;
                return (diffMap == null) ?  new DocumentPage(panelId, id, rev) :  new DocumentPage(panelId, id, rev, diffMap);
            }
           
        });

        tabs.add(new AbstractTab(new Model<>("Metadata")) {
            @Override
            public Panel getPanel(String panelId) {
                selectedTab = 1;
                return new DocumentInfo(panelId, id, rev);
            }
        });

        tabs.add(new AbstractTab(new Model<>("Workflow")) {
            @Override
            public Panel getPanel(String panelId) {
                selectedTab = 2;
                return new DocumentWorkflow(panelId, id, rev, DocumentTabs.this);
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
        
        ajaxTabbedPanel.setSelectedTab(selectedTab);
        add(ajaxTabbedPanel);
    }
    
    private void prepareLinks() {
        previousLink = new AjaxLink("previousLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<String> attachmentRevisions = documentService.getAttachmentRevisions(id);
                int index = attachmentRevisions.indexOf(rev);
                
                String nextRevision;
                
                // we are on the latest revision, return first revision in the list
                if (index == -1) {
                    nextRevision = attachmentRevisions.get(0);
                } else {
                    nextRevision = attachmentRevisions.get(index + 1);
                }
                
                PageParameters params = new PageParameters();
                params.add("id", id).add("rev", nextRevision).add("selected", selectedTab);
                setResponsePage(DocumentTabs.class, params);
            }
        };
        
        if (currentIndex == 1)
            previousLink.setEnabled(false);
        
        nextLink = new AjaxLink("nextLink") { 
            @Override
            public void onClick(AjaxRequestTarget target) {
                List<String> attachmentRevisions = documentService.getAttachmentRevisions(id);
                int index = attachmentRevisions.indexOf(rev);
                
                String nextRevision;
                
                // we are on the latest revision, return first revision in the list
                if (index == 0) {
                    nextRevision = documentService.getCurrentRevision(id);
                } else {
                    nextRevision = attachmentRevisions.get(index - 1);
                }
                
                PageParameters params = new PageParameters();
                params.add("id", id).add("rev", nextRevision).add("selected", selectedTab);
                setResponsePage(DocumentTabs.class, params);
            }
        };
        
        if (currentIndex == versionsCount)
            nextLink.setEnabled(false);
        
        add(previousLink, nextLink);
    }
    
    private void prepareLabels() {
        currentRevision = new Label("currentRevision", new Model(currentIndex));
        revisionCount = new Label("revisionCount", new Model(versionsCount));
        
        add(currentRevision, revisionCount);
    }
    
    private void getDocIdAndRev(PageParameters parameters) {
        StringValue sv = parameters.get("id");
        
        if (sv.isNull() || sv.isEmpty()) {
            log.info("ID is null or empty");
        }
        id = sv.toString();
        
        sv = parameters.get("rev");
        rev = sv.toString();
    }

    @Override
    public MenuItemEnum getActiveMenu() {
        return MenuItemEnum.DOCUMENT;
    }
}
