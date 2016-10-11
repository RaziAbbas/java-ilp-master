package org.interledger.ilp.common.api;

import io.vertx.core.AbstractVerticle;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.interledger.ilp.common.api.auth.AuthHandlerFactory;
import org.interledger.ilp.common.api.handlers.DebugRequestHandler;
import org.interledger.ilp.common.api.handlers.EndpointHandler;
import org.interledger.ilp.common.api.handlers.IndexHandler;
import org.interledger.ilp.common.config.Config;
import static org.interledger.ilp.common.config.Key.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vertx main entry point base verticle.
 *
 * @author mrmx
 */
public abstract class AbstractMainEntrypointVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(AbstractMainEntrypointVerticle.class);

    protected static final String DEFAULT_PREFIX_URI = "/";
    protected static final String KEY_INDEX_URLS = "urls";

    private Config config;
    private HttpServer server;
    private HttpServerOptions serverOptions;
    private URL serverPublicURL;
    private String prefixUri;
    private AuthHandler authHandler;

    @Override
    public void start() throws Exception {
        initConfig();
        Router router = Router.router(vertx);
        initRouter(router);
        initServer(router, serverOptions);
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopped {}", this);
        if (server != null) {
            server.close();
        }
    }

    protected abstract List<EndpointHandler> getEndpointHandlers(Config config);

    private void initConfig() throws Exception {
        config = Config.create();
        prefixUri = sanitizePrefixUri(config.getString(DEFAULT_PREFIX_URI, SERVER, PREFIX, URI));
        String host = config.getString("localhost", SERVER, HOST);
        String pubHost = config.getString(host, SERVER, PUBLIC, HOST);
        int port = config.getInt(SERVER, PORT);
        int pubPort = config.getInt(port, SERVER, PUBLIC, PORT);
        boolean ssl = config.getBoolean(SERVER, USE_HTTPS);
        boolean pubSsl = config.getBoolean(ssl, SERVER, PUBLIC, USE_HTTPS);
        serverOptions = new HttpServerOptions().setHost(host).setPort(port);
        if (ssl) {
            log.debug("Using SSL");
            //FIXME http://vertx.io/docs/vertx-core/java/#ssl
            String keyFile = config.getString(SERVER, TLS_KEY);
            String certFile = config.getString(SERVER, TLS_CERT);
            //Assume PEM encoding
            serverOptions.setPemKeyCertOptions(
                    new PemKeyCertOptions()
                    .setKeyValue(readRelativeFile(keyFile))
                    .setCertValue(readRelativeFile(certFile))
            );
        }
        serverPublicURL = new URL("http" + (pubSsl ? "s" : ""), pubHost, pubPort, prefixUri);
        log.debug("serverPublicURL: {}", serverPublicURL);

        //Init auth
        authHandler = AuthHandlerFactory.create(config);

        //Extra configuration on child classes:
        config.apply(this);
    }

    public URL getServerPublicURL() {
        return serverPublicURL;
    }
    
    protected void initRouter(Router router) {
        log.debug("init router");
        int requestBodyLimit = config.getInt(2, SERVER, REQUEST, LIMIT);
        router.route()
                .handler(BodyHandler.create().setBodyLimit(requestBodyLimit * 1024));

        if (config.getBoolean(false, SERVER, DEBUG)) {
            log.info("Enabled request debug");
            router.route("/*").handler(LoggerHandler.create(true, LoggerFormat.DEFAULT));
            router.route("/*").handler(new DebugRequestHandler());
            router.route("/*").handler(LoggerHandler.create(false, LoggerFormat.TINY)); //Log used time of request execution
        }
        initIndexHandler(router, IndexHandler.create());
    }

    protected void initServer(Router router, HttpServerOptions serverOptions) {
        log.debug("Init server");        
        server = vertx.createHttpServer(serverOptions);
        server.requestHandler  (router::accept);


        server.listen(listenHandler -> {
                    if (listenHandler.succeeded()) {
                        log.info("Server ready at {}:{} ({})",
                                serverOptions.getHost(), server.actualPort(),
                                serverPublicURL
                        );
                    } else {
                        log.error("Server failed listening at port {}",
                                server.actualPort());
                        vertx.close(completion -> {
                            System.exit(completion.succeeded() ? 1 : 2);
                        });

                    }
                });


    }

    protected void initIndexHandler(Router router, IndexHandler indexHandler) {
        List<EndpointHandler> endpointHandlers = getEndpointHandlers(config);
        if (endpointHandlers != null && !endpointHandlers.isEmpty()) {
            indexHandler.put(KEY_INDEX_URLS, publish(router, endpointHandlers));
        } else {
            log.warn("No public endpoints?");
        }
        router.route(HttpMethod.GET, prefixUri).handler(indexHandler);
    }

    private Map<String, EndpointHandler> publish(Router router, List<EndpointHandler> handlers) {
        Map<String, EndpointHandler> endpoints = new LinkedHashMap<>();
        String path;
        for (EndpointHandler handler : handlers) {
            endpoints.put(handler.getName(), handler);
            path = handlerPath(handler);
            checkProtectedEndpoint(router, handler, path);
            for (HttpMethod httpMethod : handler.getHttpMethods()) {
                log.debug("publishing {} endpoint {} at {}", httpMethod, handler.getClass().getName(), getEndpointUrl(path));
                router.route(httpMethod, path).handler(handler);
            }
        }
        return endpoints;
    }

    private void checkProtectedEndpoint(Router router, EndpointHandler handler, String path) {
        if (ProtectedResource.class.isAssignableFrom(handler.getClass())) {
            log.debug("protecting endpoint {} at {}", handler, getEndpointUrl(path));
            router.route(path).handler(authHandler);
        }
    }

    private String handlerPath(EndpointHandler handler) {
        try {
            URL url = new URL(serverPublicURL, paths(prefixUri, handler.getUri()));
            handler.setUrl(url);
            return url.getPath();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Buffer readRelativeFile(String fileName) throws IOException {
        File cwd = Paths.get(".").toAbsolutePath().normalize().toFile();
        log.debug("Loading file {}/{}", cwd, fileName);
        Buffer fileBuffer = vertx.fileSystem().readFileBlocking(new File(cwd, fileName).getCanonicalPath());
        log.debug("Loaded file {} with {} bytes", fileName, fileBuffer.length());
        return fileBuffer;
    }

    private String sanitizePrefixUri(String path) {
        if (StringUtils.isBlank(path)) {
            return DEFAULT_PREFIX_URI;
        }
        path = path.trim();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    private String paths(String parent, String... childs) {
        StringBuilder path = new StringBuilder();
        if (!"/".equals(parent)) {
            path.append(parent);
        }
        for (String child : childs) {
            path.append("/");
            path.append(child);
        }
        return path.toString();
    }

    private String getEndpointUrl(String path) {
        try {
            return new URL(serverPublicURL, path).toString();
        } catch (MalformedURLException ex) {
            log.error(path, ex);
        }
        return null;
    }

}
