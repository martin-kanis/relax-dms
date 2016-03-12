package org.fit.vutbr.relaxdms.data.db.dao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author Martin Kanis
 */

@JsonInclude(Include.NON_NULL)
public class Document extends CouchDbDocument {

    private String name;
    
    private String author;
    
    private Category category;
    
    private String description;
    
    private List<String> keywords;

    public Document() {}
    
    // Builder
    @JsonIgnoreType
    public static class Builder {
        // required parameters
        private String name;
        private String author;
        private Category category;
        
        // optional parameters
        private String description;
        private List<String> keywords;
        
        public Builder(String name, String author, Category category) {
            this.name = name;
            this.author = author;
            this.category = category;
        }
        
        public Builder description(String val) {
            description = val;
            return this;
        }
        
        public Builder keywords(List<String> list) {
            keywords = list;
            return this;
        }
        
        public Document build() {
            return new Document(this);
        }
    }

    @JsonIgnore
    private Document(Builder builder) {
        author = builder.author;
        name = builder.name;
        description = builder.description;
        keywords = builder.keywords;
        category = builder.category;
    }
    
    // getters
    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getKeywords() {
        return keywords;
    }
    
    @Override
    public String toString() {
        return "Document(_id=" + getId() + " _rev=" + getRevision() +  " author=" +
                author + " name=" + name + " category= " + category + " )";
    }
}
