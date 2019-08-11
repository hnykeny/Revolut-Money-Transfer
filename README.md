# Revolut-Money-Transfer
A RESTful API (including data model and the backing implementation) for money transfers between accounts.

## APIs
### Fetch account details
--
```http
GET /api/accounts
```
**Returns**
```javascript
[
    {
    "id" : long,
    "name" : string,
    "balance" : number upto 2 decimal places
    },
    ...
]
```
--
```http
GET /api/accounts/{id}
```
**Returns**
```javascript
{
    "id" : long,
    "name" : string,
    "balance" : number upto 2 decimal places
}
```
### Create new account**
```http
POST /api/accounts
```
RequestBody:

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `id` | `long` |  **Required**. Unique Customer Id  |
| `name` | `string` |  **Required**. Full name of the customer  |
| `balance` | `string` |  **Optional**. Opening balance parsed to decimal. If not provided, defaults to 0.00 |

**Transfer money**
```http
POST /api/accounts/transfer
```
RequestBody:

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `customerId` | `long` |  **Required**. Id of the customer  |
| `beneficiaryId` | `long` |  **Required**. Id of the beneficiary  |
| `amount` | `string` |  **Required**. amount to be transferred, parsed to double |


## Status Codes

Returns the following status codes:

| Status Code | Description |
| :--- | :--- |
| 200 | `OK` |
| 201 | `CREATED` |
| 202 | `ACCEPTED` |
| 400 | `BAD REQUEST` |
| 404 | `NOT FOUND` |
| 409 | `CONFLICT` |
| 500 | `INTERNAL SERVER ERROR` |

Error Response:
```javascript
{
    'message' : string
}
```