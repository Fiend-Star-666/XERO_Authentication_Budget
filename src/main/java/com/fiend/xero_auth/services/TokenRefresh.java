package com.fiend.xero_auth.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TokenRefresh {
    final static Logger logger = LoggerFactory.getLogger(TokenRefresh.class);

    @Value("${app.clientId}")
    private String clientId;

    @Value("${app.clientSecret}")
    private String clientSecret;

    @Value("${app.token-server-url}")
    private String TOKEN_SERVER_URL;

    @Autowired
    private TokenStorageService tokenStorageService;


    public String checkToken(HttpServletResponse response, String accessToken, String refreshToken) throws Exception {
        logger.debug("checkToken()");
        logger.debug("accessToken: " + accessToken);
        logger.debug("refreshToken: " + refreshToken);

        logger.debug("TOKEN_SERVER_URL: " + TOKEN_SERVER_URL);
        logger.debug("clientId: " + clientId);
        logger.debug("clientSecret: " + clientSecret);
        String currToken = null;

        try {
            DecodedJWT jwt = JWT.decode(accessToken);

            if (jwt.getExpiresAt().getTime() > System.currentTimeMillis()) {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "------------------ Refresh Token : NOT NEEDED - return current token -------------------");
                }
                currToken = accessToken;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("------------------ Refresh Token : BEGIN -------------------");
                }
                try {
                    TokenResponse tokenResponse = new RefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                            new GenericUrl(TOKEN_SERVER_URL), refreshToken)
                            .setClientAuthentication(new BasicAuthentication(this.clientId, this.clientSecret))
                            .execute();
                    if (logger.isDebugEnabled()) {
                        logger.debug("------------------ Refresh Token : SUCCESS -------------------");
                    }

                    // DEMO PURPOSE ONLY - You'll need to implement your own token storage solution
                    tokenStorageService.saveItem(response, "jwt_token", tokenResponse.toPrettyString());
                    tokenStorageService.saveItem(response, "access_token", tokenResponse.getAccessToken());
                    tokenStorageService.saveItem(response, "refresh_token", tokenResponse.getRefreshToken());
                    tokenStorageService.saveItem(response, "expires_in_seconds", tokenResponse.getExpiresInSeconds().toString());

                    currToken = tokenResponse.getAccessToken();
                } catch (TokenResponseException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("------------------ Refresh Token : EXCEPTION -------------------");
                    }
                    if (e.getDetails() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Error: " + e.getDetails().getError());
                        }
                        if (e.getDetails().getErrorDescription() != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(e.getDetails().getErrorDescription());
                            }
                        }
                        if (e.getDetails().getErrorUri() != null) {

                            if (logger.isDebugEnabled()) {
                                logger.debug(e.getDetails().getErrorUri());
                            }
                        }
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("------------------ Refresh Token : EXCEPTION -------------------");
                            logger.debug(e.getMessage());
                        }
                    }
                }
            }

        } catch (JWTDecodeException exception) {
            if (logger.isDebugEnabled()) {
                logger.debug("------------------ Refresh Token : INVALID TOKEN -------------------");
                logger.debug(exception.getMessage());
            }
        }

        return currToken;
    }
}
