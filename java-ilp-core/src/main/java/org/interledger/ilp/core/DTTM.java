package org.interledger.ilp.core;

public class DTTM {
    public final String DTTM;
    
    public static final DTTM future = new DTTM("future");
    
    public DTTM(String DTTM) {
        // FIXME: Check format
        this.DTTM = DTTM;
    }
}
