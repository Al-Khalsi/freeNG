package com.pixelfreebies.model.enums;

import com.pixelfreebies.exception.PixelfreebiesException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailServiceProvider {

    GOOGLE("google"),
    MAIL_TRAP("mailtrap");

    private final String name;

    public static EmailServiceProvider fromName(String name) throws PixelfreebiesException {
        for (EmailServiceProvider provider : EmailServiceProvider.values()) {
            if (provider.getName().equalsIgnoreCase(name)) {
                return provider;
            }
        }
        throw new PixelfreebiesException("Invalid email service provider: " + name);
    }

}
