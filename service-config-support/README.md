# Java ILP common [![gitter][gitter-image]][gitter-url]

[gitter-image]: https://badges.gitter.im/interledger/java.svg
[gitter-url]: https://gitter.im/interledger/java

[HOCON]: https://github.com/typesafehub/config/blob/master/HOCON.md

>This project holds all interledger java implementation common code for configuration etc.


# Usage


## Configuration

The configuration package (`org.interledger.ilp.common.config`) contains a [Config](blob/master/src/main/java/org/interledger/ilp/common/config/Config) *façade* to a [Configuration](blob/master/src/main/java/org/interledger/ilp/common/config/core/Configuration.java) [implementation](blob/master/src/main/java/org/interledger/ilp/common/config/core/DefaultConfigurationImpl.java).

This Config object enforces the use of enums as keys as a better method than static constants. There is a central enumeration [`Key`](blob/master/src/main/java/org/interledger/ilp/common/config/Key) holding all keys needed as a common repository/namespace.

The access to the values is hiearchical (key1.key2) eg:

- connector.host
- user.name  

The format used in current implementation is [HOCON] that is very flexible json-like human friendly configuration format.

The default configuration file (*application.conf*) is loaded from the classpath. 

The [HOCON] format allows include more configurations from local files and urls too.

### Sample configuration.cfg:

```
#Java properties like:
key.subkey = A string value
key.subkey2 = 100 //An int value. Comments allowed here!!

//HOCON format:

key {
    subkey: A string value that overwrites previously defined key.subkey
    subkey2= 123 //Notice the = sign! Also overwrites previously defined key.subkey2
    more {
        nesting: is cool :-)
    }
}

```

### Sample code:

``` java
    import org.interledger.ilp.common.config.Config;
    import static org.interledger.ilp.common.config.Key.*;
    
    ...
    Config config = Config.load(); //COC load application.conf from classpath
    //Read a String:
    String host = config.getString(CONNECTOR,HOST);
    //Read an int:
    int port = config.getInt(CONNECTOR,PORT);
    //Read a boolean:
    boolean https = config.getBoolean(CONNECTOR,USE_HTTPS);
    
```

## Contributors

Any contribution is very much appreciated! [![gitter][gitter-image]][gitter-url]

## License

This code is released under the Apache 2.0 License. Please see [LICENSE](LICENSE) for the full text.
