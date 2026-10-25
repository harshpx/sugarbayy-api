package com.store.sugarbayy.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.store.sugarbayy.configs.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final Resend resend;

    public EmailServiceImpl(@Value("${email.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Override
    public void sendEmail(String recipientEmail, String subject, String bodyHTML, boolean throwExceptionOnFailure) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Sugarbayy Support <support@sugarbayy.in>")
                .to(recipientEmail)
                .subject(subject)
                .html(bodyHTML)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email Sent Successfully, responseId:{}", response.getId());
        } catch (ResendException e) {
            log.error("Failed to send email to:{}, reason:{}", recipientEmail, e.getMessage());
            if (throwExceptionOnFailure) {
                throw new ApiException("Unable to send email at this moment, sorry for inconvenience :(", HttpStatus.valueOf(e.getStatusCode()));
            }
        }
    }
}
