package com.tany.demo.Mail;

public class MailResult {
    private boolean success;
    private String message;

    public MailResult() {
    }

    public MailResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static MailResult success() {
        return new MailResult(true, "发送成功.");
    }

    public static MailResult fail(String message) {
        return new MailResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SmsResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
