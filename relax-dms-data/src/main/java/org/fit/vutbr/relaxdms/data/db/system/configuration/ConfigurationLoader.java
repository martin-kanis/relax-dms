package org.fit.vutbr.relaxdms.data.db.system.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import lombok.Getter;
import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
@Singleton
@Startup
public class ConfigurationLoader {
    
    private static final String APP_DEFAULT_FILE_PATH = "META-INF/configuration/appDefault.properties";
    
    @Getter
    private Properties properties;
    
    private final Logger logger = Logger.getLogger(getClass());
    
    @PostConstruct
    public void init() {
        properties = loadConfigurationData();
    }
    
    private Properties loadConfigurationData() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();        
        InputStream configFileContents = classLoader.getResourceAsStream(APP_DEFAULT_FILE_PATH);
        Properties proper = new Properties();
        
        try {
            if (configFileContents == null) {
                logger.error("Could not parse properties file!");
            }
            proper.load(configFileContents);
            return proper;
        } catch (IOException ex) {
            logger.error("Could not parse properties file!");
        }
        
        return null;
    }
}
