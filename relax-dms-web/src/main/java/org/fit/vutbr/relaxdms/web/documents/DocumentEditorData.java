package org.fit.vutbr.relaxdms.web.documents;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Martin Kanis
 */
@Data
@RequiredArgsConstructor
public class DocumentEditorData implements Serializable {
    
    @NonNull
    private JsonNode schema;
    
    private JsonNode document;
    
    @NonNull
    private EditorUseCase useCase;
    
    private Map<String, String> diffMap;
    
    private String parameters;
    
    public enum EditorUseCase {
        CREATE("CREATE"),
        UPDATE("UPDATE");
        
        private final String name;       

        private EditorUseCase(String s) {
            name = s;
        }

        @Override
        public String toString() {
           return this.name;
        }
    }
}
