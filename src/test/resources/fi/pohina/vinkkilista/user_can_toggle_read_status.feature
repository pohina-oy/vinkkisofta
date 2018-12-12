Feature: User can toggle read status on available bookmarks

  Scenario: A user can mark an unread bookmark as read
    Given the user is logged in on homepage
    And   the first bookmark read status is "not read"
    When  the first bookmark marking button is clicked
    Then  the first bookmark read status contains "read on"
    
  Scenario: A user can mark a read bookmark as unread
    Given the user is logged in on homepage
    And   the first bookmark read status contains "read on"
    When  the first bookmark marking button is clicked
    Then  the first bookmark read status is "not read"

  Scenario: The correct date and time appears on read status
    Given the user is logged in on homepage
    And   the first bookmark is "not read"
    When  the first bookmark marking button is clicked
    Then  the first bookmark read status contains the current date
  
  Scenario: The read status of a new bookmark is "not read"
    Given the user is logged in on create page
    When  valid title "testBookmark" and valid url "http://www.somesite.org" are given
    Then  the new bookmark read status is "not read"
