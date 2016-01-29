package org.fit.vutbr.relaxdms.rest;

import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import org.ektorp.Revision;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class Convert {
    
    public List<String> revisionToString(List<Revision> revs) {
        return revs.stream().map(Revision::getRev).collect(Collectors.toList());
    }
}
