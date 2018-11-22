Feature: A bookmark can be created with valid inputs

  Scenario: blog is created with valid title and url
    Given new bookmark is selected
    And type is set to blog
    When valid title "uusi" and valid url "http://www.somesite.org" are given
    Then a new blog is created

  Scenario: blog is created with valid title, url and author
    Given new bookmark is selected
    And type is set to blog
    When valid title "uusi" and valid url "http://www.somesite.org" and valid author "testimies" are given
    Then a new blog is created

  Scenario: blog is not created with missing title
    Given new bookmark is selected
    And type is set to blog
    When missing title "" and valid url "http://www.somesite.org" are given
    Then a new blog is not created

  Scenario: blog is not created with missing url
    Given new bookmark is selected
    And type is set to blog
    When valid title "uusi" and missing url "" are given
    Then a new blog is not created