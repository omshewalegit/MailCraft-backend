package com.email_writer_sb;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailRequest {
    @JsonProperty("emailContent")
    private String EmailContent;
    private String tone;

    public EmailRequest(String emailContact, String tone) {
        EmailContent = emailContact;
        this.tone = tone;
    }

    public String getEmailContent() {
        return EmailContent;
    }

    public void setEmailContent(String emailContact) {
        EmailContent = emailContact;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    @Override
    public String toString() {
        return "EmailRequest{" +
                "EmailContact='" + EmailContent + '\'' +
                ", tone='" + tone + '\'' +
                '}';
    }
}
