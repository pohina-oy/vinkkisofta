Feature: When a bookmark is added the user is added as the creator

    Scenario: When a bookmark is added the logged in user is added as the creator
        Given new bookmark is selected
        When valid title "Title6" and valid url "http://www.somesite6.org" are given
        Then the user "testUser" is shown as creator of the bookmark with title "Title6"