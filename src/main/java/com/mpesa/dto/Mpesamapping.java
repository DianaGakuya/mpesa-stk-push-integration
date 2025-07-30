package com.mpesa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for STK Push Operations - Your API's Input Format
 *
 * This class defines what data your clients must send to initiate payments.
 * It converts incoming JSON from clients into Java objects your code can work with.
 *
 * Simple but critical: Only 2 fields needed - phone number and amount!
 *
 * Example client request:
 * POST /api/mpesa/stkpush
 * {
 *   "phone": "254796022656",
 *   "amount": 100
 * }
 */
public class Mpesamapping {

    /**
     * Customer's phone number who will pay
     *
     * Format requirements:
     * - Must start with 254 (Kenya country code)
     * - Followed by 9 digits (the actual phone number)
     * - Example: "254796022656" (not "0796022656")
     *
     * Why this format: Safaricom requires international format for STK Push
     */
    @JsonProperty("phone") // Maps JSON field "phone" to this Java property
    private String phone;

    /**
     * Payment amount in Kenyan Shillings
     *
     * Important notes:
     * - This is an integer (whole numbers only)
     * - Represents KES amount (e.g., 100 = 100 shillings)
     * - Converted to String later for Safaricom API (they require String format)
     *
     * Example: 100 (means customer pays 100 KES)
     */
    @JsonProperty("amount") // Maps JSON field "amount" to this Java property
    private int amount;

    // Default constructor - required for JSON deserialization (Jackson needs this)
    public Mpesamapping() {}

    // Constructor for easy object creation in tests or other code
    public Mpesamapping(String phone, int amount) {
        this.phone = phone;
        this.amount = amount;
    }

    // Getters and setters - required for JSON serialization/deserialization
    // Jackson uses these to read/write JSON data

    /** Get customer's phone number */
    public String getPhone() { return phone; }

    /** Set customer's phone number */
    public void setPhone(String phone) { this.phone = phone; }

    /** Get payment amount */
    public int getAmount() { return amount; }

    /** Set payment amount */
    public void setAmount(int amount) { this.amount = amount; }
}
