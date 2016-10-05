package org.interledger.ilp.core;
// FIXME: Move to cryptoconditions?
public class ConditionURI {
    public final String URI;

    public ConditionURI(String URI) {
        // FIXME: Check URI format
        this.URI = URI;
    }
}
