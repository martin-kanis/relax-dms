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
