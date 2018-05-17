Feature: Login on Register area

  Scenario: Successful login

    Given a register document
    When  the user authenticate with the user "demo" and password "demo"
    Then  the user will see the message "Login Successfully"
    And   the response headers are valid
    And   the CSS links are not broken
    And   the Scripts links are not broken
    And   the Images links are not broken
    And   the Links are not broken

  Scenario: Unsuccessful login

    Given a register document
    When the user authenticate with the user "demo2" and password "demo2"
    Then the user will see the message "Enter your userName and password correct"
