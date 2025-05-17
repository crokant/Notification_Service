package com.icispp.emailnotificationservice.services;

import com.icispp.emailnotificationservice.dto.SendEmailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    public boolean sendEmail(SendEmailMessage message){
        return true;
    }
}
