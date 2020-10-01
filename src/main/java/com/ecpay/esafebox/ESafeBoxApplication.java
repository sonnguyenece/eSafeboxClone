package com.ecpay.esafebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = {"com.ecpay.esafebox"})
public class ESafeBoxApplication {

    public static void main(String[] args){
		SpringApplication app = new SpringApplication(ESafeBoxApplication.class);
		Environment env = app.run(args).getEnvironment();

    }
}
