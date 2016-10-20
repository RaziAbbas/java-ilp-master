package org.interledger.ilp.core;

//import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.Ignore;
import org.junit.Test;

public class DTTMTest {
    // TODO: Add more test (compare objects, ...)
    @Test
    public void testCreateDTTM() {
        DTTM.c("9999-99-23T23:59:59.999Z");
    }
    
    @Test(expected=RuntimeException.class)
    public void testCreateDTTMWrongFormat() {
        DTTM.c("23:59:59.999");
    }
    
}
