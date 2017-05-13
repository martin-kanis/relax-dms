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
