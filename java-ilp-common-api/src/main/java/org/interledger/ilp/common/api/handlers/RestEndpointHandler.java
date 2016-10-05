package org.interledger.ilp.common.api.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.interledger.ilp.common.api.util.JsonObjectBuilder;
import org.interledger.ilp.common.api.util.VertxUtils;

/**
 * Rest endpoint handler
 *
 * @author mrmx
 */
public abstract class RestEndpointHandler extends EndpointHandler {

    private final static String PARAM_ENCODE_PLAIN_JSON = "plainjson";
    private final static String MIME_JSON_WITH_ENCODING = "application/json; charset=utf-8";

    public RestEndpointHandler(String name) {
        this(name, name);
    }

    public RestEndpointHandler(String name, String uri) {
        super(name, uri);
    }

    @Override
    public void handle(RoutingContext context) {
        try {
            switch (context.request().method()) {
                case GET:
                    handleGet(context);
                    break;
                case POST:
                    handlePost(context);
                    break;
                case PUT:
                    handlePut(context);
                    break;
                default: // CONNECT, DELETE, HEAD, OPTIONS, OTHER, PATCH, TRACE:
                    break;
            }
        } catch (RestEndpointException rex) {
            response(context, rex.getResponseStatus(), rex.getResponse());
        } catch (Throwable t) {
            response(context, HttpResponseStatus.INTERNAL_SERVER_ERROR, t);
        }
    }

    protected JsonObject getBodyAsJson(RoutingContext context) {
        return VertxUtils.getBodyAsJson(context);
    }

    protected void handleGet(RoutingContext context) {
        response(context, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePost(RoutingContext context) {
        response(context, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected void handlePut(RoutingContext context) {
        response(context, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    protected Supplier<JsonObject> buildJSON(CharSequence id, CharSequence message) {
        return buildJSONWith("id", id, "message", message);
    }

    protected Supplier<JsonObject> buildJSONWith(Object... pairs) {
        return JsonObjectBuilder.create().with(pairs);
    }

    protected void response(RoutingContext context, HttpResponseStatus responseStatus) {
        response(context, responseStatus, (Throwable) null);
    }

    protected void response(RoutingContext context, HttpResponseStatus responseStatus, Throwable t) {
        response(context,
                responseStatus,
                buildJSON(responseStatus.codeAsText(),
                        t == null ? responseStatus.reasonPhrase() : t.getMessage()
                )
        );
    }

    protected void response(RoutingContext context, HttpResponseStatus responseStatus, Supplier<JsonObject> response) {
        response(context, responseStatus, response.get());
    }

    protected void response(RoutingContext context, HttpResponseStatus responseStatus, JsonObject response) {
        boolean plainEncoding = StringUtils.isNotBlank(context.request().getParam(PARAM_ENCODE_PLAIN_JSON));
        String jsonResponse = plainEncoding ? response.encode() : response.encodePrettily();
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MIME_JSON_WITH_ENCODING)
                .putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(jsonResponse.length()))
                .setStatusCode(responseStatus.code())
                .end(jsonResponse);
    }

    protected static class RestEndpointException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private HttpResponseStatus responseStatus;
        private JsonObject response;

        public RestEndpointException(HttpResponseStatus responseStatus, String msg) {
            this(responseStatus, new JsonObject().put("error", msg));
        }

        public RestEndpointException(HttpResponseStatus responseStatus, JsonObject response) {
            this.responseStatus = responseStatus;
            this.response = response;
        }

        public HttpResponseStatus getResponseStatus() {
            return responseStatus;
        }

        public JsonObject getResponse() {
            return response;
        }
    }

}
