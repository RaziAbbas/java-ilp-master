package org.interledger.ilp.common.api.util;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Various number conversion utility methods
 * 
 * @author mrmx
 */
public class NumberConversionUtils {
    
    public static Number toNumber(Object value) {
        return toNumber(value, 0);
    }
    
    public static Number toNumber(Object value,Number defaultValue) {
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof Number) {
            return (Number) value;
        }        
        return NumberUtils.createNumber(value.toString());
    }
}
