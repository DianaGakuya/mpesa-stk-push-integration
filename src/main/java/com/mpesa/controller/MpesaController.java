package com.mpesa.controller;

import com.mpesa.dto.Mpesamapping;
import com.mpesa.service.MpesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * M-Pesa REST Controller
 *
 * This controller handles all HTTP requests related to M-Pesa transactions
 * It acts as the entry point for client applications (mobile apps, web apps, etc.)
 *
 * Responsibilities:
 * - Expose REST API endpoints for M-Pesa operations
 * - Handle HTTP request/response mapping
 * - Delegate business logic to MpesaService
 * - Return appropriate HTTP responses to clients
 *
 * @RestController: Combines @Controller + @ResponseBody (returns JSON by default)
 * @RequestMapping: Base URL path for all endpoints in this controller
 * @RequiredArgsConstructor: Lombok annotation for dependency injection constructor
 */
@RestController
@RequestMapping("/api/mpesa")  // Base URL: http://localhost:8090/api/mpesa
@RequiredArgsConstructor
public class MpesaController {

    // Dependency injection - Spring automatically injects MpesaService instance
    private final MpesaService mpesaService;

    /**
     * STK Push Endpoint - Initiate Mobile Money Payment Request
     *
     * This endpoint triggers an STK Push (Lipa Na M-Pesa Online) request
     * When called, it sends a payment prompt to the customer's phone
     *
     * URL: POST http://localhost:8090/api/mpesa/stkpush
     *
     * Request Body Example:
     * {
     *   "phone": "254796022656",
     *   "amount": 100
     * }
     *
     * Success Response Example:
     * {
     *   "MerchantRequestID": "29115-34620561-1",
     *   "CheckoutRequestID": "ws_CO_191220191020363925",
     *   "ResponseCode": "0",
     *   "ResponseDescription": "Success. Request accepted for processing"
     * }
     *
     * @param mapping Contains phone number and amount from request body
     * @return JSON response from Safaricom M-Pesa API
     * @throws Exception If there are issues with API communication
     */
    @PostMapping("/stkpush")
    public String initiateStkPush(@RequestBody Mpesamapping mapping) throws Exception {
        // Delegate the STK Push logic to the service layer
        // The service handles:
        // 1. Getting access token from Safaricom
        // 2. Building STK Push request
        // 3. Sending request to Safaricom API
        // 4. Returning the response
        return mpesaService.initiateStkPush(mapping);
    }

    /**
     * Callback Endpoint - Receive Transaction Results from Safaricom
     *
     * Safaricom calls this endpoint after processing STK Push requests
     * It contains the final transaction status (success, failure, timeout, etc.)
     *
     * URL: POST http://localhost:8090/api/mpesa/callback
     *
     * This endpoint should:
     * - Parse the callback payload
     * - Update transaction status in database
     * - Notify the customer about payment status
     * - Log transaction details for reconciliation
     *
     * @param callback JSON payload from Safaricom containing transaction results
     */
    @PostMapping("/callback")
    public void callback(@RequestBody String callback) {
        // Log the callback for debugging and audit purposes
        System.out.println("Callback Response: " + callback);

        // TODO: In production, you should:
        // 1. Parse the callback JSON to extract transaction details
        // 2. Update transaction status in your database
        // 3. Send confirmation/failure notifications to customers
        // 4. Implement proper error handling and logging
    }
}
