package org.interledger.ilp.core;
// FIXME: Move to cryptoconditions?

public class ConditionURI {

    // TODO:(0) Use ConditionImpl?
    final private static String EMPTY_CONDITION = "cc:0:3::10"; // FIXME
    public final String URI;
    public static final ConditionURI EMPTY = new ConditionURI(EMPTY_CONDITION);

    // FIXME Make private and force using factory.
    private ConditionURI(String URI) {
        this.URI = URI;
    }

    /**
     * @param URI
     * @return new Condition or (default) EMPTY one
     */
    public static ConditionURI build(String URI){
        if (URI.equals(EMPTY_CONDITION)) {
            // TODO: Improve URI equal "EMPTY CONDITION" logic
            return EMPTY;
        }
        return new ConditionURI(URI);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ConditionURI))return false;
        // FIXME:? Add logic for "EMPTY conditions" than can have a 
        //     different URI representation
        return URI.equals(((ConditionURI)other).URI);
    }
    
    @Override
    public String toString() {
        return URI;
    }
}
