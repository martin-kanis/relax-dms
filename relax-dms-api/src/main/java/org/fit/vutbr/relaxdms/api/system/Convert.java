package org.fit.vutbr.relaxdms.api.system;

import java.util.List;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;

/**
 *
 * @author Martin Kanis
 */
public interface Convert {
    public List<String> revisionToString(List<Revision> revs);
    
    public <T extends Document> T serialize(Class clazz, String json);
}
