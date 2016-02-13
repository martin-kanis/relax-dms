package org.fit.vutbr.relaxdms.backend.system;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

/**
 *
 * @author Martin Kanis
 */
public class LoggerProducer {
    @Produces
    Logger produce(InjectionPoint ip) {
        return Logger.getLogger(ip.getBean().getBeanClass());
    }
}
