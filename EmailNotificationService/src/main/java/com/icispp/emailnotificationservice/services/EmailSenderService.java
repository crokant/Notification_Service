package com.icispp.emailnotificationservice.services;

import com.icispp.emailnotificationservice.dto.SendEmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class EmailSenderService {

    @Value("${app.mail.sender-email}")
    private String email;

    @Autowired
    private JavaMailSender mailSender;
    /**
     * Отправляет простое текстовое письмо всем адресатам.
     *
     * @param message параметры письма: from, to, subject, body
     * @return true, если письмо успешно отправлено; false — при любых ошибках
     */
    public boolean sendEmail(SendEmailMessage message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(email);
            mail.setTo(message.getTo().toArray(new String[0]));
            mail.setSubject(message.getSubject());
            mail.setText(message.getBody());
            mailSender.send(mail);
            log.info("Email sent to " + message.getTo());
            return true;
        } catch (MailException ex) {
            log.error("Не удалось отправить email", ex);
            return false;
        }
    }

    @Scheduled(fixedDelayString = "PT10s")
    public void sendMail(){
        SendEmailMessage message = new SendEmailMessage();
        message.setSubject("Email Notification Service example");
        message.setBody("This is an example email notification service");
        message.setFrom(email);
        Set<String> recipients = new HashSet<>();
        recipients.add("ilasafronov96@gmail.com");
        message.setTo(recipients);
        log.info("Sending email to " + message.getTo());
        sendEmail(message);
    }

}
