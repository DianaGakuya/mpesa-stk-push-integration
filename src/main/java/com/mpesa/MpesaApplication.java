package com.mpesa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * M-Pesa STK Push Integration - Main Application Entry Point
 *
 * This is where everything starts! When you run "mvnw spring-boot:run", this main method
 * is called and Spring Boot does all the magic to set up your M-Pesa integration.
 *
 * What happens when this starts:
 * 1. Spring Boot scans for @Service, @Controller, @Configuration classes
 * 2. Creates MpesaConfig bean with values from application.properties
 * 3. Creates MpesaService bean and injects MpesaConfig into it
 * 4. Creates MpesaController bean and injects MpesaService into it
 * 5. Starts embedded Tomcat web server on port 8090
 * 6. Your API endpoints become available at http://localhost:8090/api/mpesa/
 *
 * Architecture Overview:
 * Client → MpesaController → MpesaService → Safaricom API → Customer's Phone
 */
@SpringBootApplication // This single annotation does A LOT:
                      // @Configuration: Marks this as a configuration class
                      // @EnableAutoConfiguration: Automatically configures Spring Boot
                      // @ComponentScan: Scans for Spring components in com.mpesa package
public class MpesaApplication {

    /**
     * Application entry point - Everything starts here!
     *
     * When you run this application:
     * 1. Spring Boot initializes the application context
     * 2. Configures all beans and dependencies
     * 3. Starts the web server
     * 4. Your M-Pesa API becomes ready to handle requests
     *
     * You'll see logs like:
     * - "Started MpesaApplication in X seconds"
     * - "Tomcat started on port(s): 8090"
     *
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        SpringApplication.run(MpesaApplication.class, args);

        // After this line executes, your M-Pesa integration is LIVE and ready!
        // Clients can now POST to http://localhost:8090/api/mpesa/stkpush
    }
}
