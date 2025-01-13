package com.store.api;

import com.store.model.Review;
import com.store.utils.ConfigReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ReviewApi {

    private static final String baseUri = ConfigReader.getBaseUrl(System.getProperty("env", "local"));
    private static final String reviewsEndpoint = ConfigReader.getEndpoint("reviews", "list");
    private static final String createReviewEndpoint = ConfigReader.getEndpoint("reviews", "create");
    private static final String reviewByIdEndpoint = ConfigReader.getEndpoint("reviews", "getById");
    private static final String updateReviewEndpoint = ConfigReader.getEndpoint("reviews", "update");
    private static final String deleteReviewEndpoint = ConfigReader.getEndpoint("reviews", "delete");

    private static RequestSpecification withAuthHeader(String token) {
        return given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + token);
    }

    // Retrieve all reviews
    public static Response getAllReviews() {
        return given()
                .baseUri(baseUri)
                .when()
                .get(reviewsEndpoint)
                .then()
                .extract().response();
    }

    // Retrieve a review by ID
    public static Response getReviewById(String reviewId) {
        return given()
                .baseUri(baseUri)
                .when()
                .get(reviewByIdEndpoint + reviewId)
                .then()
                .extract().response();
    }

    // Create a new review
    public static Response createReview(Review review, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .body(review)
                .when()
                .post(createReviewEndpoint)
                .then()
                .extract().response();
    }

    // Update a review by ID
    public static Response updateReview(int reviewId, Review review, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .body(review)
                .when()
                .put(updateReviewEndpoint + reviewId)
                .then()
                .extract().response();
    }

    // Delete a review by ID
    public static Response deleteReviewById(Integer reviewId, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .when()
                .delete(deleteReviewEndpoint + reviewId)
                .then()
                .extract().response();
    }
}
