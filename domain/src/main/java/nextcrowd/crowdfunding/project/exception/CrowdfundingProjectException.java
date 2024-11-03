package nextcrowd.crowdfunding.project.exception;

import lombok.Getter;

@Getter
public class CrowdfundingProjectException extends RuntimeException {

    public enum Reason {
        PROJECT_NOT_FOUND,
        INVALID_COMMAND,
        INVALID_PROJECT_STATUS,
        INVESTMENT_NOT_FOUND
    }

    private Reason reason;

    public CrowdfundingProjectException(Reason reason) {
        super("Crowdfunding project exception %s".formatted(reason));
        this.reason = reason;
    }

    public CrowdfundingProjectException(String message, Reason reason) {
        super(message);
        this.reason = reason;
    }

    public CrowdfundingProjectException(String message, Throwable cause, Reason reason) {
        super(message, cause);
        this.reason = reason;
    }

    public CrowdfundingProjectException(Throwable cause, Reason reason) {
        super(cause);
        this.reason = reason;
    }

    public CrowdfundingProjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Reason reason) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "CrowdfundingProjectException{" +
               "reason=" + reason +
               '}';
    }

}

