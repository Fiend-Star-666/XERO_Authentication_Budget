package com.fiend.xero_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestXeroTestApplication {

    public static void main(String[] args) {
        SpringApplication.from(XeroTestApplication::main).with(TestXeroTestApplication.class).run(args);
    }

}
