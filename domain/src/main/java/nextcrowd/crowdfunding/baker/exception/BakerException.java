package nextcrowd.crowdfunding.baker.exception;


import lombok.Getter;

@Getter
public class BakerException extends RuntimeException {
    public enum Reason {
        BAKER_NOT_FOUND
    }

    private Reason reason;

    public BakerException(Reason reason) {
        this.reason = reason;
    }

    public BakerException(String message, Reason reason) {
        super(message);
        this.reason = reason;
    }

    public BakerException(String message, Throwable cause, Reason reason) {
        super(message, cause);
        this.reason = reason;
    }

    public BakerException(Throwable cause, Reason reason) {
        super(cause);
        this.reason = reason;
    }

    public BakerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Reason reason) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "BakerException{" +
               "reason=" + reason +
               '}';
    }

}
