package com.kunlv.ddd.j.enode.example.consumer.commandhandles;

import com.kunlv.ddd.j.enode.core.ENodeAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author anruence@gmail.com
 */
@SpringBootApplication(scanBasePackages = "com.kunlv.ddd.j.enode")
@ImportAutoConfiguration(value = {ENodeAutoConfiguration.class, KafkaCommandConfig.class})
public class CommandApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommandApplication.class, args);
    }
}