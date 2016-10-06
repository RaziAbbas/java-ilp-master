package org.interledger.ilp.core;

import java.util.regex.Pattern;

public class TransferID {
    final private static String SREGEX = // Must be similar to 3a2a1d9e-8640-4d2d-b06c-84f2cd613204
            "[0-9a-fA-F]+{8}-[0-9a-fA-F]+{4}-[0-9a-fA-F]+{4}-[0-9a-fA-F]+{4}-[0-9a-fA-F]+{12}";
    final private static Pattern regex = Pattern.compile(SREGEX);
    public final String transferID;
        
    public TransferID(String transferID) {
        if (transferID == null) throw new RuntimeException("transferID can't be null");
        java.util.regex.Matcher m = regex.matcher(transferID);
        if (!m.matches()) {
            throw new IllegalArgumentException(
                    "transferID '" + transferID + "' doesn't match " + SREGEX);
        }
        this.transferID = transferID;
    }
}
