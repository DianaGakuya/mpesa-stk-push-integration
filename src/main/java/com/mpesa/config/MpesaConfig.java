package com.mpesa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * M-Pesa Configuration Class
 *
 * This class manages all M-Pesa related configuration properties from application.properties
 * It uses Spring's @Value annotation to inject configuration values at runtime
 *
 * Purpose:
 * - Centralize all M-Pesa API credentials and settings
 * - Provide clean access to configuration across the application
 * - Enable easy switching between sandbox and production environments
 *
 * @Configuration: Marks this as a Spring configuration class
 * @Getter/@Setter: Lombok annotations that auto-generate getter/setter methods
 */
@Getter
@Setter
@Configuration
public class MpesaConfig {

    /**
     * Safaricom M-Pesa API Base URL
     * Sandbox: https://sandbox.safaricom.co.ke
     * Production: https://api.safaricom.co.ke
     */
    @Value("${mpesa.base-url}")
    private String baseUrl;

    /**
     * Consumer Key from Safaricom Developer Portal
     * This is your app's public identifier for API authentication
     * Used together with Consumer Secret to generate access tokens
     */
    @Value("${mpesa.consumer-key}")
    private String consumerKey;

    /**
     * Consumer Secret from Safaricom Developer Portal
     * This is your app's private key for API authentication
     * NEVER expose this in client-side code or public repositories
     */
    @Value("${mpesa.consumer-secret}")
    private String consumerSecret;

    /**
     * Business Short Code (Paybill/Till Number)
     * For STK Push, this is the business number customers will pay to
     * Sandbox: Usually 174379 or assigned by Safaricom
     * Production: Your actual business paybill/till number
     */
    @Value("${mpesa.shortcode}")
    private String shortCode;

    /**
     * Passkey for generating STK Push passwords
     * This is provided by Safaricom for your specific shortcode
     * Used to generate secure passwords for STK Push requests
     */
    @Value("${mpesa.passkey}")
    private String passkey;

    /**
     * Callback URL where Safaricom sends transaction results
     * Must be a publicly accessible HTTPS URL
     * Safaricom will POST transaction results to this endpoint
     *
     * For development: Use ngrok or similar tools to expose localhost
     * For production: Use your actual domain
     */
    @Value("${mpesa.callback-url}")
    private String callbackUrl;
}
