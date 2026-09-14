Feature: Product Inventory Management

  Background:
    Given user is logged as a "standard_user"

  Scenario Outline: Sort products by price, view details, and navigate back
    When user sorts products by "<sortOption>"
    Then products should be displayed in "<sortOption>" sorted order

    When user views details for the first product
    Then product detail page should display matching name and price

    When user navigates back to product inventory page
    Then product inventory page should be displayed again

    Examples:
      | sortOption          |
      | Price (low to high) |
      | Price (high to low) |
      | Name (A to Z)       |
      | Name (Z to A)       |

