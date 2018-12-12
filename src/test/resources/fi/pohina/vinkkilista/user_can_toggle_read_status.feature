Feature: User can toggle read status on available bookmarks

  Scenario: A logged in user can logout from homepage
    Given the user is logged in on homepage
    When  the "logout" link is clicked
    Then  the user is redirected to the login page

  Scenario: A logged in user can logout from create page
    Given the user is logged in on search page
    When  the "logout" link is clicked
    Then  the user is redirected to the login page

  Scenario: A logged in user can logout search page
    Given the user is logged in on create page
    When  the "logout" link is clicked
    Then  the user is redirected to the login page
