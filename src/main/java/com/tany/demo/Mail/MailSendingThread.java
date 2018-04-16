package com.tany.demo.Mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class MailSendingThread implements Callable<MailResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSendingThread.class);
    /**
     * 邮箱验证正则表达式
     */
    private static final Pattern PATTERN = Pattern.compile("^([a-z0-9A-Z]+[-_.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$");

    private JavaMailSenderImpl javaMailSender;
    private MailMessage mailMessage;

    public MailSendingThread(JavaMailSenderImpl javaMailSender, MailMessage mailMessage) {
        // 检查参数
        check(mailMessage);

        this.javaMailSender = javaMailSender;
        this.mailMessage = mailMessage;
    }

    @Override
    public MailResult call() {
        try {
            LOGGER.info("开始发邮件...");
            javaMailSender.send(new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimemessage) throws Exception {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimemessage, true, "UTF-8");
                    messageHelper.setFrom(javaMailSender.getUsername());
                    messageHelper.setTo(mailMessage.getRecipients());
                    messageHelper.setSubject(mailMessage.getSubject());
                    // 添加邮件内容
                    messageHelper.setText(mailMessage.getContent(), mailMessage.isHtml());
                    // 添加附件
                    String[] attachFilenames = mailMessage.getAttachFileNames();
                    if (attachFilenames != null && attachFilenames.length > 0) {
                        for (String attachFilename : attachFilenames) {
                            DataSource dataSource = new FileDataSource(attachFilename);
                            messageHelper.addAttachment(dataSource.getName(), dataSource);
                        }
                    }
                }
            });
            LOGGER.info("邮件发送完成...");
            return MailResult.success();
        } catch (Exception e) {
            LOGGER.error("邮件发送出错.", e);
            return MailResult.fail("邮件发送出错,错误信息：" + e.getMessage());
        }
    }

    /**
     * 检查输入参数
     *
     * @param messageInfo 邮件内容
     */
    private void check(MailMessage messageInfo) {
        // 收件人
        String[] recipients = messageInfo.getRecipients();
        if (recipients == null || recipients.length == 0) {
            throw new IllegalArgumentException("收件人为空");
        } else {
            for (int i = 0; i < recipients.length; ++i) {
                String recipient = recipients[i];
                if (!isEMail(recipient)) {
                    throw new IllegalArgumentException("邮箱账号格式错误：" + recipient);
                }
            }
        }

        // 邮件内容
        String content = messageInfo.getContent();
        if (null == content || "".equals(content.trim())) {
            throw new IllegalArgumentException("邮件内容为空");
        }

        // 附件
        String[] attachFileNames = messageInfo.getAttachFileNames();
        if (attachFileNames != null && attachFileNames.length > 0) {
            File file;
            for (String fileName : messageInfo.getAttachFileNames()) {
                file = new File(fileName);
                if (file == null || !file.exists()) {
                    throw new IllegalArgumentException("附件不存在");
                }
                if (!file.isFile()) {
                    throw new IllegalArgumentException("所选附件不是标准的文件格式");
                }
                // 单个文件不大于5MB
                if (file.length() > 5242880) {
                    throw new IllegalArgumentException("单个文件大小超过限制(5MB)");
                }
            }
        }
    }

    /**
     * 验证邮箱地址格式
     *
     * @param address 邮箱地址
     * @return 验证成功返回true 否则返回false
     */
    private boolean isEMail(String address) {
        return null != address && PATTERN.matcher(address).matches();
    }
}
