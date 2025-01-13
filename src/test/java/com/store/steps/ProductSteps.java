package com.store.steps;

import com.store.api.ProductApi;
import com.store.enums.Context;
import com.store.model.Product;
import com.store.utils.MockServer;
import com.store.utils.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProductSteps {

    private Response response;
    private Product product;
    private ScenarioContext scenarioContext;
    private String token; // Authentication token, to be used in API calls

    @Given("I retrieve all products")
    public void i_retrieve_all_products() {
        response = ProductApi.getAllProducts(); // Make API call to retrieve all products
        scenarioContext.setContext(Context.RESPONSE, response);  // Storing the response in ScenarioContext using Enum
    }

    @Then("the products should be returned successfully")
    public void the_products_should_be_returned_successfully() {
        // Retrieve the response from ScenarioContext using Enum
        response = (Response) scenarioContext.getContext(Context.RESPONSE);

        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());

        // Assert that the response body contains the product data (i.e., should not be empty)
        assertTrue("Products list should not be empty", response.getBody().asString().length() > 0);
    }

    @Given("there are no products available")
    public void there_are_no_products_available() {
        MockServer.mockGetEmptyProducts();
    }

    @Then("the products empty list should be returned successfully")
    public void the_products_empty_list_should_be_returned_successfully() {
        // Retrieve the response from ScenarioContext using Enum
        response = (Response) scenarioContext.getContext(Context.RESPONSE);

        // Assert that the response status code is 200 (OK)
        assertEquals(200, response.getStatusCode());

        // Assert that the response body is an empty list (JSON)
        assertEquals("[]", response.getBody().asString());
    }

    @When("I create a new product with name {string}, description {string}, price {string} and stock {string}")
    public void i_create_a_new_product_with_name_description_price_and_stock(String name, String description, String price, String stock) {
        // Retrieve the token from ScenarioContext (this will be used for the admin check)
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);

        Double newPrice;
        if (price.isEmpty() || price.equals("null")) {
            newPrice = null;
        } else {
            newPrice = Double.parseDouble(price);
        }

        Integer newStock;
        if (stock.isEmpty() || stock.equals("null")) {
            newStock = null;
        } else {
            newStock = Integer.parseInt(stock);
        }

        // Create a product object using the provided data
        product = new Product(1, name, description, newPrice, newStock); // Assuming 0 as a placeholder for product ID

        // Create the product via Product API (using the token for admin authorization)
        response = ProductApi.createProduct(product, token);
    }

    @Then("the product should be created successfully")
    public void the_product_should_be_created_successfully() {
        // Check if the response status code is 201 (Created) and the product is in the response body
        assertEquals(201, response.statusCode());
        String productName = response.jsonPath().getString("name");
        assertEquals(product.getName(), productName); // Verify that the product name is as expected
    }

    @When("I delete the product with ID {int}")
    public void i_delete_the_product_with_id(int productId) {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        // Call the API to delete the product
        response = ProductApi.deleteProductById(productId, token);
    }

    @Then("the product should be deleted successfully")
    public void the_product_should_be_deleted_successfully() {
        // Assert status code 200 (success) and check for success message
        assertEquals("Expected successful deletion", 200, response.statusCode());
        String message = response.jsonPath().getString("message");
        assertEquals("Product deleted successfully", message);
    }

    @Then("^the product (creation|deletion) should fail with error (.+)")
    public void the_deletion_should_fail_with_error(String action, String errorMessage) {
        // Check for different types of error messages based on the scenario
        if (errorMessage.contains("Permission")) {
            assertEquals(403, response.statusCode());
        } else if (errorMessage.contains("not found")) {
            assertEquals(404, response.statusCode());
        } else {
            assertEquals(400, response.statusCode());
        }
        String error = response.jsonPath().getString("error");
        assertEquals("Expected error message", errorMessage, error);
    }

    @When("I delete a product without providing the product ID")
    public void i_try_to_delete_a_product_without_providing_the_product_id() {
        token = (String) ScenarioContext.getContext(Context.AUTH_TOKEN);
        // Attempting to delete a product without providing the product ID
        // The request should fail because the product ID is required
        response = ProductApi.deleteProductById(null, token);  // Passing null since no product ID is provided
    }
}