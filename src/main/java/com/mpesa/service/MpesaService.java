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
 * M-Pesa Service Layer - The Heart of M-Pesa Integration
 *
 * This service handles the complex process of communicating with Safaricom's API.
 * Two main operations: 1) Get authentication token, 2) Send STK Push request
 *
 * Why this works: Follows Safaricom's exact authentication and request flow
 */
@Service // Spring will manage this as a singleton bean
public class MpesaService {

    private final MpesaConfig config; // Injected configuration containing all M-Pesa credentials
    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON conversion
    private final HttpClient httpClient = HttpClient.newHttpClient(); // For HTTP requests

    // Constructor injection - Spring automatically provides MpesaConfig
    public MpesaService(MpesaConfig config) {
        this.config = config;
    }

    /**
     * Step 1: Authenticate with Safaricom to get access token
     *
     * Why this is needed: Every M-Pesa API call requires a valid OAuth2 token
     * How it works: Send consumer key + secret to get temporary access token
     *
     * @return Valid access token (expires in ~1 hour)
     * @throws Exception If authentication fails (wrong credentials, network issues)
     */
    public String getAccessToken() throws Exception {
        // Create Basic Auth credentials: "ConsumerKey:ConsumerSecret"
        String credentials = config.getConsumerKey() + ":" + config.getConsumerSecret();

        // Encode in Base64 format (OAuth2 requirement)
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        // Build OAuth request to Safaricom's token endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials"))
                .header("Authorization", "Basic " + encodedCredentials) // Standard OAuth2 Basic Auth
                .GET()
                .build();

        // Send request and extract access_token from JSON response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body()).get("access_token").asText();
    }

    /**
     * Step 2: Send STK Push request to customer's phone
     *
     * This is where the magic happens - customer gets payment prompt on their phone
     *
     * Critical Steps:
     * 1. Generate timestamp (for security)
     * 2. Create secure password (proves you own the shortcode)
     * 3. Build request with exact JSON structure Safaricom expects
     * 4. Send to Safaricom with OAuth token
     *
     * @param mapping Contains phone number and amount from your API client
     * @return Safaricom's response (success = MerchantRequestID + CheckoutRequestID)
     * @throws Exception If request fails (network, authentication, validation errors)
     */
    public String initiateStkPush(Mpesamapping mapping) throws Exception {
        // Generate current timestamp - used for security and tracking
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        /**
         * Generate secure password - This is KEY to why the integration works!
         * Format: Base64(ShortCode + Passkey + Timestamp)
         *
         * Example: Base64("174379" + "bfb279f9aa9b..." + "20250730143000")
         * This proves to Safaricom that you have the correct passkey for your shortcode
         */
        String password = Base64.getEncoder().encodeToString(
                (config.getShortCode() + config.getPasskey() + timestamp).getBytes(StandardCharsets.UTF_8)
        );

        // Build the STK Push request with all required fields
        StkPushRequest stkRequest = new StkPushRequest(
                config.getShortCode(),           // Your business number (who receives money)
                password,                        // Security password (proves you own the shortcode)
                timestamp,                       // Current time (for security and tracking)
                "CustomerPayBillOnline",         // Transaction type (STK Push to paybill)
                String.valueOf(mapping.getAmount()), // Amount as String (Safaricom requirement)
                mapping.getPhone(),              // Customer's phone (who pays)
                config.getShortCode(),           // Your business number (who receives)
                mapping.getPhone(),              // Phone to receive STK prompt (usually same as payer)
                config.getCallbackUrl(),         // Where Safaricom sends results
                "TestPayment",                   // Reference on customer's statement
                "Payment for goods"              // Description on customer's statement
        );

        // Convert Java object to JSON (using @JsonProperty annotations for correct field names)
        String jsonBody = objectMapper.writeValueAsString(stkRequest);

        // Send STK Push request to Safaricom with OAuth authentication
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + "/mpesa/stkpush/v1/processrequest"))
                .header("Authorization", "Bearer " + getAccessToken()) // OAuth2 Bearer token
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Send request and return Safaricom's response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();

        /**
         * Expected Success Response:
         * {
         *   "MerchantRequestID": "29115-34620561-1",
         *   "CheckoutRequestID": "ws_CO_191220191020363925",
         *   "ResponseCode": "0",
         *   "ResponseDescription": "Success. Request accepted for processing"
         * }
         *
         * At this point, customer receives STK Push prompt on their phone!
         */
    }
}
