# M-Pesa STK Push Integration Project

A complete Spring Boot application for integrating with Safaricom's M-Pesa STK Push (Lipa Na M-Pesa Online) service.

## Features

- ✅ STK Push payment initiation
- ✅ Safaricom Daraja API integration
- ✅ OAuth2 authentication
- ✅ Secure password generation
- ✅ Transaction callback handling
- ✅ RESTful API endpoints

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
git clone <your-repository-url>
cd mpesa-main/mpesa
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

⚠️ **Important**: Replace placeholder values with your actual Safaricom credentials

### 3. Run the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

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
    "amount": 100
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
    └── MpesaService.java       # Business logic
```

## How It Works

1. **Client sends payment request** to `/api/mpesa/stkpush`
2. **Application authenticates** with Safaricom using OAuth2
3. **STK Push request sent** to customer's phone
4. **Customer enters M-Pesa PIN** to authorize payment
5. **Safaricom processes transaction** and sends callback
6. **Application receives confirmation** via callback endpoint

## Key Implementation Details

### JSON Field Mapping
The critical fix for this project was ensuring correct JSON field names using `@JsonProperty` annotations:

```java
@JsonProperty("BusinessShortCode")  // Exact case required by Safaricom
private String BusinessShortCode;
```

### Password Generation
```java
String password = Base64.encode(ShortCode + Passkey + Timestamp);
```

### Environment Configuration
- **Sandbox**: Use `https://sandbox.safaricom.co.ke` and shortcode `174379`
- **Production**: Use `https://api.safaricom.co.ke` and your actual business shortcode

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** for sensitive data in production
3. **Implement proper callback validation** for production use
4. **Use HTTPS** for all production endpoints

## Troubleshooting

### Common Issues

**Port Already in Use**
```bash
# Kill existing Java processes
taskkill /f /im java.exe
```

**Invalid BusinessShortCode Error**
- Ensure `@JsonProperty` annotations are correctly applied
- Verify exact field name casing matches Safaricom requirements

**Connection Refused**
- Check application is running on correct port
- Verify URL is `http://localhost:8090/api/mpesa/stkpush`

## Contributing

1. Fork the repository
2. Create feature branch
3. Make changes with proper tests
4. Submit pull request

## License

This project is for educational purposes. Ensure compliance with Safaricom's terms of service.
