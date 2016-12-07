package org.fit.vutbr.relaxdms.web.documents.tabs;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.fit.vutbr.relaxdms.api.service.DocumentService;

/**
 *
 * @author Martin Kanis
 */
public class DocumentInfo extends Panel implements Serializable {
    
    @Inject
    private DocumentService documentService;

    public DocumentInfo(String id, String docId) {
        super(id);
 
        Map<String, String> metadata = documentService.getMetadataFromDoc(docId);
        List<String> keyList = metadata.keySet().stream().collect(Collectors.toList());
        
        ListView listview = new ListView("listView", keyList) {
            @Override
            protected void populateItem(ListItem item) {
                String key = (String) item.getModelObject();
                
                item.add(new Label("property", key));
                item.add(new Label("value", metadata.get(key)));
            }
        };
        add(listview);
    }
}
