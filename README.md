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
git clone https://github.com/DianaGakuya/mpesa-stk-push-integration.git
cd mpesa-stk-push-integration
```

### 2. Configure Application Properties

Copy the template file and add your credentials:

```bash
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

Edit `src/main/resources/application.properties` with your actual Safaricom credentials:

```properties
# Server configuration
server.port=8090

# Safaricom M-Pesa API configuration
mpesa.base-url=https://sandbox.safaricom.co.ke
mpesa.consumer-key=YOUR_ACTUAL_CONSUMER_KEY
mpesa.consumer-secret=YOUR_ACTUAL_CONSUMER_SECRET
mpesa.shortcode=174379
mpesa.passkey=YOUR_ACTUAL_PASSKEY
mpesa.callback-url=https://your-domain.com/api/mpesa/callback
```

**⚠️ Security Note**: Never commit your actual credentials to version control. The `application.properties` file is ignored by Git.

### 3. Run the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The application will start on **http://localhost:8090**

## API Usage

### STK Push Request

**Endpoint**: `POST http://localhost:8090/api/mpesa/stkpush`

**Request Body**:
```json
{
    "phone": "254796022656",
    "amount": 1
}
```

**Success Response**:
```json
{
    "MerchantRequestID": "29115-34620561-1",
    "CheckoutRequestID": "ws_CO_191220191020363925",
    "ResponseCode": "0",
    "ResponseDescription": "Success. Request accepted for processing",
    "CustomerMessage": "Success. Request accepted for processing"
}
```

### Postman Testing

1. **Method**: POST
2. **URL**: `http://localhost:8090/api/mpesa/stkpush`
3. **Headers**: `Content-Type: application/json`
4. **Body**: Raw JSON with phone and amount

## Project Structure

```
src/
├── main/java/com/mpesa/
│   ├── MpesaApplication.java          # Spring Boot main class
│   ├── config/MpesaConfig.java        # Configuration properties
│   ├── controller/MpesaController.java # REST API endpoints
│   ├── dto/
│   │   ├── Mpesamapping.java          # Request DTO
│   │   └── StkPushRequest.java        # Safaricom API DTO
│   └── service/MpesaService.java      # Business logic
└── main/resources/
    ├── application.properties.template # Credential template
    └── application.properties         # Your actual credentials (git-ignored)
```

## How It Works

1. **Client Request**: Your app sends phone number and amount to the API
2. **Authentication**: Service gets OAuth2 token from Safaricom
3. **STK Push**: Service sends payment request to Safaricom
4. **Customer Prompt**: Customer receives payment prompt on their phone
5. **Payment**: Customer enters M-Pesa PIN to complete payment
6. **Callback**: Safaricom sends result to your callback URL

## Troubleshooting

**Application Won't Start**
```bash
# Check if port 8090 is in use
netstat -ano | findstr :8090

# Kill existing processes
taskkill /f /im java.exe
```

**Invalid BusinessShortCode Error**
- Use sandbox shortcode: `174379`
- Never change this value for sandbox testing
- Ensure your passkey matches this shortcode

**Connection Refused in Postman**
- Verify application is running: check console for "Started MpesaApplication"
- Use exact URL: `http://localhost:8090/api/mpesa/stkpush`
- Check request body format matches example above

**STK Push Not Received**
- Ensure phone number format: `254XXXXXXXXX`
- Amount must be positive integer
- Check Safaricom sandbox limitations

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## Security

- Never commit actual M-Pesa credentials
- Use environment variables in production
- Implement proper callback URL validation
- Add request rate limiting for production use

## License

This project is for educational purposes. Ensure compliance with Safaricom's terms of service.

---

**Author**: Diana Gakuya  
**Contact**: For questions about this implementation
