package org.interledger.ilp.common.api.core;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Base ILP exception
 *
 * @author mrmx
 */
public final class InterledgerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public enum RegisteredException {

        InternalError             (HttpResponseStatus.INTERNAL_SERVER_ERROR, "InternalErrorError"),
        InsufficientAmountError   (HttpResponseStatus.INTERNAL_SERVER_ERROR, "InsufficientAmountError"),
        InsufficientPrecisionError(HttpResponseStatus.INTERNAL_SERVER_ERROR, "InsufficientPrecisionError"),
        LedgerTransferError       (HttpResponseStatus.INTERNAL_SERVER_ERROR, "LedgerTransferError"),
        MaximunDataSizeExceeded   (HttpResponseStatus.INTERNAL_SERVER_ERROR, "MaximunDataSizeExceededError"),
        TransferNotFoundError     (HttpResponseStatus.NOT_FOUND            , "TransferNotFoundError"),
        AccountExists             (HttpResponseStatus.INTERNAL_SERVER_ERROR, "AccountExistsError"),
        AccountNotFoundError      (HttpResponseStatus.NOT_FOUND            , "AccountNotFoundError"),
        FulfillmentNotFoundError  (HttpResponseStatus.NOT_FOUND            , "FulfillmentNotFoundError"),
        UnauthorizedError         (HttpResponseStatus.UNAUTHORIZED         , "UnauthorizedError"),
        ForbiddenError            (HttpResponseStatus.FORBIDDEN            , "UnauthorizedError"),
        BadRequestError           (HttpResponseStatus.BAD_REQUEST          , "BadRequestError");
        private final HttpResponseStatus HTTPErrorCode;
        private final String sID;

        RegisteredException(HttpResponseStatus HTTPErrorCode, String sID) {
            this.HTTPErrorCode = HTTPErrorCode;
            this.sID = sID;
        }


        public HttpResponseStatus getHTTPErrorCode() {
            return HTTPErrorCode;
        }

        public String getsID() {
            return sID;
        }
    }
    final RegisteredException exception;
    final String description;
    final Throwable cause;
    
    public InterledgerException(RegisteredException exception, String description, Throwable cause) {
        this.exception = exception;
        this.description = description;
        this.cause = cause;
    }
    
    public RegisteredException getException() {
        return exception;
    }

    public String getDescription() {
        return description;
    }

    public Throwable getCause() {
        return cause;
    }

    public InterledgerException(RegisteredException exception) {
        this(exception,"", null);
    }

    public InterledgerException(RegisteredException exception, String description) {
        this(exception, description, null);
    }
}
