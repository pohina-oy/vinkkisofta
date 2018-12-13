Feature: Tags can be given as input when creating a bookmark and are shown in bookmark listing

    Scenario: A bookmark is created with a valid tag which is then shown in bookmark listing
        Given new bookmark is selected
        When valid title "Title00" and valid url "http://www.somesite.org" and valid tags "test00" are given
        Then tag "test00" is shown in bookmark listing

    Scenario: A bookmark is created with 2 valid tags which are then shown in bookmark listing
        Given new bookmark is selected
        When valid title "Title0" and valid url "http://www.somesite.org" and valid tags "test1, test2" are given
        Then tag "test1" is shown in bookmark listing
        And tag "test2" is shown in bookmark listing