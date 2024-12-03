package com.pixelfreebies.config.security.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!prod")
public class AuthorizationEvents {


    /**
     * Handle the AuthorizationDeniedEvent event and log the authorization failure.
     * <p>
     * This event is published by Spring Security when an authorization is denied.
     * </p>
     *
     * @param event the AuthorizationDeniedEvent
     */
    @EventListener
    public void onFailureAuthentication(final AuthorizationDeniedEvent event) {
        log.error("Authorization failed for user: {} - due to: {}",
                event.getAuthentication().get().getName(),
                event.getAuthorizationDecision().toString()
        );
    }

}
