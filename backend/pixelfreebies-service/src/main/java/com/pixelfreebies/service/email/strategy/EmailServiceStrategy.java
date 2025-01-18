package com.pixelfreebies.service.email.strategy;

@FunctionalInterface
public interface EmailServiceStrategy {

    void sendEmail(String to, String subject, String body);

}
