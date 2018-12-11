Feature: Tags can be given as input when creating a bookmark and user can see them in bookmark listing

    Scenario: a bookmark is created with a valid tag that is then shown in bookmark listing
        Given new bookmark is selected
        When valid title "Title" and valid url "http://www.somesite.org" and valid tags "test" are given
        Then tag "test" is shown in bookmark listing

    Scenario: a bookmark is created with 2 valid tags that are then shown in bookmark listing
        Given new bookmark is selected
        When valid title "Title" and valid url "http://www.somesite.org" and valid tags "test1, test2" are given
        Then tag "test1" is shown in bookmark listing
        And tag "test2" is shown in bookmark listing