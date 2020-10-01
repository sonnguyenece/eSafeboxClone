package com.ecpay.esafebox;


import com.ecpay.esafebox.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;


@SpringBootApplication(scanBasePackages = {"com.ecpay.esafebox"})
@Slf4j
public class ESafeBoxApplication {
    private static final Logger LOGGER = LogManager.getLogger(Constants.LOGGER_APPENDER.APPLICATION);
    public static void main(String[] args) {
        Long id = System.currentTimeMillis();
        LOGGER.info("{}---------Start eSafeBox application---------", id);
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

        LOGGER.info("----------------------------------------------------------");
        LOGGER.info("Application    :" + env.getProperty("spring.application.name"));
        LOGGER.info("Url            :" + protocol + "://" + ipServer + ":" + env.getProperty("server.port") + env.getProperty("server.servlet.context-path"));
        LOGGER.info("----------------------------------------------------------");

    }
}
