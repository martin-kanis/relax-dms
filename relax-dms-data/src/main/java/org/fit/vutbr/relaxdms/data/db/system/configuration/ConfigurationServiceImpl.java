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
