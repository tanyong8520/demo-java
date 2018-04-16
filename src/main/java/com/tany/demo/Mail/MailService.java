package com.tany.demo.Mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSenderImpl javaMailSender;
    @Autowired
    private ExecutorService executorService;

    private MailResult send(String subject, String content, boolean isHtml, boolean isAsync, String[] attachFileNames,
                            String[] recipients) {

        MailMessage message = new MailMessage();
        message.setRecipients(recipients);
        message.setSubject(subject);
        message.setContent(content);
        message.setHtml(isHtml);
        message.setAttachFileNames(attachFileNames);

        MailSendingThread sendingThread = new MailSendingThread(javaMailSender, message);
        Future<MailResult> future = executorService.submit(sendingThread);
        // 不是异步发送时返回发送结果
        if (!isAsync) {
            MailResult mailResult;
            try {
                mailResult = future.get();
            } catch (InterruptedException e) {
                LOG.error("线程中的异常.", e);
                mailResult = MailResult.fail("线程中断异常,错误信息：" + e);
            } catch (ExecutionException e) {
                LOG.error("线程执行异常.", e);
                mailResult = MailResult.fail("线程执行异常,错误信息：" + e);
            }
            return mailResult;
        }
        return MailResult.success();
    }
}
