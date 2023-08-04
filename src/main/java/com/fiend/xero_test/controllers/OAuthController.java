package com.fiend.xero_test.controllers;

import com.fiend.xero_test.services.XeroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);


    @Autowired
    private XeroService xeroService;

    @GetMapping("/xero/callback")
    public ResponseEntity<String> callback(OAuth2User principal) {
        logger.info("OAuth2 callback");
        String accessToken = xeroService.getAccessToken(principal);
        logger.info("Access Token: " + accessToken);
        // You now have the access token, you can use it to call the Xero API
        return ResponseEntity.ok("Access Token: " + accessToken);
    }

    @GetMapping("/xero/callback1")
    public ResponseEntity<String> callback(@RequestHeader HttpHeaders headers, OAuth2User principal) {
        logger.info("Received request at /xero/callback endpoint");

        // Log the headers
        headers.forEach((key, value) -> {
            logger.info(String.format("Header '%s' = %s", key, value));
        });

        // If you want to log the full request, you might need to configure a filter or interceptor.

        String accessToken = xeroService.getAccessToken(principal);
        logger.info("Access Token: " + accessToken);

        return ResponseEntity.ok("Access Token: " + accessToken);
    }
}
