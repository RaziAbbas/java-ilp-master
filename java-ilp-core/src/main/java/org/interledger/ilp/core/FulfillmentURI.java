package org.interledger.ilp.core;
//FIXME: Move to cryptoconditions?

public class FulfillmentURI {

    public final String URI;

    public static final FulfillmentURI EMPTY = new FulfillmentURI("cf:0:");
    public FulfillmentURI(String URI) {
        // FIXME: Check URI format
        this.URI = URI;
    }
}
