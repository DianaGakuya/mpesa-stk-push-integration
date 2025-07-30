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
mpesa.consumer-key=81tQzJIaR9saUsq4Eu9eu6Dnko9UXHvC1titNFb5slpStVGb
mpesa.consumer-secret=hkucAbkk0ENhM2XbK45PZQ9Qji1UJSVcacJNdsqIjCHm4qHUAzyAJUQJTGf6SvyA
mpesa.shortcode=174379
mpesa.passkey=bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919
mpesa.callback-url=https://mydomain.com/pat
```

⚠️ **Important**: These are current sandbox credentials (July 2025). Replace with your own fresh credentials if needed.

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

**Last Updated**: July 30, 2025  
**Status**: ✅ Working with fresh sandbox credentials
