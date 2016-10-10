package org.interledger.ilp.core;
//FIXME: Move to cryptoconditions?

public class FulfillmentURI {

    public final String URI;

    public FulfillmentURI(String URI) {
        // FIXME: Check URI format
        this.URI = URI;
    }
}
