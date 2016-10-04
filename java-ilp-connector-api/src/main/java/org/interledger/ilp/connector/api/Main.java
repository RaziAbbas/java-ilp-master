package org.interledger.ilp.connector.api;

import java.util.Arrays;
import java.util.List;
import org.interledger.ilp.common.api.AbstractMainEntrypointVerticle;
import org.interledger.ilp.common.api.handlers.EndpointHandler;
import org.interledger.ilp.common.api.util.VertxRunner;
import org.interledger.ilp.common.config.Config;
import org.interledger.ilp.common.config.core.Configurable;
import org.interledger.ilp.common.config.core.ConfigurationException;
import org.interledger.ilp.connector.api.handlers.HealthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vertx main entry point
 *
 * @author mrmx
 */
public class Main extends AbstractMainEntrypointVerticle implements Configurable {

    private static final Logger log = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        VertxRunner.run(Main.class);
    }

    @Override
    public void configure(Config config) throws ConfigurationException {
        
    }
        
    @Override
    protected List<EndpointHandler> getEndpointHandlers(Config config) {
        return Arrays.asList(
                HealthHandler.create()
        );
    }

    
}
