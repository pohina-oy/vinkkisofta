Feature: Tags are added to a bookmark based on url

    Scenario: A video bookmark is created with video tag automatically added
        Given new bookmark is selected
        When valid title "Rick Roll" and valid url "https://www.youtube.com/watch?v=dQw4w9WgXcQ" are given
        And search bookmarks is selected
        And valid tag "video" is given as search input
        Then a bookmark with title "Rick Roll" is listed

    Scenario: A blog bookmark is created with blog tag automatically added
        Given new bookmark is selected
        When valid title "Nanoscale views" and valid url "https://nanoscale.blogspot.com" are given
        And search bookmarks is selected
        And valid tag "blog" is given as search input
        Then a bookmark with title "Nanoscale views" is listed

