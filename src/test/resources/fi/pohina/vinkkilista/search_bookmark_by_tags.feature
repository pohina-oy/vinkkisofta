Feature: User can search bookmarks by tags

    Scenario: matching bookmarks are returned with single valid tag given
        Given search bookmarks is selected
        When  valid tag "blog" is given as search input
        Then  only bookmarks with tag "blog" are listed

    Scenario: matching bookmarks are returned with multiple valid tags given
        Given search bookmarks is selected
        When  valid tags "blog,video" are given as search input
        Then  only bookmarks with one of the tags "blog,video" are listed

    Scenario: matching bookmarks are returned with some valid and some unmatching tags given
        Given search bookmarks is selected
        When  valid tags "blog,video" and unmatching tags "journal,educational" are given as search input
        Then  only bookmarks with one of the tags "blog,video" are listed

    Scenario: no bookmarks are returned with no valid tags given
        Given search bookmarks is selected
        When  no valid tag "" is given as search input
        Then  no bookmarks are listed

    Scenario: no bookmarks are returned when no matching valid tags are given
        Given search bookmarks is selected
        When  no matching valid tags "journal,educational" are given as search input
        Then  no bookmarks are listed

