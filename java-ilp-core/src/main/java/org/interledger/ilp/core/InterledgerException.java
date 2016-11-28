package org.interledger.ilp.core;

// http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletResponse.html

/**
 * Base ILP exception
 *
 * @author mrmx
 */
public final class InterledgerException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final int BAD_REQUEST           = 400;
    private static final int UNAUTHORIZED          = 401;
    private static final int FORBIDDEN             = 403;
    private static final int NOT_FOUND             = 404;
    private static final int UNPROCESSABLE_ENTITY  = 422;
    private static final int INTERNAL_SERVER_ERROR = 500;

    public enum RegisteredException {
        /*
         * TODO: Avoid Oracle attachs. 
         * Providing detailed exception info can be used by attackants
         * about the internals of the server. Since we are treating withh
         * money minimal information must be provided.
         * Ussually BAD_REQUEST , INTERNAL_ERROR must be enough to let external
         * peers known whether the problem is on their request or in the internal
         * processing of such request.
         */
        // FIXME: This enum must be in the core package.
        InternalError             (INTERNAL_SERVER_ERROR, "InternalErrorError"),
        InsufficientAmountError   (INTERNAL_SERVER_ERROR, "InsufficientAmountError"),
        InsufficientPrecisionError(INTERNAL_SERVER_ERROR, "InsufficientPrecisionError"),
        LedgerTransferError       (INTERNAL_SERVER_ERROR, "LedgerTransferError"),
        MaximunDataSizeExceeded   (INTERNAL_SERVER_ERROR, "MaximunDataSizeExceededError"),
        TransferNotFoundError     (NOT_FOUND            , "TransferNotFoundError"),
        AccountExists             (INTERNAL_SERVER_ERROR, "AccountExistsError"),
        AccountNotFoundError      (NOT_FOUND            , "AccountNotFoundError"),
        FulfillmentNotFoundError  (NOT_FOUND            , "FulfillmentNotFoundError"),
        UnauthorizedError         (UNAUTHORIZED         , "UnauthorizedError"),
        ForbiddenError            (FORBIDDEN            , "UnauthorizedError"),
        BadRequestError           (BAD_REQUEST          , "BadRequestError"),
        MissingFulfillmentError   (NOT_FOUND            , "MissingFulfillmentError"),
        AlreadyRolledBackError    (UNPROCESSABLE_ENTITY , "AlreadyRolledBackError"),
        TransferNotConditionalError(UNPROCESSABLE_ENTITY , "TransferNotConditionalError"),
//        InvalidFulfillmentError   (HttpResponseStatus.UNPROCESSABLE_ENTITY , "InvalidFulfillmentError"),
        UnmetConditionError       (UNPROCESSABLE_ENTITY , "UnmetConditionError");
        
        
        
        private final int HTTPErrorCode;
        private final String sID;

        RegisteredException(int HTTPErrorCode, String sID) {
            this.HTTPErrorCode = HTTPErrorCode;
            this.sID = sID;
        }


        public int getHTTPErrorCode() {
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
