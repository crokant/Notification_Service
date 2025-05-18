package com.icispp.notificationservice.services;
import com.icispp.notificationservice.Entity.EmailDetails;

public interface EmailService {

    String sendSimpleMail(EmailDetails details) throws Exception;
    String sendMailWithAttachment(EmailDetails details);
}