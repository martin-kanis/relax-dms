package org.fit.vutbr.relaxdms.data.db.dao.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Martin Kanis
 */

@JsonInclude(Include.NON_NULL)
public class Document extends CouchDbDocument {

    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
