package com.pixelfreebies.util;

import com.pixelfreebies.model.domain.User;
import com.pixelfreebies.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;
    private final List<String> PERMITTED_URLS = new ArrayList<>();
    private @Value("${base.url}") String BASE_URL;

    @PostConstruct
    public void init() {
        this.PERMITTED_URLS.add("/");
        this.PERMITTED_URLS.add(this.BASE_URL + "/auth/**");
        this.PERMITTED_URLS.add(this.BASE_URL + "/file/download");
        // Swagger UI paths
        this.PERMITTED_URLS.add("/swagger-ui");
        this.PERMITTED_URLS.add("/swagger-ui.html");
        this.PERMITTED_URLS.add("/v3/api-docs");
        this.PERMITTED_URLS.add("/v2/api-docs");
        this.PERMITTED_URLS.add("/api-docs");
        this.PERMITTED_URLS.add("/swagger-resources");
        this.PERMITTED_URLS.add("/webjars");
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? this.userRepository.findByEmail(authentication.getName()).orElse(null) : null;
    }

}
