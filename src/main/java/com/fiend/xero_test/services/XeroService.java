package com.fiend.xero_test.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class XeroService {

    private static final Logger logger = LoggerFactory.getLogger(XeroService.class);


    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public String getAccessToken(OAuth2User principal) {
        logger.info("principal: {}", principal);
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient("xero", principal.getName());
        logger.info("authorizedClient: {}", authorizedClient);
        return authorizedClient.getAccessToken().getTokenValue();
    }

    public String getBudgets(String accessToken) {
        logger.info("accessToken: {}", accessToken);
        RestTemplate restTemplate = new RestTemplate();
        logger.info("restTemplate: {}", restTemplate);
        HttpHeaders headers = new HttpHeaders();
        logger.info("headers: {}", headers);
        headers.setBearerAuth(accessToken);
        logger.info("headers: {}", headers);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        logger.info("entity: {}", entity);
        ResponseEntity<String> response = restTemplate.exchange("https://api.xero.com/api.xro/2.0/Budgets", HttpMethod.GET, entity, String.class);
        logger.info("response: {}", response);
        return response.getBody();
    }
}
