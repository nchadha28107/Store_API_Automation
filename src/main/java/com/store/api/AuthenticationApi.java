package com.store.api;

import com.store.utils.ConfigReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.store.model.User;

import static io.restassured.RestAssured.given;

public class AuthenticationApi {

    private static final String baseUri = ConfigReader.getBaseUrl(System.getProperty("env", "local"));
    private static final String registerEndpoint = ConfigReader.getEndpoint("auth", "register");
    private static final String loginEndpoint = ConfigReader.getEndpoint("auth", "login");
    private static final String logoutEndpoint = ConfigReader.getEndpoint("auth", "logout");

    public static Response registerUser(User user) {
        return given()
                .baseUri(baseUri)  // Use the environment-specific base URI
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(registerEndpoint);
    }

    public static Response loginUser(User user) {
        return given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(loginEndpoint);
    }

    public static Response logoutUser(String token) {
        return given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + (token != null ? token : ""))
                .when()
                .get(logoutEndpoint );
    }

    public static Response registerUserFromRawBody(String requestBody) {
        return given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(requestBody) // Using raw request body
                .when()
                .post(registerEndpoint);
    }

    // Login User with raw request body
    public static Response loginUserFromRawBody(String requestBody) {
        return given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(requestBody) // Using raw request body
                .when()
                .post(loginEndpoint);
    }
}
