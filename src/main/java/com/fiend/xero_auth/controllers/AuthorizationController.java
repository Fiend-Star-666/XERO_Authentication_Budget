package com.fiend.xero_auth.controllers;

import com.fiend.xero_auth.services.TokenStorageService;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class AuthorizationController {
    final static Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    @Autowired
    private TokenStorageService tokenStorageService;

    @Value("${app.clientId}")
    private String clientId;

    @Value("${app.clientSecret}")
    private String clientSecret;

    @Value("${app.redirectURI}")
    private String redirectURI;

    @Value("${app.tokenServerUrl}")
    private String TOKEN_SERVER_URL;

    @Value("${app.authorizationServerUrl}")
    private String AUTHORIZATION_SERVER_URL;

    private final JsonFactory JSON_FACTORY = new JacksonFactory();

    @GetMapping("/")
    public ResponseEntity<Void> doGet(HttpServletResponse response) throws IOException {
        List<String> scopeList = Arrays.asList(
                "openid",
                "email",
                "profile",
                "offline_access",
                "accounting.settings",
                "accounting.transactions",
                "accounting.contacts",
                "accounting.journals.read",
                "accounting.reports.read",
                "accounting.attachments",
                "accounting.budgets.read"
        );

        String secretState = "secret" + new Random().nextInt(999_999);
        logger.info("scopeList: " + scopeList);
        tokenStorageService.saveItem(response, "state", secretState);
        logger.info("state: " + secretState);

        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                JSON_FACTORY,
                new GenericUrl(TOKEN_SERVER_URL),
                new ClientParametersAuthentication(clientId, clientSecret),
                clientId,
                AUTHORIZATION_SERVER_URL
        )
                .setScopes(scopeList)
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .build();

        logger.info("flow: " + flow);

        String url = flow.newAuthorizationUrl()
                .setClientId(clientId)
                .setScopes(scopeList)
                .setState(secretState)
                .setRedirectUri(redirectURI)
                .build();

        logger.info("url: " + url);

        return ResponseEntity.status(HttpServletResponse.SC_MOVED_TEMPORARILY)
                .location(URI.create(url))
                .build();
    }
}
