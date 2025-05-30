package com.icispp.notificationservice.Entity;

import com.icispp.notificationservice.models.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
    public EmailDetails(Message message)
    {
        subject = message.getSubject();
        msgBody = message.getContent();
    }
}