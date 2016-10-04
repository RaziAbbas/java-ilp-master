package org.interledger.ilp.connector.api.handlers;

import io.vertx.ext.web.RoutingContext;
import org.interledger.ilp.common.api.handlers.EndpointHandler;

/**
 * Health handler
 *
 * @author mrmx
 */
public class HealthHandler extends EndpointHandler {

    public HealthHandler() {
        super("health");
    }

    public static EndpointHandler create() {
        return new HealthHandler();
    }

    /**
     * //TBD
     *
     * @param context
     */
    @Override
    public void handle(RoutingContext context) {
        context.response().end("OK");
    }

}
