package com.store.steps;

import com.store.api.OrderApi;
import com.store.enums.Context;
import com.store.model.Order;
import com.store.utils.MockServer;
import com.store.utils.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderSteps {

    private Response response;
    private Order order;
    private ScenarioContext scenarioContext;
    private String token;

    // Retrieve All Orders
    @Given("I retrieve all orders")
    public void i_retrieve_all_orders() {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        response = OrderApi.getAllOrders(token); // Make API call to retrieve all orders
        scenarioContext.setContext(Context.RESPONSE, response);  // Store the response
    }

    @Then("^the (orders|order) should be returned successfully")
    public void the_orders_should_be_returned_successfully(String action) {
        response = (Response) scenarioContext.getContext(Context.RESPONSE);
        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());
        // Assert that the response body contains order data (i.e., should not be empty)
        assertTrue(action + " list should not be empty", response.getBody().asString().length() > 0);
    }

    @Given("there are no orders available")
    public void there_are_no_order_available() {
        MockServer.mockGetEmptyOrder();
    }

    @Then("the orders empty list should be returned successfully")
    public void the_orders_empty_list_should_be_returned_successfully() {
        // Retrieve the response from ScenarioContext using Enum
        response = (Response) scenarioContext.getContext(Context.RESPONSE);

        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());

        // Assert that the response body is an empty list (JSON)
        assertEquals("[]", response.getBody().asString());
    }

    // Retrieve Single Order by ID
    @When("I retrieve the order with ID {int}")
    public void i_retrieve_the_order_with_id(int orderId) {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        response = OrderApi.getOrderById(orderId, token);
        scenarioContext.setContext(Context.RESPONSE, response);
    }

    @Then("the order with ID {int} should be returned successfully")
    public void the_order_with_id_should_be_returned_successfully(int orderId) {
        response = (Response) scenarioContext.getContext(Context.RESPONSE);
        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());
        // Assert that the order ID matches the expected order ID
        assertEquals(orderId, response.jsonPath().getInt("id"));
    }

    @When("I create a new order with productId {string}, userId {string}, quantity {string} and totalAmount {string}")
    public void i_create_a_new_order_with_productId_userId_quantity_and_totalAmount(String productId, String userId, String quantity, String totalAmount) {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);

        Double newTotalAmount;
        if (totalAmount.isEmpty() || totalAmount.equals("null")) {
            newTotalAmount = null;
        } else {
            newTotalAmount = Double.parseDouble(totalAmount);
        }

        Integer newQuantity;
        if (quantity.isEmpty() || quantity.equals("null")) {
            newQuantity = null;
        } else {
            newQuantity = Integer.parseInt(quantity);
        }

        order = new Order(1, productId, userId, newQuantity, newTotalAmount);  // Prepare the order to be created
        response = OrderApi.createOrder(order, token);
    }

    @Then("the order should be created successfully")
    public void the_order_should_be_created_successfully() {
        assertEquals(201, response.getStatusCode());
        assertTrue("Order was created successfully", response.jsonPath().getInt("id") > 0);
    }

    @Then("^the order (retrieval|creation|update|deletion) should fail with error (.*)")
    public void the_order_should_fail_with_error(String action, String errorMessage) {
        // Check if the response contains the expected error message
        if (errorMessage.contains("Permission")) {
            assertEquals(403, response.statusCode());
        } else if (errorMessage.contains("not found")) {
            assertEquals(404, response.statusCode());
        } else {
            assertEquals(400, response.statusCode());
        }
        assertEquals(errorMessage, response.jsonPath().getString("error")); // Verify that the error message is as expected
    }

    // Update Order
    @When("I update the order with ID {int} to have quantity {string} and totalAmount {string}")
    public void i_update_the_order_with_id_to_have_quantity_and_totalAmount(int orderId, String newQuantity, String newTotalAmount) {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        if (newQuantity.isEmpty() || newQuantity.equals("null")) {
            order.setQuantity(Integer.parseInt(null));
        } else {
            order.setQuantity(Integer.parseInt(newQuantity));
        }
        if (newTotalAmount.isEmpty() || newTotalAmount.equals("null")) {
            order.setTotalAmount(null);
        } else {
            order.setTotalAmount(Double.parseDouble(newTotalAmount));
        }
        response = OrderApi.updateOrder(orderId, order, token);
    }

    @Then("the order should be updated successfully")
    public void the_order_should_be_updated_successfully() {
        assertEquals(200, response.getStatusCode());
        assertEquals(order.getQuantity(), Integer.valueOf(response.jsonPath().getInt("quantity")));
        assertEquals(order.getTotalAmount(), response.jsonPath().getDouble("totalAmount"), 0.01);
    }

    // Delete Order
    @When("I delete the order with ID {int}")
    public void i_delete_the_order_with_id(int orderId) {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        System.out.println(token);
        response = OrderApi.deleteOrderById(orderId, token);
    }

    @Then("the order should be deleted successfully")
    public void the_order_should_be_deleted_successfully() {
        assertEquals(204, response.getStatusCode());
    }

    @When("I delete an order without providing the order ID")
    public void i_delete_an_order_without_providing_the_order_id() {
        response = OrderApi.deleteOrderById(null, token); // Assuming 0 or null can be used for invalid ID
    }
}
