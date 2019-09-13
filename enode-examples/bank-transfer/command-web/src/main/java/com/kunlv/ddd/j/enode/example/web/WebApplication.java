package com.kunlv.ddd.j.enode.example.web;

import com.kunlv.ddd.j.enode.core.ENodeAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.kunlv.ddd.j.enode"})
@ImportAutoConfiguration(value = {ENodeAutoConfiguration.class, KafkaConfig.class})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}