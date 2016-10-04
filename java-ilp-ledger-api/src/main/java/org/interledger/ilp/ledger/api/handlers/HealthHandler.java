package org.interledger.ilp.ledger.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import org.interledger.ilp.common.api.handlers.EndpointHandler;
import org.interledger.ilp.common.api.handlers.RestEndpointHandler;

/**
 * Health handler
 *
 * @author mrmx
 */
public class HealthHandler extends RestEndpointHandler {

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
    protected void handleGet(RoutingContext context) {
        response(context,HttpResponseStatus.OK,buildJSONWith("status","OK"));
    }

}
