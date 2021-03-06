Feature: A bookmark can be created with valid inputs

  Scenario: bookmark is created with valid title and url
    Given new bookmark is selected
    When valid title "testBookmark" and valid url "http://www.somesite.org" are given
    Then a new bookmark is created

  Scenario: bookmark is created with valid title, url and author
    Given new bookmark is selected
    When valid title "testBookmark2" and valid url "http://www.somesite.org" and valid author "testAuthor" are given
    Then a new bookmark is created

  Scenario: bookmark is created with valid title, url, author and tags
    Given new bookmark is selected
    When valid title "This is the title" and valid url "http://www.somesite.org" and valid author "The Author" and valid tags "blog, testing" are given
    Then a new bookmark is created with given inputs

  Scenario: bookmark is not created with missing title
    Given new bookmark is selected
    When missing title "" and valid url "http://www.somesite.org" are given
    Then a new bookmark is not created

  Scenario: bookmark is not created with missing url
    Given new bookmark is selected
    When valid title "testBookmark4" and missing url "" are given
    Then a new bookmark is not created