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
 * M-Pesa Service - Handles STK Push operations
 */
@Service
public class MpesaService {

    private final MpesaConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public MpesaService(MpesaConfig config) {
        this.config = config;
    }

    /**
     * Get OAuth2 access token from Safaricom - DEFINITIVE FIX
     */
    public String getAccessToken() throws Exception {
        try {
            // Clean credentials
            String consumerKey = config.getConsumerKey().trim();
            String consumerSecret = config.getConsumerSecret().trim();

            String credentials = consumerKey + ":" + consumerSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials"))
                    .header("Authorization", "Basic " + encodedCredentials)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("=== OAUTH DEBUG ===");
            System.out.println("Status: " + response.statusCode());
            System.out.println("Response: " + response.body());
            System.out.println("Consumer Key: " + consumerKey.substring(0, 10) + "...");
            System.out.println("==================");

            if (response.statusCode() == 400) {
                throw new RuntimeException("CREDENTIALS EXPIRED! Get new sandbox credentials from: https://developer.safaricom.co.ke/");
            } else if (response.statusCode() != 200) {
                throw new RuntimeException("OAuth failed (Status: " + response.statusCode() + "): " + response.body());
            }

            var jsonResponse = objectMapper.readTree(response.body());
            if (!jsonResponse.has("access_token")) {
                throw new RuntimeException("Invalid OAuth response: " + response.body());
            }

            return jsonResponse.get("access_token").asText();

        } catch (Exception e) {
            System.err.println("❌ OAuth Failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Initiate STK Push payment request
     */
    public String initiateStkPush(Mpesamapping mapping) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String password = Base64.getEncoder().encodeToString(
                    (config.getShortCode() + config.getPasskey() + timestamp).getBytes(StandardCharsets.UTF_8)
            );

            StkPushRequest stkRequest = new StkPushRequest(
                    config.getShortCode(), password, timestamp, "CustomerPayBillOnline",
                    String.valueOf(mapping.getAmount()), mapping.getPhone(), config.getShortCode(),
                    mapping.getPhone(), config.getCallbackUrl(),
                    "TestPayment", "Payment for goods"
            );

            String jsonBody = objectMapper.writeValueAsString(stkRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/mpesa/stkpush/v1/processrequest"))
                    .header("Authorization", "Bearer " + getAccessToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\",\"type\":\"" + e.getClass().getSimpleName() + "\"}";
        }
    }
}
