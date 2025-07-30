# M-Pesa STK Push Integration Project

A complete Spring Boot application for integrating with Safaricom's M-Pesa STK Push (Lipa Na M-Pesa Online) service.

## Features

- ✅ STK Push payment initiation
- ✅ Safaricom Daraja API integration
- ✅ OAuth2 authentication with robust error handling
- ✅ Secure password generation
- ✅ Transaction callback handling
- ✅ RESTful API endpoints
- ✅ Enhanced debugging and error diagnostics

## Technology Stack

- **Backend Framework**: Spring Boot 3.5.3
- **Java Version**: Java 21
- **Build Tool**: Maven
- **HTTP Client**: Java HttpClient
- **JSON Processing**: Jackson

## Prerequisites

1. **Java 21+** installed
2. **Maven 3.6+** or use included wrapper
3. **Safaricom Developer Account** at https://developer.safaricom.co.ke
4. **Postman** for API testing

## Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/DianaGakuya/mpesa-stk-push-integration.git
cd mpesa-stk-push-integration
```

### 2. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Server configuration
server.port=8090

# Safaricom M-Pesa API configuration
mpesa.base-url=https://sandbox.safaricom.co.ke
mpesa.consumer-key=YOUR_CONSUMER_KEY_HERE
mpesa.consumer-secret=YOUR_CONSUMER_SECRET_HERE
mpesa.shortcode=174379
mpesa.passkey=YOUR_PASSKEY_HERE
mpesa.callback-url=https://your-domain.com/api/mpesa/callback
```

⚠️ **IMPORTANT**: Replace the placeholder values with your actual Safaricom credentials from https://developer.safaricom.co.ke

🔒 **NEVER commit real credentials to version control** - Always use placeholders in public repositories.

### 3. Run the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or on Windows
.\mvnw.cmd spring-boot:run

# Verify it's running
curl http://localhost:8090/api/mpesa/stkpush
```

## API Documentation

### STK Push Endpoint

**POST** `/api/mpesa/stkpush`

**Request Body:**
```json
{
    "phone": "254796022656",
    "amount": 1
}
```

**Success Response:**
```json
{
    "MerchantRequestID": "29115-34620561-1",
    "CheckoutRequestID": "ws_CO_191220191020363925",
    "ResponseCode": "0",
    "ResponseDescription": "Success. Request accepted for processing"
}
```

**Error Response (Expired Credentials):**
```json
{
    "error": "CREDENTIALS EXPIRED! Get new sandbox credentials from: https://developer.safaricom.co.ke/",
    "type": "RuntimeException"
}
```

### Callback Endpoint

**POST** `/api/mpesa/callback`

Receives transaction status updates from Safaricom.

## Postman Testing

1. **Method**: POST
2. **URL**: `http://localhost:8090/api/mpesa/stkpush`
3. **Headers**: `Content-Type: application/json`
4. **Body**:
   ```json
   {
       "phone": "254796022656",
       "amount": 1
   }
   ```

## Project Structure

```
src/main/java/com/mpesa/
├── MpesaApplication.java       # Main application entry point
├── config/
│   └── MpesaConfig.java        # Configuration management
├── controller/
│   └── MpesaController.java    # REST API endpoints
├── dto/
│   ├── Mpesamapping.java       # Request DTO
│   └── StkPushRequest.java     # Safaricom API DTO
└── service/
    └── MpesaService.java       # Business logic & OAuth handling
```

## Safaricom Developer Portal Setup

### Step 1: Create Safaricom Developer Account

1. **Visit Safaricom Developer Portal**
   - Go to https://developer.safaricom.co.ke/
   - Click **"Get Started"** or **"Sign Up"**

2. **Complete Registration**
   - Fill in your personal details (Name, Email, Phone)
   - Use a valid Safaricom number (07XXXXXXXX or 254XXXXXXXXX)
   - Verify your email and phone number
   - Create a strong password

3. **Account Verification**
   - Check your email for verification link
   - Verify your phone number via SMS
   - Complete your profile information

### Step 2: Create Sandbox Application

1. **Login to Developer Portal**
   - Navigate to https://developer.safaricom.co.ke/
   - Login with your credentials

2. **Create New App**
   - Click **"CREATE_NEW APP"** button
   - Fill in application details:
     - **App Name**: `Mpesa-stk-demo` (or your preferred name)
     - **Description**: Brief description of your M-Pesa integration project

3. **Add API Products**
   - Click **"Add API Products"** ✏️ button
   - Select **"M-PESA EXPRESS Sandbox"** (STK Push)
   - Optionally select **"M-Pesa Sandbox"** (for other M-Pesa APIs)
   - Click **"Save"**

### Step 3: Get Your Credentials

1. **View Credentials**
   - Click **"Credentials"** 👁️ button on your app
   - You'll see:
     - **Consumer Key**: Starts with random characters (e.g., `81tQzJIaR9saUsq4...`)
     - **Consumer Secret**: Long string of characters
     - **Shortcode**: Usually shows `N/A` (use `174379` for sandbox)

2. **Copy Your Credentials**
   ```
   Consumer Key: [Copy this - starts with random letters/numbers]
   Consumer Secret: [Copy this - long string]
   Shortcode: 174379 (Standard sandbox shortcode)
   Passkey: bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919
   ```

### Step 4: Important Sandbox Information

#### **Sandbox Environment Details:**
- **Base URL**: `https://sandbox.safaricom.co.ke`
- **Test Shortcode**: `174379` (Use this for all sandbox testing)
- **Test Passkey**: `bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919`
- **Valid Test Numbers**: Any Safaricom number (254XXXXXXXXX format)

#### **Key Differences: Sandbox vs Production**

| Feature | Sandbox | Production |
|---------|---------|------------|
| Base URL | `https://sandbox.safaricom.co.ke` | `https://api.safaricom.co.ke` |
| Shortcode | `174379` (fixed) | Your actual business shortcode |
| Passkey | Standard test passkey | Your business passkey |
| Phone Numbers | Any valid format | Real customer numbers |
| Money | Virtual/Test money | Real money transactions |
| Callback URLs | Can use localhost/ngrok | Must be HTTPS public URLs |

### Step 5: Configure Your Application

1. **Update application.properties**
   ```properties
   # Replace with your actual credentials from Step 3
   mpesa.consumer-key=YOUR_CONSUMER_KEY_FROM_PORTAL
   mpesa.consumer-secret=YOUR_CONSUMER_SECRET_FROM_PORTAL
   mpesa.shortcode=174379
   mpesa.passkey=bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919
   ```

2. **Test Phone Number Format**
   - Use format: `254XXXXXXXXX` (Kenya country code + 9 digits)
   - Example: `254796022656`
   - ❌ Don't use: `0796022656` or `+254796022656`

### Step 6: Testing Your Setup

1. **Start Your Application**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Test STK Push in Postman**
   ```json
   POST http://localhost:8090/api/mpesa/stkpush
   {
       "phone": "254796022656",
       "amount": 1
   }
   ```

3. **Expected Success Response**
   ```json
   {
       "MerchantRequestID": "29115-34620561-1",
       "CheckoutRequestID": "ws_CO_191220191020363925",
       "ResponseCode": "0",
       "ResponseDescription": "Success. Request accepted for processing"
   }
   ```

### Step 7: Common Sandbox Issues & Solutions

#### **1. Credentials Expired Error**
```json
{"error":"CREDENTIALS EXPIRED! Get new sandbox credentials"}
```
**Solution**: 
- Go back to developer portal
- Delete old app and create new one
- Get fresh Consumer Key/Secret

#### **2. Invalid BusinessShortCode**
```json
{"errorCode": "400.002.02", "errorMessage": "Bad Request - Invalid BusinessShortCode"}
```
**Solution**: 
- Always use `174379` for sandbox
- Ensure `@JsonProperty("BusinessShortCode")` annotation is correct

#### **3. Invalid Access Token**
```json
{"errorCode": "404.001.03", "errorMessage": "Invalid Access Token"}
```
**Solution**: 
- Check Consumer Key/Secret are correct
- Verify they're properly set in application.properties
- Ensure no extra spaces in credentials

### Step 8: Callback URL Setup (Optional)

For production or advanced testing, you'll need a public callback URL:

1. **Using ngrok (for testing)**
   ```bash
   # Install ngrok and run
   ngrok http 8090
   
   # Use the HTTPS URL provided
   # Example: https://abc123.ngrok.io/api/mpesa/callback
   ```

2. **Update callback URL**
   ```properties
   mpesa.callback-url=https://abc123.ngrok.io/api/mpesa/callback
   ```

### Step 9: Moving to Production

When ready for production:

1. **Get Production Credentials**
   - Apply for M-Pesa business account
   - Get real business shortcode and passkey
   - Get production Consumer Key/Secret

2. **Update Configuration**
   ```properties
   mpesa.base-url=https://api.safaricom.co.ke
   mpesa.consumer-key=YOUR_PRODUCTION_CONSUMER_KEY
   mpesa.consumer-secret=YOUR_PRODUCTION_CONSUMER_SECRET
   mpesa.shortcode=YOUR_BUSINESS_SHORTCODE
   mpesa.passkey=YOUR_BUSINESS_PASSKEY
   mpesa.callback-url=https://yourdomain.com/api/mpesa/callback
   ```

3. **Important Production Considerations**
   - Use HTTPS for all endpoints
   - Implement proper callback validation
   - Add proper error handling and logging
   - Use environment variables for credentials
   - Test thoroughly in sandbox first

---

## How It Works

1. **Client sends payment request** to `/api/mpesa/stkpush`
2. **Application authenticates** with Safaricom using OAuth2
3. **STK Push request sent** to customer's phone
4. **Customer enters M-Pesa PIN** to authorize payment
5. **Safaricom processes transaction** and sends callback
6. **Application receives confirmation** via callback endpoint

## Key Implementation Details

### Critical Success Factors

**1. JSON Field Mapping**
The most critical fix was ensuring correct JSON field names using `@JsonProperty` annotations:

```java
@JsonProperty("BusinessShortCode")  // Exact case required by Safaricom
private String BusinessShortCode;

@JsonProperty("Amount")  // Must be String, not int
private String Amount;
```

**2. Password Generation**
```java
String password = Base64.encode(ShortCode + Passkey + Timestamp);
```

**3. OAuth Error Handling**
Enhanced error handling that detects expired credentials:
```java
if (response.statusCode() == 400) {
    throw new RuntimeException("CREDENTIALS EXPIRED! Get new sandbox credentials from: https://developer.safaricom.co.ke/");
}
```

### Environment Configuration
- **Sandbox**: Use `https://sandbox.safaricom.co.ke` and shortcode `174379`
- **Production**: Use `https://api.safaricom.co.ke` and your actual business shortcode

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** for sensitive data in production
3. **Implement proper callback validation** for production use
4. **Use HTTPS** for all production endpoints
5. **Regularly refresh sandbox credentials** (they expire periodically)

## Troubleshooting

### Common Issues

**1. Credentials Expired Error**
```json
{"error":"CREDENTIALs EXPIRED! Get new sandbox credentials from: https://developer.safaricom.co.ke/"}
```
**Solution**: Get fresh credentials from Safaricom Developer Portal and update `application.properties`

**2. Port Already in Use**
```bash
# Windows
taskkill /f /im java.exe

# Check what's using port 8090
netstat -ano | findstr :8090
taskkill /PID [PID_NUMBER] /F
```

**3. Invalid BusinessShortCode Error**
- Ensure `@JsonProperty` annotations are correctly applied
- Verify exact field name casing matches Safaricom requirements
- Check that Amount field is String, not int

**4. Connection Refused**
- Check application is running on correct port
- Verify URL is `http://localhost:8090/api/mpesa/stkpush`
- Ensure no firewall blocking port 8090

**5. OAuth Failed with Status 400**
Usually means expired credentials. The application now provides clear error messages with solutions.

## Recent Updates (July 2025)

- ✅ Enhanced OAuth error handling with clear diagnostics
- ✅ Fixed JSON field mapping issues (BusinessShortCode casing)
- ✅ Updated to latest sandbox credentials
- ✅ Improved error messages and debugging
- ✅ Added comprehensive troubleshooting guide

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Make changes with proper tests
4. Commit changes (`git commit -m 'Add amazing feature'`)
5. Push to branch (`git push origin feature/amazing-feature`)
6. Submit pull request

## License

This project is for educational purposes. Ensure compliance with Safaricom's terms of service.

## Support

For issues and questions:
1. Check the troubleshooting section above
2. Verify your credentials are fresh and valid
3. Ensure proper JSON field casing in DTOs
4. Check application logs for detailed error information

---
