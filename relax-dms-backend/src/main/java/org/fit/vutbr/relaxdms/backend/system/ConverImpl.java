package org.fit.vutbr.relaxdms.backend.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.ektorp.Revision;
import org.fit.vutbr.relaxdms.api.system.Convert;
import org.fit.vutbr.relaxdms.data.db.dao.model.Document;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class ConverImpl implements Convert {
//    
//    @Inject
//    private Logger logger;
    
    @Override
    public List<String> revisionToString(List<Revision> revs) {
        return revs.stream().map(Revision::getRev).collect(Collectors.toList());
    }
    
    @Override
    public <T extends Document> T serialize(Class clazz, String json) {
        try {
            return (T) new ObjectMapper().readValue(json, clazz);
        } catch (IOException ex) {
//            logger.error(ex);
            return null;
        }
    }
}
