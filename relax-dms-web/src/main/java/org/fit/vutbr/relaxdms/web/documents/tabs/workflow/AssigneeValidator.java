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

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.fit.vutbr.relaxdms.data.client.keycloak.api.KeycloakAdminClient;

/**
 *
 * @author Martin Kanis
 */
public class AssigneeValidator implements IValidator<String> {

    private final KeycloakAdminClient authClient;

    public AssigneeValidator(KeycloakAdminClient authClient) {
        this.authClient = authClient;
    }
    
    @Override
    public void validate(IValidatable<String> validatable) {
        final String user = validatable.getValue();

        // validate user
        if (!authClient.userExists(user)) {
            error(validatable, "User doesn't exist");
        }
    }

    private void error(IValidatable<String> validatable, String errorKey) {
            ValidationError error = new ValidationError(errorKey);
            validatable.error(error);
    }
}
