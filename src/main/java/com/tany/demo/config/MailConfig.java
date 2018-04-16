package com.tany.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Value("${spring.mail.host:127.0.0.1}")
    private String host;
    @Value("${spring.mail.username:root}")
    private String username;
    @Value("${spring.mail.password:root}")
    private String password;

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", true);
        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }
}
