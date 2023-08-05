package com.fiend.xero_auth.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Service
public class TokenStorageService {

    public String get(Cookie[] cookies, String key) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(key))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public void clear(HttpServletResponse response) {
        List<String> cookieNames = Arrays.asList("jwt_token", "id_token", "access_token", "refresh_token", "expires_in_seconds", "xero_tenant_id");

        cookieNames.forEach(cookieName -> response.addCookie(new Cookie(cookieName, "")));
    }

    public void saveItem(HttpServletResponse response, String key, String value) {
        String sanitizedValue = sanitize(value);
        response.addCookie(new Cookie(key, sanitizedValue));
    }

    private String sanitize(String value) {
        StringBuilder sanitized = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c > 32 && c < 127) && c != 34 && c != 44) {
                sanitized.append(c);
            }
        }
        return sanitized.toString();
    }


    public void save(HttpServletResponse response, Map<String, String> cookies) {
        cookies.forEach((key, value) -> {
            String sanitizedValue = sanitize(value);
            response.addCookie(new Cookie(key, sanitizedValue));
        });
    }
}
