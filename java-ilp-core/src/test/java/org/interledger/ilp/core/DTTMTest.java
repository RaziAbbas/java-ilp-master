package org.interledger.ilp.core;

import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.Ignore;
import org.junit.Test;

public class DTTMTest {
    // TODO: Add more test (compare objects, ...)
    @Test
    public void testCreateDTTM() {
        String input;
        DTTM parsedDate;
        input = "2999-12-31T23:59:59.999Z";
        parsedDate = new DTTM(input);
        assertEquals(input+".equals("+parsedDate.toString()+")", input , parsedDate.toString());

        input = "2016-11-14T14:27:09.825Z";
        parsedDate = new DTTM(input);
        assertEquals(input+".equals("+parsedDate.toString()+")", input , parsedDate.toString());
    }
    
    @Test(expected=RuntimeException.class)
    public void testCreateDTTMWrongFormat() {
        new DTTM("23:59:59.999");
    }
    
    
    
}
