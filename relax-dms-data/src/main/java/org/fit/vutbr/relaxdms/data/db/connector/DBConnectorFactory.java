/* 
 * The MIT License
 *
 * Copyright 2017 mkanis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
