package org.interledger.ilp.core;
//FIXME: Move to cryptoconditions?

public class FulfillmentURI {

    public final String URI;

    public static final FulfillmentURI EMPTY = new FulfillmentURI("cf:0:");
    public static final FulfillmentURI MISSING = new FulfillmentURI("cf:-1:");
    
    public FulfillmentURI(String URI) {
        // FIXME: Check URI format
        this.URI = URI;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof FulfillmentURI))return false;
        return URI.equals(((FulfillmentURI)other).URI);
    }
}
