package org.interledger.ilp.core;
// FIXME: Move to cryptoconditions?
public class ConditionURI {
    // TODO:(0) Use ConditionImpl?
    public final String URI;
    public static final ConditionURI EMPTY = new ConditionURI("cc:0:3::10"); // FIXME. cc:0:...
    public ConditionURI(String URI) {
        this.URI = URI;
    }
}
