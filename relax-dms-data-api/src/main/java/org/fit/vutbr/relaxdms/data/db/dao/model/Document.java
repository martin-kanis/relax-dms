package org.fit.vutbr.relaxdms.data.db.dao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.sql.Timestamp;
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
    
    private Timestamp timestamp;

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
        private Timestamp timestamp;
        
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
        
        public Builder timestamp(Timestamp val) {
            timestamp = val;
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
        timestamp = builder.timestamp;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
