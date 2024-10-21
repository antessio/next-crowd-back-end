package nextcrowd.crowdfunding.loan.exception;

import lombok.Getter;

@Getter
public class LoanException extends RuntimeException {

    public enum Reason{
        LOAN_NOT_FOUND,
        INVALID_COMMAND,
        INVALID_LOAN_STATUS
    }

    private Reason reason;

    public LoanException(Reason reason) {
        this.reason = reason;
    }

    public LoanException(String message, Reason reason) {
        super(message);
        this.reason = reason;
    }

    public LoanException(String message, Throwable cause, Reason reason) {
        super(message, cause);
        this.reason = reason;
    }

    public LoanException(Throwable cause, Reason reason) {
        super(cause);
        this.reason = reason;
    }

    public LoanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Reason reason) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "LoanException{" +
               "reason=" + reason +
               '}';
    }

}

