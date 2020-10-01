package com.ecpay.esafebox;

import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;

import com.ecpay.esafebox.utils.Constants;


@EntityScan(basePackages = {"com.ecpay.entities.ecbox"})
@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {"com.ecpay.esafebox"})
public class ESafeBoxApplication {
	private static final Logger LOGGER = LogManager.getLogger(Constants.LOGGER_APPENDER.APPLICATION);
    public static void main(String[] args){
    	long id = System.currentTimeMillis();
		LOGGER.info("[B][" + id + "] >>>>>>>>>>>>>>>>>>>>>>>>>> Start Ecpay ESafebox Application ...");
		SpringApplication app = new SpringApplication(ESafeBoxApplication.class);
		Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String ipServer = "localhost";
        try {
            ipServer = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            ipServer = env.getProperty("server.address") != null ? env.getProperty("server.address") : "localhost";
        }
        LOGGER.info("----------------------------------------------------------");
        LOGGER.info("   Application         : " + env.getProperty("spring.application.name"));
        LOGGER.info("   Url                 : " + protocol + "://" + ipServer + ":" + env.getProperty("server.port") + env.getProperty("server.servlet.context-path") + "/swagger-ui.html");
        LOGGER.info("   Profile(s)          : " + (env.getActiveProfiles().length != 0 ? env.getActiveProfiles()[0] : "default"));
        LOGGER.info("----------------------------------------------------------");

        LOGGER.info("[E][" + id + "][Duration = " + (System.currentTimeMillis() - id) + "] >>>>>>>>>>>>>>>>>>>>>>>>>> Ecpay ESafebox Application STARTED!");
    }
}
