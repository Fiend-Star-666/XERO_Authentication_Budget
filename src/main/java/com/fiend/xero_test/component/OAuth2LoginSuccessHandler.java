package com.fiend.xero_test.component;

import com.fiend.xero_test.services.XeroService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);


    @Autowired
    private XeroService xeroService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        logger.info("Authentication successful");
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        logger.info("Token: {}", token);
        String accessToken = xeroService.getAccessToken(token.getPrincipal());
        logger.info("Access token: {}", accessToken);
        // Store the access token in HttpSession
        HttpSession session = request.getSession();
        logger.info("Session: {}", session);
        session.setAttribute("ACCESS_TOKEN", accessToken);
        logger.info("Session attribute: {}", session.getAttribute("ACCESS_TOKEN"));
        String targetUrl = determineTargetUrl(request, response, authentication);
        logger.info("Target URL: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        logger.info("Redirected to target URL");
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logger.info("Determining target URL");
        return "/budgets";  // The target URL after successful authentication
    }
}
