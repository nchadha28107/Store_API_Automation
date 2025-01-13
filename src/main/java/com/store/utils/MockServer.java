package com.store.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MockServer {

    private static WireMockServer wireMockServer;
    private static final int PORT = 9090;  // Define the mock server port

    public static void startMockServer() {
        // Start the WireMock server
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
        WireMock.configureFor("localhost", PORT);
        // Configure RestAssured to use the mock server's base URI
        RestAssured.baseURI = "http://localhost:" + PORT;

        setupMockResponses();
    }

    private static void setupMockResponses(){
        // Mock API responses
        mockRegister();
        mockLogin();
        mockLogout();
        mockGetProducts();
        mockCreateProductSuccess();
        mockDeleteProducts();
        mockGetReviews();
        mockCreateReviews();
        mockUpdateReviews();
        mockDeleteReviews();
        mockGetOrders();
        mockCreateOrders();
        mockUpdateOrders();
        mockDeleteOrders();
    }

    public static void stopMockServer() {
        // Stop the WireMock server after tests
        wireMockServer.stop();
    }

    private static void mockRegister() {
        // Mocking the POST /register endpoint
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(containing("username"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"User registered successfully!\"}")));

        // Simulate invalid registration (missing username)
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.username", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Username is required\" }")));

        // Simulate registration with already used email
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.email", containing("email5")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Email is already in use\" }")));

        // Simulate registration with invalid email format
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.email", containing("invalid-email")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Invalid email format\" }")));

        // Simulate too long username
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.username", containing("a".repeat(256))))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Username too long\" }")));

        // Simulate too long password
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.password", containing("a".repeat(256))))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Password too long\" }")));

        // Simulate too long email
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.email", containing("a".repeat(256) + "@example.com")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Email too long\" }")));

        // Mock for "Username must be at least 3 characters" error
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.username", matching(".{1,2}")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Username must be at least 3 characters\" }")));

        // Mock for "Password is required" error
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.password", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Password is required\" }")));

        // Mock registration request with missing email
        stubFor(post(urlEqualTo("/api/v1/auth/register"))
                .withRequestBody(matchingJsonPath("$.email", absent()))  // Check if email is absent
                .willReturn(aResponse()
                        .withStatus(400)  // Return Bad Request if email is missing
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"error\": \"Email is required\" }")));
    }

    private static void mockLogin() {
        // Mocking the POST /login endpoint
        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(containing("validUser"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\": \"fake-jwt-token\"}")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(containing("nonAdminValidUser"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\": \"nonAdmin-jwt-token\"}")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.username", matching(".*invalid.*")))  // Match username containing 'invalid'
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Invalid username or password\" }")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.password", matching(".*wrong.*")))  // Match password containing 'wrong'
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Invalid username or password\" }")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.email", matching(".*wrong.*")))  // Match email containing 'wrong'
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Invalid username or password\" }")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.email", matching(".*invalidemail.*")))  // Match email containing 'wrong'
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Invalid email format\" }")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.username", matching(".*unregistered.*")))  // Match email containing 'wrong'
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"User not found\" }")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.username", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Username and password are required\" }")));

        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.password", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Password is required\" }")));

        // Simulate too long username
        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.username", containing("a".repeat(256))))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Username too long\" }")));

        // Mock registration request with missing email
        stubFor(post(urlEqualTo("/api/v1/auth/login"))
                .withRequestBody(matchingJsonPath("$.email", absent()))  // Check if email is absent
                .willReturn(aResponse()
                        .withStatus(401)  // Return Bad Request if email is missing
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"error\": \"Email is required\" }")));

//        // Simulate too many login attempts (rate limiting)
//        stubFor(post(urlEqualTo("/api/v1/auth/login"))
//                .willReturn(aResponse()
//                        .withStatus(429)
//                        .withBody("{ \"error\": \"Too many login attempts, please try again later\" }")));
    }

    private static void mockLogout() {
        // Mocking successful logout with a valid token
        stubFor(get(urlEqualTo("/api/v1/auth/logout"))
                .withHeader("Authorization", matching(".*fake-jwt-token.*")) // Match the valid token
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"message\": \"Logout successful\" }")));

        // Simulate logout with missing authentication token
        stubFor(get(urlEqualTo("/api/v1/auth/logout"))
                .withHeader("Authorization", matching("Bearer *"))  // No token provided
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"User is not logged in\" }")));

        // Simulate logout with an expired authentication token
        stubFor(get(urlEqualTo("/api/v1/auth/logout"))
                .withHeader("Authorization", matching(".*expiredToken.*"))  // Simulate expired token
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{ \"error\": \"Session expired, please log in again\" }")));
    }

    private static void mockGetProducts() {
        // Mocking the GET /products endpoint
        stubFor(get(urlEqualTo("/api/v1/products"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\": 1, \"name\": \"Product A\"}, {\"id\": 2, \"name\": \"Product B\"}]")));
    }

    public static void mockGetEmptyProducts() {
        // Mocking the GET /products endpoint
        stubFor(get(urlEqualTo("/api/v1/products"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));  // Empty list as the response body
    }

    public static void mockCreateProductSuccess() {
        stubFor(WireMock.post(urlEqualTo("/api/v1/products"))
                .withRequestBody(matchingJsonPath("$.name", WireMock.containing("Product A"))) // Matching product name
                .willReturn(aResponse()
                        .withStatus(201)  // HTTP 201 Created
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"name\": \"Product A\", \"description\": \"Description A\", \"price\": 100.0, \"stock\": 50}")));


        stubFor(post(urlEqualTo("/api/v1/products"))
                .withRequestBody(matchingJsonPath("$.name", equalTo(""))) // Check for empty name field
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product name is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/products"))
                .withRequestBody(matchingJsonPath("$.description", equalTo(""))) // Check for empty description field
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product description is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/products"))
                .withRequestBody(matchingJsonPath("$.price", absent())) // Check for empty price field
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product price is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/products"))
                .withRequestBody(matchingJsonPath("$.stock", absent())) // Matching null for stock
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request because stock is null
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product stock is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/products"))
                .withRequestBody(matchingJsonPath("$.stock", matching("0"))) // Matching null for stock
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request because stock is null
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product stock must be greater than zero\"}")));

        stubFor(post(urlEqualTo("/api/v1/products"))
                .withRequestBody(matchingJsonPath("$.price", containing("-100.0")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Price cannot be negative\"}")));

        stubFor(post(urlEqualTo("/api/v1/products"))
                .withHeader("Authorization", equalTo("Bearer nonAdmin-jwt-token"))  // Match specific token
                .willReturn(aResponse()
                        .withStatus(403)  // HTTP 403 Forbidden
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Permission denied\"}")));
    }

    private static void mockDeleteProducts() {
        stubFor(delete(urlEqualTo("/api/v1/products/1"))
                .willReturn(aResponse()
                        .withStatus(200)  // Successful deletion
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Product deleted successfully\"}")));

        stubFor(delete(urlEqualTo("/api/v1/products/null"))
                .willReturn(aResponse()
                        .withStatus(400)  // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product ID is required\"}")));

        stubFor(delete(urlEqualTo("/api/v1/products/9999"))  // Non-existing product ID
                .willReturn(aResponse()
                        .withStatus(404)  // Not Found
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product not found\"}")));

        stubFor(delete(urlEqualTo("/api/v1/products/1"))
                .withHeader("Authorization", equalTo("Bearer nonAdmin-jwt-token")) // Simulate non-admin user
                .willReturn(aResponse()
                        .withStatus(403)  // Forbidden (admin required)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Permission denied\"}")));
    }

    public static void mockGetEmptyReviews() {
        // Mocking the GET /products endpoint
        stubFor(get(urlEqualTo("/api/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));  // Empty list as the response body
    }


    public static void mockGetEmptyOrder() {
        // Mocking the GET /products endpoint
        stubFor(get(urlEqualTo("/api/v1/orders"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));  // Empty list as the response body
    }

    private static void mockGetReviews() {
        // Mocking the GET /reviews endpoint
        stubFor(get(urlEqualTo("/api/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("[{\"id\": 1, \"productId\": \"P123\", \"userId\": \"U456\", \"rating\": 5, \"comment\": \"Excellent product!\"},"
                                + "{\"id\": 2, \"productId\": \"P124\", \"userId\": \"U457\", \"rating\": 4, \"comment\": \"Very good product.\"}]")));

        stubFor(get(urlEqualTo("/api/v1/reviews/123"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"id\": \"123\", \"productId\": \"P123\", \"userId\": \"U456\", \"rating\": 5, \"comment\": \"Excellent product!\"}")));

        stubFor(get(urlEqualTo("/api/v1/reviews/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"error\": \"Review not found\"}")));
    }

    private static void mockCreateReviews() {
        stubFor(post(urlEqualTo("/api/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"productId\": \"P123\", \"userId\": \"1\", \"rating\": 5, \"comment\": \"Excellent product\"}")));

        stubFor(post(urlEqualTo("/api/v1/reviews"))
                .withRequestBody(matchingJsonPath("$.rating", absent()))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Rating is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/reviews"))
                .withRequestBody(matchingJsonPath("$.rating", equalTo("6")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Rating must be between 1 and 5\"}")));

        stubFor(post(urlEqualTo("/api/v1/reviews"))
                .withRequestBody(matchingJsonPath("$.comment", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Comment is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/reviews"))
                .withRequestBody(matchingJsonPath("$.productId", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"ProductId is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/reviews"))
                .withRequestBody(matchingJsonPath("$.productId", equalTo("P124")))
                .willReturn(aResponse()
                        .withStatus(404) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Product not found\"}")));

        stubFor(post(urlEqualTo("/api/v1/reviews"))
                .withRequestBody(matchingJsonPath("$.userId", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"UserId is required\"}")));
    }

    private static void mockUpdateReviews() {
        stubFor(put(urlEqualTo("/api/v1/reviews/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"productId\": \"P123\", \"userId\": \"1\", \"rating\": 4, \"comment\": \"Excellent product\"}")));

        stubFor(put(urlEqualTo("/api/v1/reviews/999"))
                .willReturn(aResponse()
                        .withStatus(404) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Review not found\"}")));

        stubFor(put(urlEqualTo("/api/v1/reviews/123"))
                .withRequestBody(matchingJsonPath("$.rating", equalTo("6")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Rating must be between 1 and 5\"}")));

        stubFor(put(urlEqualTo("/api/v1/reviews/123"))
                .withRequestBody(matchingJsonPath("$.rating", absent()))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Rating is required\"}")));

        stubFor(put(urlEqualTo("/api/v1/reviews/123"))
                .withRequestBody(matchingJsonPath("$.comment", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Comment is required\"}")));
    }

    private static void mockDeleteReviews() {
        stubFor(delete(urlEqualTo("/api/v1/reviews/123"))
                .willReturn(aResponse()
                        .withStatus(204)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"productId\": \"P123\", \"userId\": \"1\", \"rating\": 4, \"comment\": \"Excellent product\"}")));

        stubFor(delete(urlEqualTo("/api/v1/reviews/999"))
                .willReturn(aResponse()
                        .withStatus(404) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Review not found\"}")));

        stubFor(delete(urlEqualTo("/api/v1/reviews/null"))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Review ID is required\"}")));
    }

    private static void mockGetOrders() {
        // Mocking the GET /reviews endpoint
        stubFor(get(urlEqualTo("/api/v1/orders"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("[{\"id\": 1, \"productId\": \"P123\", \"userId\": \"U456\", \"quantity\": 3, \"totalAmount\": 69.99}\"},"
                                + "{\"id\": 2, \"productId\": \"P124\", \"userId\": \"U457\", \"quantity\": 5, \"totalAmount\": 159.99}\"}]")));

        stubFor(get(urlEqualTo("/api/v1/orders/123"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"id\": 123, \"productId\": \"P123\", \"userId\": \"U456\", \"quantity\": 3, \"totalAmount\": 69.99}\"}")));

        stubFor(get(urlEqualTo("/api/v1/orders/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"error\": \"Order not found\"}")));

        stubFor(get(urlMatching("/api/v1/orders(/.*)?"))
                .withHeader("Authorization", equalTo("Bearer nonAdmin-jwt-token"))  // Match specific token
                .willReturn(aResponse()
                        .withStatus(403)  // HTTP 403 Forbidden
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Permission denied\"}")));
    }

    private static void mockCreateOrders() {
        stubFor(post(urlEqualTo("/api/v1/orders"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"productId\": \"P123\", \"userId\": \"U456\", \"quantity\": 3, \"totalAmount\": 69.99}\"}")));

        stubFor(post(urlEqualTo("/api/v1/orders"))
                .withRequestBody(matchingJsonPath("$.productId", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"ProductId is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/orders"))
                .withRequestBody(matchingJsonPath("$.userId", equalTo("")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"UserId is required\"}")));

        stubFor(post(urlEqualTo("/api/v1/orders"))
                .withRequestBody(matchingJsonPath("$.totalAmount", containing("-100.0")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Total amount cannot be negative\"}")));

        stubFor(post(urlEqualTo("/api/v1/orders"))
                .withRequestBody(matchingJsonPath("$.quantity", equalTo("0")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Quantity must be greater than zero\"}")));

        stubFor(post(urlEqualTo("/api/v1/orders"))
                .withRequestBody(matchingJsonPath("$.totalAmount", absent()))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Total amount is required\"}")));

        stubFor(post(urlMatching("/api/v1/orders(/.*)?"))
                .withHeader("Authorization", equalTo("Bearer nonAdmin-jwt-token"))  // Match specific token
                .willReturn(aResponse()
                        .withStatus(403)  // HTTP 403 Forbidden
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Permission denied\"}")));
    }

    private static void mockUpdateOrders() {
        stubFor(put(urlEqualTo("/api/v1/orders/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"productId\": \"P123\", \"userId\": \"U456\", \"quantity\": 5, \"totalAmount\": 250.0}\"}")));

        stubFor(put(urlEqualTo("/api/v1/orders/999"))
                .willReturn(aResponse()
                        .withStatus(404) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Order not found\"}")));

        stubFor(put(urlMatching("/api/v1/orders(/.*)?"))
                .withRequestBody(matchingJsonPath("$.quantity", equalTo("0")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Quantity must be greater than zero\"}")));

        stubFor(put(urlMatching("/api/v1/orders(/.*)?"))
                .withRequestBody(matchingJsonPath("$.totalAmount", absent()))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Total amount is required\"}")));

        stubFor(put(urlMatching("/api/v1/orders(/.*)?"))
                .withRequestBody(matchingJsonPath("$.totalAmount", containing("-50.0")))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Total amount cannot be negative\"}")));

        stubFor(put(urlMatching("/api/v1/orders(/.*)?"))
                .withHeader("Authorization", equalTo("Bearer nonAdmin-jwt-token"))  // Match specific token
                .willReturn(aResponse()
                        .withStatus(403)  // HTTP 403 Forbidden
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Permission denied\"}")));
    }

    private static void mockDeleteOrders() {
        stubFor(delete(urlEqualTo("/api/v1/orders/123"))
                .willReturn(aResponse()
                        .withStatus(204)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"productId\": \"P123\", \"userId\": \"U456\", \"quantity\": 3, \"totalAmount\": 69.99}\"}")));

            stubFor(delete(urlEqualTo("/api/v1/orders/999"))
                .willReturn(aResponse()
                        .withStatus(404) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Order not found\"}")));

        stubFor(delete(urlEqualTo("/api/v1/orders/null"))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Order ID is required\"}")));

        stubFor(delete(urlMatching("/api/v1/orders(/.*)?"))
                .withHeader("Authorization", equalTo("Bearer nonAdmin-jwt-token"))  // Match specific token
                .willReturn(aResponse()
                        .withStatus(403)  // HTTP 403 Forbidden
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Permission denied\"}")));
    }
}