package com.fiend.xero_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestXeroTestApplication {

    public static void main(String[] args) {
        SpringApplication.from(XeroAuthApplication::main).with(TestXeroTestApplication.class).run(args);
    }

}
