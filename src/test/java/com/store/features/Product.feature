@product
Feature: Products API

  # Retrieve All Products
  @retrieve @positive
  Scenario: TC_1 User can successfully retrieve all products
    Given I retrieve all products
    Then the products should be returned successfully

  @retrieve @positive
  Scenario: TC_2 User can retrieve all products when no products are available
    Given there are no products available
    When I retrieve all products
    Then the products empty list should be returned successfully

  # Create New Product (Admin Only)
  @create @positive
  Scenario: TC_3 Admin can successfully create a new product
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new product with name "Product A", description "Description A", price "100.0" and stock "50"
    Then the product should be created successfully

  @create @negative
  Scenario: TC_4 User cannot create a new product without admin rights
    Given I log in with username "nonAdminValidUser7", password "password8" and email "email7"
    When I create a new product with name "Product B", description "Description B", price "200.0" and stock "30"
    Then the product creation should fail with error Permission denied

  @create @negative
  Scenario: TC_5 User cannot create a new product with missing required name field
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new product with name "", description "Description B", price "200.0" and stock "30"
    Then the product creation should fail with error Product name is required

  @create @negative
  Scenario: TC_6 User cannot create a new product with missing required description field
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new product with name "Product C", description "", price "200.0" and stock "30"
    Then the product creation should fail with error Product description is required

  @create @negative
  Scenario: TC_7 User cannot create a new product with missing required price field
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new product with name "Product C", description "Description C", price "" and stock "30"
    Then the product creation should fail with error Product price is required

  @create @negative
  Scenario: TC_8 User cannot create a new product with missing required stock field
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new product with name "Product C", description "Description C", price "200.0" and stock ""
    Then the product creation should fail with error Product stock is required

  @create @negative
  Scenario: TC_9 User cannot create a new product with invalid price
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new product with name "Product C", description "Description C", price "-100.0" and stock "50"
    Then the product creation should fail with error Price cannot be negative

  @create @negative
  Scenario: TC_10 User cannot create a new product with invalid stock
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I create a new product with name "Product C", description "Description C", price "100.0" and stock "0"
    Then the product creation should fail with error Product stock must be greater than zero

  # Delete Product by ID (Admin Only)
  @delete @positive
  Scenario: TC_11 Admin can delete a product by ID
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I delete the product with ID 1
    Then the product should be deleted successfully

  @delete @negative
  Scenario: TC_12 User cannot delete a product without admin rights
    Given I log in with username "nonAdminValidUser7", password "password8" and email "email7"
    When I delete the product with ID 1
    Then the product deletion should fail with error Permission denied

  @delete @negative
  Scenario: TC_13 User cannot delete a non-existing product
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I delete the product with ID 9999
    Then the product deletion should fail with error Product not found

  @delete @negative
  Scenario: TC_14 User cannot delete a product without providing the product ID
    Given I log in with username "validUser7", password "password8" and email "email7"
    When I delete a product without providing the product ID
    Then the product deletion should fail with error Product ID is required