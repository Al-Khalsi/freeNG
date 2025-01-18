package com.pixelfreebies.service.email.factory;

import com.pixelfreebies.model.enums.EmailServiceProvider;
import com.pixelfreebies.service.email.strategy.EmailServiceStrategy;

@FunctionalInterface
public interface EmailServiceFactory {

    EmailServiceStrategy createEmailService(EmailServiceProvider provider);

}