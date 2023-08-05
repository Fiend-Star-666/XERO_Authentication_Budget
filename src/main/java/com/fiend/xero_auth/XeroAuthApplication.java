package com.fiend.xero_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.fiend.xero_auth.services")
@ComponentScan(basePackages = "com.fiend.xero_auth.controllers")
public class XeroAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(XeroAuthApplication.class, args);
    }

}
