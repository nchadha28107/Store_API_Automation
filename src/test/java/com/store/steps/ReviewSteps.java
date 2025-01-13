package com.store.steps;

import com.store.api.ReviewApi;
import com.store.enums.Context;
import com.store.model.Review;
import com.store.utils.MockServer;
import com.store.utils.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReviewSteps {

    private Response response;
    private Review review;
    private ScenarioContext scenarioContext;
    private String token; // Authentication token, to be used in API calls

    // Retrieve All Reviews
    @Given("I retrieve all reviews")
    public void i_retrieve_all_reviews() {
        response = ReviewApi.getAllReviews(); // Make API call to retrieve all reviews
        scenarioContext.setContext(Context.RESPONSE, response);  // Storing the response in ScenarioContext using Enum
    }

    @Then("the reviews should be returned successfully")
    public void the_reviews_should_be_returned_successfully() {
        // Retrieve the response from ScenarioContext using Enum
        response = (Response) scenarioContext.getContext(Context.RESPONSE);

        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());

        // Assert that the response body contains review data (i.e., should not be empty)
        assertTrue("Reviews list should not be empty", response.getBody().asString().length() > 0);
    }

    @Given("there are no reviews available")
    public void there_are_no_reviews_available() {
        MockServer.mockGetEmptyReviews();
    }

    @Then("the reviews empty list should be returned successfully")
    public void the_reviews_empty_list_should_be_returned_successfully() {
        // Retrieve the response from ScenarioContext using Enum
        response = (Response) scenarioContext.getContext(Context.RESPONSE);

        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());

        // Assert that the response body is an empty list (JSON)
        assertEquals("[]", response.getBody().asString());
    }

    @When("I retrieve the review with ID {string}")
    public void i_retrieve_the_review_with_id(String reviewId) {
        response = ReviewApi.getReviewById(reviewId);
        scenarioContext.setContext(Context.RESPONSE, response);
    }

    @Then("the review with ID {string} should be returned successfully")
    public void the_review_with_id_should_be_returned_successfully(String reviewId) {
        // Retrieve the response from ScenarioContext using Enum
        response = (Response) scenarioContext.getContext(Context.RESPONSE);

        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());

        // Assert that the review ID matches the expected review ID
        assertEquals(reviewId, response.jsonPath().getString("id"));
    }

    @When("I create a new review with productId {string}, userId {string}, rating {string} and comment {string}")
    public void i_create_a_new_review_with_productId_userId_rating_and_comment(String productId, String userId, String rating, String comment) {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        Integer newRating;
        if (rating.isEmpty() || rating.equals("null")) {
            newRating = null;
        } else {
            newRating = Integer.parseInt(rating);
        }
        review = new Review(1, productId, userId, newRating, comment);
        // Simulate a POST request to create the review
        response = ReviewApi.createReview(review, token);
    }

    @Then("the review should be created successfully")
    public void the_review_should_be_created_successfully() {
        assertEquals(201, response.getStatusCode());
        assertTrue("Review was created successfully", response.jsonPath().getInt("id") == (review.getId()));
    }

    @When("I update the review with ID {int} to have rating {string} and comment {string}")
    public void i_update_the_review_with_id_to_have_rating_and_comment(int reviewId, String newRating, String newComment) {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        if (newRating.isEmpty() || newRating.equals("null")) {
            review.setRating(null);
        } else {
            review.setRating(Integer.parseInt(newRating));
        }
        review.setComment(newComment);
        response = ReviewApi.updateReview(reviewId, review, token);
    }

    @Then("the review should be updated successfully")
    public void the_review_should_be_updated_successfully() {
        assertEquals(200, response.getStatusCode());
        assertEquals(review.getRating(), Integer.valueOf(response.jsonPath().getInt("rating")));
        assertEquals(review.getComment(), response.jsonPath().getString("comment"));
    }

    // Delete Review by ID
    @When("I delete the review with ID {int}")
    public void i_delete_the_review_with_id(int reviewId) {
        response = ReviewApi.deleteReviewById(reviewId, token);
    }

    @Then("the review should be deleted successfully")
    public void the_review_should_be_deleted_successfully() {
        assertEquals(204, response.getStatusCode());
    }

    @When("I delete a review without providing the review ID")
    public void i_delete_a_review_without_providing_the_review_id() {
        response = ReviewApi.deleteReviewById(null, token);  // Passing null since no review ID is provided
    }

    @Then("^the review (retrieval|creation|update|deletion) should fail with error (.*)")
    public void the_review_should_fail_with_error(String action, String errorMessage) {
        // Check if the response contains the expected error message
        if (errorMessage.contains("Permission")) {
            assertEquals(403, response.statusCode());
        } else if (errorMessage.contains("not found")) {
            assertEquals(404, response.statusCode());
        } else {
            assertEquals(400, response.statusCode());
        }
        // Check the error message in the response
        assertEquals(errorMessage, response.jsonPath().getString("error"));
    }
}