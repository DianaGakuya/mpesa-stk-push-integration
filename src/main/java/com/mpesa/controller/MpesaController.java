package com.mpesa.controller;

import com.mpesa.dto.Mpesamapping;
import com.mpesa.service.MpesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * M-Pesa REST Controller - The Entry Point for All M-Pesa Operations
 *
 * This controller exposes HTTP endpoints that clients (mobile apps, web apps) can call.
 * Think of it as the "front door" to your M-Pesa integration.
 *
 * Why it works: Follows REST API principles with clean JSON input/output
 */
@RestController // Combines @Controller + @ResponseBody (automatically converts responses to JSON)
@RequestMapping("/api/mpesa") // Base URL for all endpoints: http://localhost:8090/api/mpesa
@RequiredArgsConstructor // Lombok: generates constructor for final fields (dependency injection)
public class MpesaController {

    private final MpesaService mpesaService; // Spring automatically injects this service

    /**
     * POST /api/mpesa/stkpush - Initiate M-Pesa payment
     *
     * This is the main endpoint your clients will call to request payments.
     *
     * Flow:
     * 1. Client sends JSON with phone + amount
     * 2. Controller passes to MpesaService
     * 3. Service handles Safaricom API communication
     * 4. Customer gets STK Push on their phone
     * 5. Response returned to client
     *
     * Example request:
     * POST http://localhost:8090/api/mpesa/stkpush
     * {
     *   "phone": "254796022656",
     *   "amount": 100
     * }
     *
     * @param mapping Request body containing phone number and amount
     * @return Safaricom's response with MerchantRequestID and CheckoutRequestID
     */
    @PostMapping("/stkpush")
    public String initiateStkPush(@RequestBody Mpesamapping mapping) throws Exception {
        // Delegate to service layer for business logic
        return mpesaService.initiateStkPush(mapping);
    }

    /**
     * POST /api/mpesa/callback - Handle transaction results from Safaricom
     *
     * Safaricom calls this endpoint to notify you about transaction status.
     * This happens AFTER the customer enters their M-Pesa PIN.
     *
     * Important: This endpoint is called by Safaricom, not your clients!
     *
     * Callback scenarios:
     * - Success: Customer paid successfully
     * - Failed: Customer cancelled or insufficient funds
     * - Timeout: Customer didn't respond in time
     *
     * @param callback JSON payload from Safaricom with transaction results
     */
    @PostMapping("/callback")
    public void callback(@RequestBody String callback) {
        System.out.println("Callback Response: " + callback);

        // TODO in production:
        // 1. Parse callback JSON
        // 2. Update your database with transaction status
        // 3. Send confirmation to customer
        // 4. Update your business systems
    }
}
