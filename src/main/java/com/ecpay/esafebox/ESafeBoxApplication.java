package com.ecpay.esafebox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication(scanBasePackages = {"com.ecpay.esafebox"})
@Slf4j
public class ESafeBoxApplication {

    public static void main(String[] args) {
        Long id = System.currentTimeMillis();
        log.info("{}---------Start eSafeBox application---------", id);
        SpringApplication app = new SpringApplication(ESafeBoxApplication.class);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String ipServer = "localhost";
        try {
            ipServer = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        log.info("----------------------------------------------------------");
        log.info("Application    :" + env.getProperty("spring.application.name"));
        log.info("Url            :" + protocol + "://" + ipServer + ":" + env.getProperty("server.port") + env.getProperty("server.servlet.context-path"));
        log.info("----------------------------------------------------------");

    }
}
