package com.fiend.xero_auth.controllers;

import com.fiend.xero_auth.services.TokenRefresh;
import com.fiend.xero_auth.services.TokenStorageService;
import com.xero.api.ApiClient;
import com.xero.api.client.AccountingApi;
import com.xero.models.accounting.Budgets;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/xero")
public class BudgetController {

    final static Logger logger = LoggerFactory.getLogger(BudgetController.class);

    private static final long serialVersionUID = 1L;

    private AccountingApi accountingApi;

    private final TokenRefresh tokenRefresh;
    private final TokenStorageService tokenStorageService;

    @Autowired
    public BudgetController(TokenRefresh tokenRefresh, TokenStorageService tokenStorageService) {
        this.tokenRefresh = tokenRefresh;
        this.tokenStorageService = tokenStorageService;
    }

    @GetMapping("/getBudgets")
    public ResponseEntity<?> getAccountingBudgets(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Cookie[] cookies = request.getCookies();

        logger.info("cookies: " + Arrays.toString(cookies));

        String savedAccessToken = tokenStorageService.get(cookies, "access_token");
        String savedRefreshToken = tokenStorageService.get(cookies, "refresh_token");
        String xeroTenantId = tokenStorageService.get(cookies, "xero_tenant_id");

        logger.info("xeroTenantId: " + xeroTenantId);
        logger.info("savedAccessToken: " + savedAccessToken);
        logger.info("savedRefreshToken: " + savedRefreshToken);

        List<UUID> ids = new ArrayList<>();
        LocalDate dateTo = LocalDate.now();
        LocalDate dateFrom = LocalDate.now().minusDays(7);

        // Check expiration of token and refresh if necessary
        String accessToken = tokenRefresh.checkToken(response, savedAccessToken, savedRefreshToken);
        logger.info("Access Token: " + accessToken);

        accountingApi = AccountingApi.getInstance(new ApiClient());
        try {
            // Get All Budgets
            Budgets budgets = accountingApi.getBudgets(accessToken, xeroTenantId, null, null, null);
            logger.info("How many budgets did we find: " + budgets);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            logger.error("API calls failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
