Feature: Added bookmarks are shown as a list on the bookmark listing page

    Scenario: Added bookmarks are shown on the bookmark listing page
        Given new bookmark is selected
        When valid title "Title1" and valid url "http://www.somesite1.org" are given
        And new bookmark is selected
        And valid title "Title2" and valid url "http://www.somesite2.org" are given
        Then bookmark with title "Title1" is listed
        And bookmark with title "Title2" is listed

    Scenario: A bookmark that has not been added is not shown on the bookmark listing page
        Given new bookmark is selected
        When valid title "Title3" and valid url "http://www.somesite3.org" are given
        Then bookmark with title "Title4" is not listed