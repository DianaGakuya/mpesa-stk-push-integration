package com.mpesa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.config.MpesaConfig;
import com.mpesa.dto.Mpesamapping;
import com.mpesa.dto.StkPushRequest;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * M-Pesa Service Layer - Core Business Logic
 *
 * This service handles all M-Pesa business operations and API communications
 * It acts as the bridge between your application and Safaricom's Daraja API
 *
 * Key Responsibilities:
 * - Authenticate with Safaricom API (OAuth2 access tokens)
 * - Generate secure passwords for STK Push requests
 * - Build and send STK Push requests to Safaricom
 * - Handle API responses and error scenarios
 * - Provide detailed logging for debugging and monitoring
 *
 * Architecture Pattern: Service Layer Pattern
 * - Controller handles HTTP requests/responses
 * - Service handles business logic and external API calls
 * - Configuration provides settings and credentials
 *
 * @Service: Marks this as a Spring service component for dependency injection
 */
@Service
public class MpesaService {

    // Configuration injection - Contains all M-Pesa settings and credentials
    private final MpesaConfig config;

    // JSON processing - Converts Java objects to/from JSON for API communication
    private final ObjectMapper objectMapper = new ObjectMapper();

    // HTTP client - Handles all HTTP requests to Safaricom API
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Constructor with dependency injection
     * Spring automatically injects MpesaConfig when creating this service
     *
     * @param config Configuration containing M-Pesa credentials and settings
     */
    public MpesaService(MpesaConfig config) {
        this.config = config;
    }

    /**
     * Get OAuth2 Access Token from Safaricom
     *
     * Before making any M-Pesa API calls, you need a valid access token
     * This method authenticates with Safaricom using your app credentials
     *
     * Process:
     * 1. Combine Consumer Key + Consumer Secret
     * 2. Encode credentials in Base64 format
     * 3. Send GET request to OAuth endpoint
     * 4. Extract access token from response
     *
     * Token Validity: Usually 1 hour (3600 seconds)
     *
     * @return Valid access token string for API authentication
     * @throws Exception If authentication fails or network issues occur
     */
    public String getAccessToken() throws Exception {
        try {
            System.out.println("DEBUG - Starting getAccessToken()");

            // Step 1: Prepare credentials for Basic Authentication
            // Format: "ConsumerKey:ConsumerSecret"
            String credentials = config.getConsumerKey() + ":" + config.getConsumerSecret();

            // Step 2: Encode credentials in Base64 format (required by OAuth2)
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            System.out.println("DEBUG - Credentials encoded successfully");

            // Step 3: Build OAuth endpoint URL
            String url = config.getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials";
            System.out.println("DEBUG - Attempting to connect to: " + url);

            // Step 4: Create HTTP GET request with Basic Authentication
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + encodedCredentials)
                    .GET()
                    .build();
            System.out.println("DEBUG - HTTP request created, sending...");

            // Step 5: Send request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("DEBUG - Response received. Status: " + response.statusCode());
            System.out.println("DEBUG - Response body: " + response.body());

            // Step 6: Extract access token from JSON response
            // Expected response: {"access_token":"aBcDeFg...", "expires_in":"3599"}
            return objectMapper.readTree(response.body()).get("access_token").asText();

        } catch (Exception e) {
            System.err.println("ERROR in getAccessToken: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Initiate STK Push (Lipa Na M-Pesa Online) Request
     *
     * This is the main method that triggers mobile money payment requests
     * It sends a payment prompt to the customer's phone for approval
     *
     * Process Flow:
     * 1. Generate timestamp for transaction tracking
     * 2. Create secure password using shortcode + passkey + timestamp
     * 3. Build STK Push request with all required fields
     * 4. Get fresh access token for API authentication
     * 5. Send STK Push request to Safaricom
     * 6. Return response to caller
     *
     * Customer Experience:
     * 1. Customer receives STK Push prompt on their phone
     * 2. Customer enters M-Pesa PIN to authorize payment
     * 3. Payment is processed and confirmation sent
     * 4. Transaction result is sent to your callback URL
     *
     * @param mapping Contains customer phone number and payment amount
     * @return JSON response from Safaricom (success/failure details)
     */
    public String initiateStkPush(Mpesamapping mapping) {
        try {
            System.out.println("DEBUG - Starting STK Push for phone: " + mapping.getPhone() + ", amount: " + mapping.getAmount());

            // Step 1: Generate timestamp for this transaction
            // Format: yyyyMMddHHmmss (e.g., "20250728143000")
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            System.out.println("DEBUG - Generated timestamp: " + timestamp);

            // Step 2: Generate secure password for STK Push authentication
            // Formula: Base64(ShortCode + Passkey + Timestamp)
            // This password proves you have the correct passkey for the shortcode
            String password = Base64.getEncoder().encodeToString(
                    (config.getShortCode() + config.getPasskey() + timestamp).getBytes(StandardCharsets.UTF_8)
            );
            System.out.println("DEBUG - Generated password successfully");

            // Step 3: Build STK Push request with all required fields
            StkPushRequest stkRequest = new StkPushRequest(
                    config.getShortCode(),           // Business receiving the payment
                    password,                        // Secure authentication password
                    timestamp,                       // Transaction timestamp
                    "CustomerPayBillOnline",         // Transaction type (STK Push)
                    String.valueOf(mapping.getAmount()), // Convert amount to String (CRITICAL FIX)
                    mapping.getPhone(),              // Customer's phone number (payer)
                    config.getShortCode(),           // Business shortcode (payee)
                    mapping.getPhone(),              // Phone number for STK Push prompt
                    config.getCallbackUrl(),         // Where Safaricom sends results
                    "TestPayment",                   // Reference on customer's statement
                    "Payment for goods"              // Description on customer's statement
            );
            System.out.println("DEBUG - Created STK request object");

            // Step 4: Convert request object to JSON format
            String jsonBody = objectMapper.writeValueAsString(stkRequest);
            System.out.println("DEBUG - JSON Body: " + jsonBody);

            // CRITICAL DEBUG: Let's see exact values being sent
            System.out.println("=== DEBUGGING STK PUSH REQUEST ===");
            System.out.println("BusinessShortCode: " + config.getShortCode());
            System.out.println("Consumer Key: " + config.getConsumerKey().substring(0, 10) + "...");
            System.out.println("Passkey: " + config.getPasskey().substring(0, 10) + "...");
            System.out.println("Generated Password: " + password.substring(0, 20) + "...");
            System.out.println("Timestamp: " + timestamp);
            System.out.println("==================================");

            // Step 5: Get fresh access token for API authentication
            System.out.println("DEBUG - Getting access token...");
            String accessToken = getAccessToken();
            System.out.println("DEBUG - Access token obtained: " + accessToken.substring(0, 10) + "...");

            // Step 6: Build HTTP POST request to STK Push endpoint
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/mpesa/stkpush/v1/processrequest"))
                    .header("Authorization", "Bearer " + accessToken)  // OAuth2 Bearer token
                    .header("Content-Type", "application/json")        // JSON content type
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            System.out.println("DEBUG - HTTP request created, sending to Safaricom...");

            // Step 7: Send request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("DEBUG - Response status: " + response.statusCode());
            System.out.println("DEBUG - Response body: " + response.body());

            // Step 8: Return Safaricom's response to the caller
            return response.body();

        } catch (Exception e) {
            // Error handling with detailed logging
            System.err.println("ERROR in initiateStkPush: " + e.getMessage());
            e.printStackTrace();

            // Return error in JSON format for consistent API responses
            return "{\"error\":\"" + e.getMessage() + "\",\"type\":\"" + e.getClass().getSimpleName() + "\"}";
        }
    }
}
