package com.holosumary.holosummary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HoloSummaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoloSummaryApplication.class, args);
    }

}
