package org.interledger.ilp.ledger.api;


import static org.junit.Assert.*;

import org.interledger.ilp.common.config.Config;
import org.junit.Test;


/**
 * Simple ledger tests
 *
 * @author mrmx
 */
public class ConfigurationTest {
    //     private static final Logger log = LoggerFactory.getLogger(AbstractMainEntrypointVerticle.class);

    @Test
    public void testRunServer() {
        // TODO: Check othe config "stuff" in AbstractMainEntrypointVerticle.initConfig
        Config.create(); // Will raise and exception if parsing application.conf and/or app.conf fails.
        assertTrue(true);
    }

}
