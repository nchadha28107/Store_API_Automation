package com.store.steps;


import com.store.api.AuthenticationApi;
import com.store.enums.Context;
import com.store.model.User;
import com.store.utils.ScenarioContext;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationSteps {

    private Response response;

    private String handleSpecialCase(String input) {
        if ("longUsername".equals(input)) {
            return "a".repeat(256);
        }
        if ("longPassword".equals(input)) {
            return "a".repeat(256);
        }
        if ("longEmail".equals(input)) {
            return "a".repeat(256) + "@example.com";
        }
        return input;
    }

    @Given("I register with username {string}, password {string} and email {string}")
    public void i_register_with_username_and_password(String username, String password, String email) {
        // Check for special cases, e.g., long passwords
        username = handleSpecialCase(username);
        password = handleSpecialCase(password);
        email = handleSpecialCase(email);
        response = AuthenticationApi.registerUser(new User(username, password, email));
    }

    @When("I log in with username {string}, password {string} and email {string}")
    public void i_log_in_with_username_and_password(String username, String password, String email) {
        // Check for special cases, e.g., long passwords
        if ("longUsername".equals(username)) {
            username = "a".repeat(256);  // Replace with a 256-character password
        }
        if ("longPassword".equals(password)) {
            password = "a".repeat(256);  // Replace with a 256-character password
        }
        response = AuthenticationApi.loginUser(new User(username, password, email));
        // Extract the token from the login response
        String token = response.jsonPath().getString("token");
        // Store the token for future use
        ScenarioContext.setContext(Context.AUTH_TOKEN, token);
    }

    @Then("the registration should be successful")
    public void the_registration_should_be_successful() {
        assertEquals(201, response.statusCode());
    }

    @Then("the login should be successful")
    public void the_login_should_be_successful() {
        assertEquals(200, response.statusCode());
    }

    @Then("the login should fail")
    public void the_login_should_fail() {
        assertEquals(401, response.statusCode());
    }

    @Then("the login should be successful and return an authentication token")
    public void the_login_should_be_successful_and_return_an_authentication_token() {
        // Validate that the status code is 200 (OK)
        assertEquals(200, response.statusCode());

        // Extract the authentication token from the response
        String authToken = response.jsonPath().getString("token");

        // Assert that the token is not null or empty
        assertNotNull(authToken, "Authentication token should not be null or empty");

        // Optionally, you can also validate the token format or perform further assertions
        // For example, if the token is expected to be a JWT, you can validate its structure.
        // But for now, we simply check that it's not null or empty.
    }

    @When("I log out with valid token")
    public void i_log_out_with_valid_token() {
        String token = response.jsonPath().getString("token");
        response = AuthenticationApi.logoutUser(token);
    }

    @Then("the logout should be successful")
    public void the_logout_should_be_successful() {
        assertEquals(200, response.statusCode());
    }

    @When("I log out without an authentication token")
    public void i_log_out_without_token() {
        response = AuthenticationApi.logoutUser(null);
    }

    @Then("^the (registration|login|logout) should fail with error (.+)")
    public void the_auth_should_fail_with_error_message(String action, String errorMessage) {
        if(action.equalsIgnoreCase("registration")) {
            assertEquals(400, response.statusCode());
        } else {
            assertEquals(401, response.statusCode());
        }
        assertEquals(errorMessage, response.jsonPath().getString("error"));
    }

    @When("I attempt to log out with expired token")
    public void i_attempt_to_log_out_with_expired_token() {
        String expiredToken = "expiredToken";  // Simulate expired token
        response = AuthenticationApi.logoutUser(expiredToken);
    }

    @Given("I register with body$")
    public void i_register_with_body(String requestBody) {
        // Here, we directly use the raw request body in the API call.
        response = AuthenticationApi.registerUserFromRawBody(requestBody);
    }

    @When("I log in with body$")
    public void i_log_in_with_body(String requestBody) {
        // Here, we directly use the raw request body for the login API call.
        response = AuthenticationApi.loginUserFromRawBody(requestBody);
    }
}

