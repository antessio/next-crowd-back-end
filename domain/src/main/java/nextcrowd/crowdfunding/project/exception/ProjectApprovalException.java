package nextcrowd.crowdfunding.project.exception;

import lombok.Getter;

@Getter
public class ProjectApprovalException extends RuntimeException {

    public enum Reason{
        PROJECT_NOT_FOUND,
        INVALID_COMMAND,
        INVALID_PROJECT_STATUS
    }

    private Reason reason;

    public ProjectApprovalException(Reason reason) {
        this.reason = reason;
    }

    public ProjectApprovalException(String message, Reason reason) {
        super(message);
        this.reason = reason;
    }

    public ProjectApprovalException(String message, Throwable cause, Reason reason) {
        super(message, cause);
        this.reason = reason;
    }

    public ProjectApprovalException(Throwable cause, Reason reason) {
        super(cause);
        this.reason = reason;
    }

    public ProjectApprovalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Reason reason) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reason = reason;
    }

}

