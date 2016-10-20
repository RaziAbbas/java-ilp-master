package org.interledger.ilp.ledger.account;

import org.interledger.ilp.core.exceptions.InterledgerException;

/**
 *
 * @author mrmx
 */
public class AccountExistsException extends InterledgerException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of {@code AccountExistsException} without
     * detail message.
     */
    public AccountExistsException() {
    }

    /**
     * Constructs an instance of {@code AccountExistsException} with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AccountExistsException(String msg) {
        super(msg);
    }
}
