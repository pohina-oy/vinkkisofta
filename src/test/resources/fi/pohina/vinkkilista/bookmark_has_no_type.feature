Feature: Bookmark has no type

    Scenario: A Bookmark can be created without a type
        Given new bookmark is selected
        When valid title "Title5" and valid url "http://www.somesite5.org" are given
        Then bookmark with title "Title5" is listed

    Scenario: New bookmark page does not have type field
        Given new bookmark is selected
        Then there is no type field