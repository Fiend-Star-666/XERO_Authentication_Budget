package com.fiend.xero_auth.controllers;

import com.fiend.xero_auth.services.TokenStorageService;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.xero.api.ApiClient;
import com.xero.api.client.IdentityApi;
import com.xero.models.identity.Connection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
public class CallbackController {

    @Autowired
    private TokenStorageService TokenStorageService;

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

    final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    final JsonFactory JSON_FACTORY = new JacksonFactory();

    @GetMapping("/xero/callback")
    public ResponseEntity<Void> doGet(HttpServletResponse response, HttpServletRequest request, @RequestParam(required = false, defaultValue = "123") String code,
                                      @RequestParam String state) throws IOException {

        String secretState = TokenStorageService.get(request.getCookies(), "state");

        if (secretState.equals(state)) {

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

            DataStoreFactory DATA_STORE_FACTORY = new MemoryDataStoreFactory();

            AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                    HTTP_TRANSPORT, JSON_FACTORY, new GenericUrl(TOKEN_SERVER_URL),
                    new ClientParametersAuthentication(clientId, clientSecret), clientId, AUTHORIZATION_SERVER_URL)
                    .setScopes(scopeList).setDataStoreFactory(DATA_STORE_FACTORY).build();

            TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();

            ApiClient defaultIdentityClient = new ApiClient("https://api.xero.com", null, null, null, null);
            IdentityApi idApi = new IdentityApi(defaultIdentityClient);

            List<Connection> connection = idApi.getConnections(tokenResponse.getAccessToken(), null);

            TokenStorageService.saveItem(response, "jwt_token", tokenResponse.toPrettyString());
            TokenStorageService.saveItem(response, "id_token", tokenResponse.get("id_token").toString());
            TokenStorageService.saveItem(response, "access_token", tokenResponse.getAccessToken());
            TokenStorageService.saveItem(response, "refresh_token", tokenResponse.getRefreshToken());
            TokenStorageService.saveItem(response, "expires_in_seconds", tokenResponse.getExpiresInSeconds().toString());
            TokenStorageService.saveItem(response, "xero_tenant_id", connection.get(0).getTenantId().toString());

            return ResponseEntity.status(HttpServletResponse.SC_MOVED_TEMPORARILY)
                    .location(URI.create("./AuthenticatedResource")).build();
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }
}
