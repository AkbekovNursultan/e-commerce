E-COMMERCE

This project uses Spring Security with both JWT-based authentication and OAuth2 login (GitHub). It includes:

Username/password login

OAuth2 login via GitHub

JWT tokens (access & refresh)

Role-based access control

Secure endpoints with method-level security

Token-based authentication for APIs


Instructions:

1. Change properties
Before running the application, make sure to update sensitive credentials in application.properties:

🔑 OAuth2 GitHub Login
Replace the following with your own GitHub OAuth app credentials:

spring.security.oauth2.client.registration.github.client-id=YOUR_CLIENT_ID_HERE
spring.security.oauth2.client.registration.github.client-secret=YOUR_CLIENT_SECRET_HERE
You can generate these from GitHub under:
Settings → Developer settings → OAuth Apps

📧 Email Service (Used for account verification, etc.)
Replace the following with your own email credentials (recommended: use an App Password for Gmail):

spring.mail.username=your.email@example.com
spring.mail.password=your_app_password


2. Register new user

Send the POST request with body(UserRegisterRequest) to this route  http://localhost:8080/api/register

For example:
{
  "username": "jose",
  "email": "jamshyt@gmail.com",
  "password": "josealdo",
  "role": "WORKER",
  "country": "USA",
  "city": "New York",
  "address": "123 Main St, Apt 4B",
  "additionalInfo": "Near Central Park",
  "phone": "906090",
  "zipCode": "10001"
}


3. Login

    1. Username/Password Login
    Endpoint: POST /api/login-basic
    
    Request body:
    {
      "username": "john_doe",
      "password": "securePassword"
    }

    Response:
    {
      "accessToken": "...",
      "refreshToken": "..."
    }

    2. OAuth2 Login (GitHub/Google)
    Start flow: GET /oauth2/authorization/github or /oauth2/authorization/google
    
    Upon successful login:
    
    The user is saved/loaded
    
    The client is redirected to /oauth2/success?token=...&refreshToken=...
    
    This token can then be stored and used
