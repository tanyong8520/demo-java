package com.tany.demo.Mail;

import java.util.Arrays;

public class MailMessage {
    /**
     * 收件人列表
     */
    private String[] recipients;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 是否发送网页内容
     */
    private boolean html;
    /**
     * 附件名称
     */
    private String[] attachFileNames;

    public MailMessage() {
    }

    public MailMessage(String[] recipients, String subject, String content) {
        this(recipients, subject, content, false, null);
    }

    public MailMessage(String[] recipients, String subject, String content, boolean html) {
        this(recipients, subject, content, html, null);
    }

    public MailMessage(String[] recipients, String subject, String content, boolean html, String[] attachFileNames) {
        this.recipients = recipients;
        this.subject = subject;
        this.content = content;
        this.html = html;
        this.attachFileNames = attachFileNames;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getAttachFileNames() {
        return attachFileNames;
    }

    public void setAttachFileNames(String[] attachFileNames) {
        this.attachFileNames = attachFileNames;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    @Override
    public String toString() {
        return "MailMessage{" +
                "recipients=" + Arrays.toString(recipients) +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", html=" + html +
                ", attachFileNames=" + Arrays.toString(attachFileNames) +
                '}';
    }
}
