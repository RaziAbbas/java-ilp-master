package org.interledger.ilp.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DTTM {

    public final Date date;
    // https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
    final private static String pattern = "yyyy'-'MM'-'dd'T'HH:mm:ss.SSS'Z'";
    static SimpleDateFormat sdf;
    static {
        sdf  = new SimpleDateFormat(pattern, new Locale("C"));
        sdf.setLenient(false);
    }

    public static final DTTM future = new DTTM("2999-12-31T23:59:59.999Z");

    private DTTM(Date date) {
        this.date = date;
    }

    private DTTM(String DTTMformatedString) {
        if (DTTMformatedString==null){
            throw new RuntimeException("DTTM string constructor null");
        }
        try {
            this.date = sdf.parse(DTTMformatedString);
        } catch (ParseException e) {
            throw new RuntimeException("'" + DTTMformatedString + "' "
                    + "couldn't be parsed as a date-time with format '" + pattern + "'");
        }
    }
    
    // TODO: Remove this static constructor. doesn't look to be useful
    public static DTTM c(String DTTM) {
        return new DTTM(DTTM);
    }

    public static DTTM getNow() {
        return new DTTM(new Date());
    }
    
    @Override
    public String toString() {
        String result = sdf.format(date);
        return result;//  ;
        
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof DTTM))return false;
        return date.equals(((DTTM)other).date);
    }
}
