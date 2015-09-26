package org.fit.vutbr.relaxdms.data.db.connector;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

/**
 *
 * @author Martin Kanis
 */
@Singleton
@Startup
public class DBConnectorFactory {
   
    private CouchDbConnector couchDBConnector;
    
    @PostConstruct
    private void init() {
        HttpClient httpClient = new StdHttpClient.Builder().build();
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        couchDBConnector = dbInstance.createConnector("relax-dms", true);
    }
    
    public CouchDbConnector get() {
        return couchDBConnector;
    }
}
