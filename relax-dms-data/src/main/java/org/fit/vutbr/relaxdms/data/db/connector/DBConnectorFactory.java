package org.fit.vutbr.relaxdms.data.db.connector;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import lombok.Getter;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.RestTemplate;
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
    
    @Getter
    private RestTemplate restTemplate;
    
    @PostConstruct
    private void init() {
        HttpClient httpClient;
        httpClient = new StdHttpClient.Builder()
                .host("127.0.0.1")
                .port(5984)
                .username("admin")
                .password("password")
                .build();
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        couchDBConnector = dbInstance.createConnector("relax-dms", false);

        restTemplate = new RestTemplate(httpClient);
    }
    
    public CouchDbConnector get() {
        return couchDBConnector;
    }
}
