package com.mpesa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * STK Push Request DTO for Safaricom Daraja API
 *
 * This class represents the exact JSON structure that Safaricom's M-Pesa API expects.
 * The @JsonProperty annotations ensure field names match Safaricom's requirements exactly.
 *
 * Critical Success Factor: Field name casing MUST match Safaricom's documentation exactly,
 * otherwise you'll get "Invalid BusinessShortCode" or similar errors.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Only include non-null fields in JSON output
public class StkPushRequest {

    /** Business Short Code - Your M-Pesa paybill/till number (e.g., "174379" for sandbox) */
    @JsonProperty("BusinessShortCode") // Must be "BusinessShortCode", not "businessShortCode"
    private String BusinessShortCode;

    /**
     * Encrypted password for authentication
     * Generated as: Base64(ShortCode + Passkey + Timestamp)
     * This proves you have the correct passkey for your shortcode
     */
    @JsonProperty("Password")
    private String Password;

    /**
     * Transaction timestamp in format: yyyyMMddHHmmss (e.g., "20250730143000")
     * Used in password generation and for transaction tracking
     */
    @JsonProperty("Timestamp")
    private String Timestamp;

    /**
     * Transaction type - Always "CustomerPayBillOnline" for STK Push
     * This tells M-Pesa what kind of transaction you're initiating
     */
    @JsonProperty("TransactionType")
    private String TransactionType;

    /**
     * Transaction amount in Kenyan Shillings
     * MUST be String, not number - Safaricom API requirement
     */
    @JsonProperty("Amount")
    private String Amount;

    /**
     * Customer's phone number (the person paying)
     * Format: 254XXXXXXXXX (Kenya country code + 9 digits)
     */
    @JsonProperty("PartyA")
    private String PartyA;

    /**
     * Your business shortcode (who receives the money)
     * Usually same as BusinessShortCode above
     */
    @JsonProperty("PartyB")
    private String PartyB;

    /**
     * Phone number that will receive the STK Push prompt
     * Usually same as PartyA - the customer's phone
     */
    @JsonProperty("PhoneNumber")
    private String PhoneNumber;

    /**
     * Your callback URL where Safaricom sends transaction results
     * Must be publicly accessible HTTPS URL in production
     */
    @JsonProperty("CallBackURL") // Note: "CallBackURL", not "callbackUrl"
    private String CallBackURL;

    /**
     * Reference that appears on customer's M-Pesa statement
     * Keep it short and meaningful (max 12 characters)
     */
    @JsonProperty("AccountReference")
    private String AccountReference;

    /**
     * Description that appears on customer's M-Pesa statement
     * Explains what the payment is for (max 13 characters)
     */
    @JsonProperty("TransactionDesc")
    private String TransactionDesc;

    // Default constructor required for JSON deserialization
    public StkPushRequest() {}

    // Constructor to easily create STK Push requests with all required fields
    public StkPushRequest(String businessShortCode, String password, String timestamp,
                          String transactionType, String amount, String partyA,
                          String partyB, String phoneNumber, String callBackURL,
                          String accountReference, String transactionDesc) {
        this.BusinessShortCode = businessShortCode;
        this.Password = password;
        this.Timestamp = timestamp;
        this.TransactionType = transactionType;
        this.Amount = amount;
        this.PartyA = partyA;
        this.PartyB = partyB;
        this.PhoneNumber = phoneNumber;
        this.CallBackURL = callBackURL;
        this.AccountReference = accountReference;
        this.TransactionDesc = transactionDesc;
    }

    // Getters and Setters
    public String getBusinessShortCode() { return BusinessShortCode; }
    public void setBusinessShortCode(String businessShortCode) { this.BusinessShortCode = businessShortCode; }

    public String getPassword() { return Password; }
    public void setPassword(String password) { this.Password = password; }

    public String getTimestamp() { return Timestamp; }
    public void setTimestamp(String timestamp) { this.Timestamp = timestamp; }

    public String getTransactionType() { return TransactionType; }
    public void setTransactionType(String transactionType) { this.TransactionType = transactionType; }

    public String getAmount() { return Amount; }
    public void setAmount(String amount) { this.Amount = amount; }

    public String getPartyA() { return PartyA; }
    public void setPartyA(String partyA) { this.PartyA = partyA; }

    public String getPartyB() { return PartyB; }
    public void setPartyB(String partyB) { this.PartyB = partyB; }

    public String getPhoneNumber() { return PhoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.PhoneNumber = phoneNumber; }

    public String getCallBackURL() { return CallBackURL; }
    public void setCallBackURL(String callBackURL) { this.CallBackURL = callBackURL; }

    public String getAccountReference() { return AccountReference; }
    public void setAccountReference(String accountReference) { this.AccountReference = accountReference; }

    public String getTransactionDesc() { return TransactionDesc; }
    public void setTransactionDesc(String transactionDesc) { this.TransactionDesc = transactionDesc; }
}
