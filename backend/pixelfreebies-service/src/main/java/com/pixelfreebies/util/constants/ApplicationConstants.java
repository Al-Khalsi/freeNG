package com.pixelfreebies.util.constants;

import org.springframework.http.HttpHeaders;

public final class ApplicationConstants {

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_DEFAULT_VALUE = "some_strong_hard_to_guess_default_value";
    public static final String JWT_AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String PIXELFREEBIES_SUFFIX = " Pixelfreebies";

}
