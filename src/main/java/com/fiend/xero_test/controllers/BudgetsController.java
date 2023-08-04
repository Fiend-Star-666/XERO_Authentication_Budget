package com.fiend.xero_test.controllers;

import com.fiend.xero_test.services.XeroService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BudgetsController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetsController.class);


    @Autowired
    private XeroService xeroService;

    @GetMapping("/budgets")
    public ResponseEntity<String> getBudgets(HttpServletRequest request) {
        logger.info("getBudgets() called");
        HttpSession session = request.getSession();
        logger.info("session id: " + session.getId());
        String accessToken = (String) session.getAttribute("ACCESS_TOKEN");
        logger.info("accessToken: " + accessToken);
        String budgets = xeroService.getBudgets(accessToken);
        logger.info("budgets: " + budgets);
        return ResponseEntity.ok(budgets);
    }
}
