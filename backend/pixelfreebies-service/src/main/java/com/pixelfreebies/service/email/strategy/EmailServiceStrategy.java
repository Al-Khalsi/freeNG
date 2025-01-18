package com.pixelfreebies.service.email.strategy;

import com.pixelfreebies.exception.PixelfreebiesException;

@FunctionalInterface
public interface EmailServiceStrategy {

    void sendEmail(String to, String subject, String body) throws PixelfreebiesException;

}
