package com.icispp.notificationservice.services;

import com.icispp.notificationservice.Entity.EmailDetails;
import com.icispp.notificationservice.models.Message;
import com.icispp.notificationservice.models.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    public String sendSimpleMail(EmailDetails details) throws Exception
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(sender);
        mailMessage.setTo(details.getRecipient());
        mailMessage.setText(details.getMsgBody());
        mailMessage.setSubject(details.getSubject());
        javaMailSender.send(mailMessage);
        return "Mail Sent Successfully...";

    }

    public String sendSimpleMailToUser(Message message, User user){
        EmailDetails details = new EmailDetails(message);
        details.setRecipient(user.getEmail());
        try{
            sendSimpleMail(details);
            message.setDelivered(true);
            return "Mail Sent Successfully...";
        }
        catch (Exception e) {
            message.setDelivered(false);
            return "Error while Sending Mail";
        }
    }


    public String
    sendMailWithAttachment(EmailDetails details)
    {
        // Creating a mime message
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());

            // Adding the attachment
            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }

        // Catch block to handle MessagingException
        catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
