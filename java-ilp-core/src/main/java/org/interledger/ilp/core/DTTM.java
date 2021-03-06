package org.interledger.ilp.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DateTime with Zone ISO-8601 standard wrapper
 * <p>
 * Immutable thread-safe minimum implementation of ISO-8601
 * with some extra support for ILP protocol (FUTURE DateTimeZone constant) 
 * <p>
 * Java 1.8+ provides the standard java.time.ZonedDateTime but 
 * with extra functionality.
 *
 */
public class DTTM {

    private final Date date;
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

    public DTTM(String DTTMformatedString) {
        if (DTTMformatedString==null) {
            throw new RuntimeException("DTTM string constructor null");
        }
        try {
            this.date = sdf.parse(DTTMformatedString);
        } catch (ParseException e) {
            throw new RuntimeException("'" + DTTMformatedString + "' "
                + "couldn't be parsed as a date-time with format '" + pattern + "'");
        }
    }

    public static DTTM getNow() {
        return new DTTM(new Date());
    }

    @Override
    public String toString() {
        String result = sdf.format(date);
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof DTTM))return false;
        return date.equals(((DTTM)other).date);
    }
}
