package org.fit.vutbr.relaxdms.web.cp.menu;

/**
 *
 * @author Martin Kanis
 */
public enum MenuItemEnum {
    DOCUMENT("Document"),
    ADMIN("Admin"),
    USER("User");

    private final String label;

    private MenuItemEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
