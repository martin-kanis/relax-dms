package org.fit.vutbr.relaxdms.data.db.system.configuration;

import java.util.Properties;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.fit.vutbr.relaxdms.data.system.configuration.ConfigurationService;

/**
 *
 * @author Martin Kanis
 */
@Stateless
public class ConfigurationServiceImpl implements ConfigurationService {

    @Inject
    private ConfigurationLoader config;
    
    @Override
    public String getDbHost() {
        Properties p = config.getProperties();
        return p.getProperty("dbHost");
    }
    
    @Override
    public String getDbName() {
        Properties p = config.getProperties();
        return p.getProperty("dbName");
    }

    @Override
    public String getDbShowPath() {
        Properties p = config.getProperties();
        return p.getProperty("dbShowPath");
    }  
}
