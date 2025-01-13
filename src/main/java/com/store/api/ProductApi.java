package com.store.api;

import com.store.model.Product;
import com.store.utils.ConfigReader;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ProductApi {

    private static final String baseUri = ConfigReader.getBaseUrl(System.getProperty("env", "local"));
    private static final String productsEndpoint = ConfigReader.getEndpoint("products", "list");
    private static final String createProductEndpoint = ConfigReader.getEndpoint("products", "create");
    private static final String deleteProductEndpoint = ConfigReader.getEndpoint("products", "delete");

    private static RequestSpecification withAuthHeader(String token) {
        return given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + token);
    }
    // Retrieve all products
    public static Response getAllProducts() {
        return given()
                .baseUri(baseUri)  // Use the environment-specific base URI
                .when()
                .get(productsEndpoint)
                .then()
                .extract().response();
    }

    // Create a product (admin only)
    public static Response createProduct(Product product, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .body(product)
                .when()
                .post(createProductEndpoint)
                .then()
                .extract().response();
    }

    // Delete a product by ID (admin only)
    public static Response deleteProductById(Integer productId, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .when()
                .delete(deleteProductEndpoint + productId)
                .then()
                .extract().response();
    }
}
