package com.icispp.emailnotificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendEmailMessage {
    Set<String> to;
    String from;
    String subject;
    String body;
}
