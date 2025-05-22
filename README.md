
# customs-financials-session-cache

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Coverage](https://img.shields.io/badge/test_coverage-90-green.svg)](/target/scala-3.3.5/scoverage-report/index.html) [![Accessibility](https://img.shields.io/badge/WCAG2.2-AA-purple.svg)](https://www.gov.uk/service-manual/helping-people-to-use-your-service/understanding-wcag)

This repository is responsible for storing the duty deferment account number and associated data via a UUID (LinkId) to prevent the passing over a URL parameter

## Running the service

*From the root directory*

`sbt run` - starts the service locally

`sbt runAllChecks` - Will run all checks required for a successful build

Default service port on local - 9840

### Required dependencies

There are a number of dependencies required to run the service.

The easiest way to get started with these is via the service manager CLI - you can find the installation guide [here](https://docs.tax.service.gov.uk/mdtp-handbook/documentation/developer-set-up/set-up-service-manager.html)

| Command                                       | Description                                                      |
|-----------------------------------------------|------------------------------------------------------------------|
| `sm2 --start CUSTOMS_FINANCIALS_ALL`          | Runs all dependencies                                            |
| `sm2 -s`                                      | Shows running services                                           |
| `sm2 --stop CUSTOMS_FINANCIALS_SESSION_CACHE` | Stop the micro service                                           |
| `sbt run` or ` sbt "run 9840"`                | (from root dir) to compile the current service with your changes |

### Runtime Dependencies
(These are subject to change and may not include every dependency)

* `AUTH`
* `AUTH_LOGIN_STUB`
* `AUTH_LOGIN_API`
* `BAS_GATEWAY`
* `CA_FRONTEND`
* `SSO`
* `USER_DETAILS`

## Testing

The minimum requirement for test coverage is 90%. Builds will fail when the project drops below this threshold.

### Unit Tests

| Command                                | Description                  |
|----------------------------------------|------------------------------|
| `sbt test`                             | Runs unit tests locally      |
| `sbt "test/testOnly *TEST_FILE_NAME*"` | runs tests for a single file |

### Coverage

| Command                                  | Description                                                                                                 |
|------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `sbt clean coverage test coverageReport` | Generates a unit test coverage report that you can find here target/scala-3.3.5/scoverage-report/index.html |


## Feature Switches
Not applicable

## Available Routes

You can find a list of microservice specific routes here - `/conf/app.routes`

| Path                                  | Description                                                                             |
|---------------------------------------|-----------------------------------------------------------------------------------------|
| GET /account-link/:id/:linkId         | Retrieves the AccountLink based on the sessionId (id) and the LinkId (linkId)           |
| GET /account-links/:sessionId         | Retrieves the AccountLink based on the sessionId (sessionId)                            |
| GET /account-links/session/:sessionId | Retrieves the AccountLink based on the session (session) and the session Id (sessionId) |
| POST /update-links                    | Stores the generated AccountLinks into mongo                                            |
| DELETE /remove/:id                    | Removes all associated links for a given EORI based on session id                       |

## GET /account-link/:id/:linkId

Retrieves the AccountLink based on the sessionId (id) and the LinkId (linkid)

### Example Response body

```json
{
  "eori": "EORI",
  "accountNumber": "DAN",
  "accountStatus": "open",
  "accountStatusId": 1,
  "linkId": "UUID"
}
```

### Response codes

| Status                               | Description                                          |
| ---------------------------------  | ---------------------------------------------------- |
| 200 | An AccountLink found and returned        |
| 404 | No AccountLink found for the sessionId / linkId        |

##POST /update-links

Stores the generated AccountLinks into mongo

## Example Request body

```json
{
  "sessionId": "session",
  "accountLinks" : [
    {
      "eori": "EORI",
      "accountNumber": "DAN",
      "accountStatus": "open",
      "accountStatusId": 1,
      "linkId": "UUID"
    }
  ]
}
```

### Fields

| Field                               | Required                                          | Description                                          |
| ---------------------------------  | ---------------------------------------------------- | ---------------------------------------------------- |
| sessionId | Mandatory        | User's session ID        |
| accountLinks | Mandatory        | Sequence of account links        |
| accountLinks.eori | Mandatory        | User's EORI number        |
| accountLinks.accountNumber | Mandatory        | Account number for the specific account        |
| accountLinks.accountStatusId | Optional        | Status associated to the account        |
| accountLinks.linkId | Mandatory        | UUID to retrieve specific record       |

### Response codes

| Status                               | Description                                          |
| ---------------------------------  | ---------------------------------------------------- |
| 204 | Insert successful        |

### Account Status ID
| Status ID                               | Description                                          |
| ---------------------------------  | ---------------------------------------------------- |
| 0 | Deferment account available        |
| 1 | Change of legal entity        |
| 2 | Guarantee cancelled on Guarantors request        |
| 3 | Guarantee cancelled on Traders request        |
| 4 | Direct Debit mandate cancelled        |
| 5 | Direct Debit account closed / transferred        |
| 6 | Debit rejected refer to drawer       |
| 7 | Returned mail other        |
| 8 | Guarantee exceeded        |
| 9 | Account cancelled        |

### Account Status
| Status ID                               | Description                                          |
| ---------------------------------  | ---------------------------------------------------- |
| closed | Account is closed        |
| open | Account is open and available to use        |
| suspended | Account is suspended until action from user        |


## DELETE /remove/:id

Removes all associated links for a given EORI based on session id

### Response codes

| Status                               | Description                                          |
| ---------------------------------  | ---------------------------------------------------- |
| 204 | Delete successful        |

## Helpful commands

| Command                                       | Description                                                                                                 |
|-----------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `sbt runAllChecks`                            | Runs all standard code checks                                                                               |
| `sbt clean`                                   | Cleans code                                                                                                 |
| `sbt compile`                                 | Better to say 'Compiles the code'                                                                           |
| `sbt coverage`                                | Prints code coverage                                                                                        |
| `sbt test`                                    | Runs unit tests                                                                                             |
| `sbt it/test`                                 | Runs integration tests                                                                                      |
| `sbt scalafmtCheckAll`                        | Runs code formatting checks based on .scalafmt.conf                                                         |
| `sbt scalastyle`                              | Runs code style checks based on /scalastyle-config.xml                                                      |
| `sbt Test/scalastyle`                         | Runs code style checks for unit test code /test-scalastyle-config.xml                                       |
| `sbt coverageReport`                          | Produces a code coverage report                                                                             |
| `sbt "test/testOnly *TEST_FILE_NAME*"`        | runs tests for a single file                                                                                |
| `sbt clean coverage test coverageReport`      | Generates a unit test coverage report that you can find here target/scala-3.3.5/scoverage-report/index.html |
| `sbt "run -Dfeatures.some-feature-name=true"` | enables a feature locally without risking exposure                                                          |
