package org.interledger.ilp.common.api.auth;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import org.interledger.ilp.common.api.auth.impl.SimpleAuthProvider;
import org.interledger.ilp.common.config.Config;
import org.interledger.ilp.common.config.core.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code AuthProvider} factory.
 * 
 * @author mrmx
 */
public class AuthHandlerFactory {
    private static final Logger log = LoggerFactory.getLogger(AuthHandlerFactory.class);
    
    enum Provider {
        Basic,
        Jdbc,
        Shiro;  
    }
    
    enum Auth {
        realm
    }
        
    public static AuthHandler create(Config config) {
        Config authConfig = config.getConfig(Auth.class);
        authConfig.debug();
        Provider provider = authConfig.getEnum(Provider.class);                 
        AuthProvider authProvider = null;
        AuthHandler authHandler = null;
        switch(provider) {
            case Basic:
                authProvider = new SimpleAuthProvider();
                String realm = authConfig.getString("ILP Ledger API",Auth.realm);
                authHandler = BasicAuthHandler.create(authProvider,realm);                
                break;
            default: // Jdbc, Shiro, break;
                throw new ConfigurationException("authProvider not defined for provider "+provider);
        }
        authConfig.apply(authProvider);
        log.debug("Created {} authProvider using {} impl",provider,authProvider.getClass().getSimpleName());                
        return authHandler;
    }
}
