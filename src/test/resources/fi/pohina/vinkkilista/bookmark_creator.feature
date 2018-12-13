Feature: When a bookmark is added the user is added as the creator

    Scenario: When a bookmark is added the logged in user is added as the creator
        Given user is logged in and new bookmark is selected
        When valid title "Title6" and valid url "http://www.somesite6.org" are given
        Then the user "testUser" is shown as creator of the bookmark with title "Title6"

    Scenario: Multiple bookmarks added by the logged in user show the user as the creator
        Given user is logged in and new bookmark is selected
        When valid title "Title7" and valid url "http://www.somesite7.org" are given
        And new bookmark is selected
        And valid title "Title8" and valid url "http://www.somesite8.org" are given
        Then the user "testUser" is shown as creator of the bookmark with title "Title7"
        And the user "testUser" is shown as creator of the bookmark with title "Title8"