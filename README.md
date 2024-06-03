
# customs-financials-session-cache

This repository is responsible for storing the duty deferment account number and associated data via a UUID (LinkId) to prevent the passing over a URL parameter

| Path                               | Description                                          |
| ---------------------------------  | ---------------------------------------------------- |
| GET /account-link/:id/:linkId | Retrieves the AccountLink based on the sessionId (id) and the LinkId (linkid)|
| POST /update-links | Stores the generated AccountLinks into mongo |
| DELETE /remove/:id  | Removes all associated links for a given EORI based on session id |


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



### License
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

## All tests and checks

This is a sbt command alias specific to this project. It will run a scala style check, run unit tests, run integration
tests and produce a coverage report:
> `sbt runAllChecks`
