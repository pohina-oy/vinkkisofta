Feature: User can log in with Github

    Scenario: A new user can log in with Github
        Given the user is on the login page
        When  the "Sign in with Github" link is clicked
        Then  the user is logged in and redirected to the bookmarks

    Scenario: A logged out user cannot see the bookmark listing
        Given the user has not logged in
        When  the user navigates to /bookmarks/
        Then  the user is redirected to the login page