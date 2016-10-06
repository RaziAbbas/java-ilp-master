package org.interledger.ilp.common.api.auth.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.interledger.ilp.common.config.Config;
import org.interledger.ilp.common.config.core.Configurable;
import org.interledger.ilp.common.config.core.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple in-memory vertx {@code AuthProvider}.
 *
 * @author mrmx
 */
public class SimpleAuthProvider implements Configurable, AuthProvider {

    private static final Logger log = LoggerFactory.getLogger(SimpleAuthProvider.class);

    private final static String USER_NAME = "username";
    private final static String USER_PASS = "password";
    private final static String USER_ROLE = "role";

    private final Map<String, SimpleUser> users;

    /**
     * Configuration keys
     */
    private enum Key {
        users
    }

    public SimpleAuthProvider() {
        this.users = new HashMap<>();
    }

    public void addUser(String username, String password, String role) {
        if (!users.containsKey(username)) {
            users.put(username, new SimpleUser(username, password, role));
        }
    }

    @Override
    public void configure(Config config) throws ConfigurationException {
        List<String> users = config.getStringList(Key.users);
        for (String user : users) {
            addUser(user,
                    config.getStringFor(user, "pass"),
                    config.hasKey(user, USER_ROLE)
                    ? config.getStringFor(user, USER_ROLE) : null
            );
        }
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String username = authInfo.getString(USER_NAME);
        SimpleUser user = users.get(username);
        if (user != null && user.getPassword().equals(authInfo.getString(USER_PASS))) {
            resultHandler.handle(Future.succeededFuture(user));
        } else {
            resultHandler.handle(Future.failedFuture(username));
        }
    }

    public static final class SimpleUser extends AbstractUser {

        private final String username;
        private final String password;
        private final String role;
        private final JsonObject principal;

        public SimpleUser(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.principal = new JsonObject();
            principal.put(USER_NAME, username);
            principal.put(USER_ROLE, role);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        @Override
        public int hashCode() {
            return username.hashCode();
        }

        @Override
        public JsonObject principal() {
            return principal;
        }

        @Override
        protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
            if (StringUtils.isBlank(role)) {
                resultHandler.handle(Future.succeededFuture(true));
            } else if (StringUtils.isNotBlank(role) && role.equalsIgnoreCase(permission)) {
                resultHandler.handle(Future.succeededFuture(true));
            } else {
                log.debug("User {} has no permission {}", username, role);
                resultHandler.handle(Future.failedFuture(permission));
            }
        }

        @Override
        public void setAuthProvider(AuthProvider authProvider) {
            throw new UnsupportedOperationException("Not supported " + authProvider);
        }

        @Override
        public String toString() {
            return username;
        }

    }

}
