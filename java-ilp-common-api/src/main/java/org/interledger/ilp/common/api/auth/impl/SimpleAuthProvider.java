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
import org.interledger.ilp.common.config.Config;
import org.interledger.ilp.common.config.core.Configurable;
import org.interledger.ilp.common.config.core.ConfigurationException;

/**
 * Simple in-memory vertx {@code AuthProvider}.
 *
 * @author mrmx
 */
public class SimpleAuthProvider implements Configurable, AuthProvider {

    private final static String USER_NAME = "username";
    private final static String USER_PASS = "password";

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

    public void addUser(String username, String password) {
        if (!users.containsKey(username)) {
            users.put(username, new SimpleUser(username, password));
        }
    }

    @Override
    public void configure(Config config) throws ConfigurationException {
        List<String> users = config.getStringList(Key.users);
        for(String user : users) {
            addUser(user,config.getStringFor(user,"pass"));
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
        private final JsonObject principal;

        public SimpleUser(String username, String password) {
            this.username = username;
            this.password = password;
            this.principal = new JsonObject();
            principal.put(USER_NAME, username);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public int hashCode() {
            return username.hashCode();
        }

        @Override
        public String toString() {
            return username;
        }

        @Override
        protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
            resultHandler.handle(Future.succeededFuture());
        }

        @Override
        public JsonObject principal() {
            return principal;
        }

        @Override
        public void setAuthProvider(AuthProvider authProvider) {
            throw new UnsupportedOperationException("Not supported " + authProvider);
        }
    }

}
