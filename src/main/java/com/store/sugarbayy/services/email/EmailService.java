package com.store.sugarbayy.services.email;

public interface EmailService {
    void sendEmail(String recipientEmail, String subject, String bodyHTML, boolean throwExceptionOnFailure);
}
