package com.fiend.xero_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication

public class XeroAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(XeroAuthApplication.class, args);
    }

    // https://stackoverflow.com/questions/52653836/how-to-open-browser-automatically-after-spring-boot-app-starts
    @EventListener({ApplicationReadyEvent.class})
    void applicationReadyEvent() {
        System.out.println("Application started ... launching browser now");
        browse("https://localhost:9090/");
    }

    // https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
    public static void browse(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();
            try {
                if (os.contains("win")) {
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    runtime.exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                    runtime.exec("xdg-open " + url);
                } else {
                    System.err.println("Cannot open browser on this platform.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
