package com.store.api;

import com.store.model.Order;
import com.store.utils.ConfigReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private static final String baseUri = ConfigReader.getBaseUrl(System.getProperty("env", "local"));
    private static final String ordersEndpoint = ConfigReader.getEndpoint("orders", "list");
    private static final String createOrderEndpoint = ConfigReader.getEndpoint("orders", "create");
    private static final String orderByIdEndpoint = ConfigReader.getEndpoint("orders", "getById");
    private static final String updateOrderEndpoint = ConfigReader.getEndpoint("orders", "update");
    private static final String deleteOrderEndpoint = ConfigReader.getEndpoint("orders", "delete");

    private static RequestSpecification withAuthHeader(String token) {
        return given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + token);
    }

    // Retrieve all orders (Admin only)
    public static Response getAllOrders(String token) {
        return withAuthHeader(token) // Admin token for authentication
                .when()
                .get(ordersEndpoint)
                .then()
                .extract().response();
    }

    // Retrieve a specific order by ID (Admin only)
    public static Response getOrderById(int orderId, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .when()
                .get(orderByIdEndpoint + orderId)
                .then()
                .extract().response();
    }

    // Create a new order
    public static Response createOrder(Order order, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .body(order)
                .when()
                .post(createOrderEndpoint)
                .then()
                .extract().response();
    }

    // Update an existing order by ID (Admin only)
    public static Response updateOrder(int orderId, Order order, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .body(order)
                .when()
                .put(updateOrderEndpoint + orderId)
                .then()
                .extract().response();
    }

    // Delete an order by ID (Admin only)
    public static Response deleteOrderById(Integer orderId, String token) {
        return withAuthHeader(token) // Admin token for authentication
                .when()
                .delete(deleteOrderEndpoint + orderId)
                .then()
                .extract().response();
    }
}