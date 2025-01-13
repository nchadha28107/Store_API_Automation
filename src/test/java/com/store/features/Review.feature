@review
Feature: Reviews API

  # Retrieve All Reviews
  @retrieve @positive
  Scenario: TC_1 User can successfully retrieve all reviews
    Given I retrieve all reviews
    Then the reviews should be returned successfully

  @retrieve @positive
  Scenario: TC_2 User can retrieve all reviews when no reviews are available
    Given there are no reviews available
    When I retrieve all reviews
    Then the reviews empty list should be returned successfully

  # Retrieve Single Review by ID
  @retrieve @positive
  Scenario: TC_3 User can successfully retrieve a review by ID
    Given I retrieve the review with ID "123"
    Then the review with ID "123" should be returned successfully

  @retrieve @negative
  Scenario: TC_4 User cannot retrieve a review by ID that does not exist
    Given I retrieve the review with ID "999"
    Then the review retrieval should fail with error Review not found

  # Create New Review
  @create @positive
  Scenario: TC_5 User can successfully create a new review
    Given I create a new review with productId "P123", userId "U456", rating "3" and comment "This is an excellent product!"
    Then the review should be created successfully

  @create @negative
  Scenario: TC_6 User cannot create a review with missing required productId
    Given I create a new review with productId "", userId "U456", rating "3" and comment "This is a good product."
    Then the review creation should fail with error ProductId is required

  @create @negative
  Scenario: TC_7 User cannot create a review with missing required productId
    Given I create a new review with productId "P124", userId "U456", rating "3" and comment "This is a good product."
    Then the review creation should fail with error Product not found

  @create @negative
  Scenario: TC_8 User cannot create a review with missing required userId
    Given I create a new review with productId "P123", userId "", rating "3" and comment "This is a good product."
    Then the review creation should fail with error UserId is required

  @create @negative
  Scenario: TC_9 User cannot create a review with missing required rating
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    Then the review creation should fail with error Rating is required

  @create @negative
  Scenario: TC_10 User cannot create a review with an empty comment
    Given I create a new review with productId "P123", userId "U456", rating "4" and comment ""
    Then the review creation should fail with error Comment is required

  @create @negative
  Scenario: TC_11 User cannot create a review with an invalid rating
    Given I create a new review with productId "P123", userId "U456", rating "6" and comment "Amazing product!"
    Then the review creation should fail with error Rating must be between 1 and 5

  # Update Review by ID
  @update @positive
  Scenario: TC_12 User can successfully update an existing review
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I update the review with ID 123 to have rating "4" and comment "Excellent product"
    Then the review should be updated successfully

  @update @negative
  Scenario: TC_13 User cannot update a review that does not exist
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I update the review with ID 999 to have rating "4" and comment "Updated This is a good product."
    Then the review update should fail with error Review not found

  @update @negative
  Scenario: TC_14 User cannot update a review with invalid rating
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I update the review with ID 123 to have rating "6" and comment "Excellent"
    Then the review update should fail with error Rating must be between 1 and 5

  @update @negative
  Scenario: TC_15 User cannot update a review with missing required rating
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I update the review with ID 123 to have rating "" and comment "Excellent"
    Then the review update should fail with error Rating is required

  @update @negative
  Scenario: TC_16 User cannot update a review with an empty comment
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I update the review with ID 123 to have rating "4" and comment ""
    Then the review update should fail with error Comment is required

  # Delete Review by ID
  @delete @positive
  Scenario: TC_17 User can successfully delete a review by ID
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I delete the review with ID 123
    Then the review should be deleted successfully

  @delete @negative
  Scenario: TC_18 User cannot delete a review that does not exist
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I delete the review with ID 999
    Then the review deletion should fail with error Review not found

  @delete @negative
  Scenario: TC_19 User cannot delete a review without providing the review ID
    Given I create a new review with productId "P123", userId "U456", rating "" and comment "This is a good product."
    When I delete a review without providing the review ID
    Then the review deletion should fail with error Review ID is required