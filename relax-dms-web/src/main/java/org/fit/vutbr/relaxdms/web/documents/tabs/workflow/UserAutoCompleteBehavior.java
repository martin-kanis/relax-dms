package org.fit.vutbr.relaxdms.web.documents.tabs.workflow;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.StringAutoCompleteRenderer;
import org.apache.wicket.util.string.Strings;
import org.fit.vutbr.relaxdms.data.client.keycloak.api.KeycloakAdminClient;

/**
 *
 * @author mkanis
 */
public class UserAutoCompleteBehavior extends AutoCompleteBehavior<String> {
    
    @Inject
    private KeycloakAdminClient authClient;
    
    private final Set<String> filter;
    
    private boolean retain;

    public UserAutoCompleteBehavior(AutoCompleteSettings settings) {
        super(StringAutoCompleteRenderer.INSTANCE, settings);
        filter = Collections.EMPTY_SET;
    }
    
    public UserAutoCompleteBehavior(AutoCompleteSettings settings, Set<String> filter, boolean retain) {
        super(StringAutoCompleteRenderer.INSTANCE, settings);
        this.filter = filter;
        this.retain = retain;
    }

    @Override
    protected Iterator<String> getChoices(String input) {
        if (Strings.isEmpty(input)) {
            List<String> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }

        Set<String> choices = authClient.getUsers(input);
        if (retain) {
            choices.retainAll(filter);
        } else {
            choices.removeAll(filter);
        }

        return choices.iterator();
    }   
}
