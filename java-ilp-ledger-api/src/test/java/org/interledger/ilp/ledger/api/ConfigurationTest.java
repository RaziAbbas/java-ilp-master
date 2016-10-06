package org.interledger.ilp.ledger.api;


import static org.junit.Assert.*;

import org.interledger.ilp.common.api.AbstractMainEntrypointVerticle;
import org.interledger.ilp.common.config.Config;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
        Config.create(); // Will fail if parsing application.conf and/or app.conf fails.
        assertTrue(true);
    }

}
