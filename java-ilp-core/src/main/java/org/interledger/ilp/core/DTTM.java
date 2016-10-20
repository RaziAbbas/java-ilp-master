package org.interledger.ilp.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DTTM {

    public final Date DTTM;
    // https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
    final private static String dateFormat = "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";
    static SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    public static String mockDate = null;
    public static final DTTM future = new DTTM("9999-12-31T23:59:59.999Z");

    private DTTM(String DTTM) {
        try {
            this.DTTM = sdf.parse(DTTM);
        } catch (ParseException e) {
            throw new RuntimeException("'" + DTTM + "' "
                    + "couldn't be parsed as a date-time with format '" + dateFormat + "'");
        }
    }
    
    public static DTTM c(String DTTM) {
        return new DTTM(DTTM);
    }

    public static DTTM getNow() {
        Date now = new Date(); // TODO: Check.
        return new DTTM(sdf.format(now));
    }
    
    @Override
    public String toString() {
        if (mockDate != null) return mockDate;
        return sdf.format(DTTM);//  ;
        
    }
}
