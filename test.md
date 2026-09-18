# Peso Bank - Transaction & Audit Trail Test Cases

Manual test cases for the deposit/withdraw/transfer and audit trail feature.
Base URL: `http://localhost:8080`

Run **Setup** first to get real IDs, then substitute `{accountAId}`, `{accountBId}`,
`{userAId}`, `{userBId}`, `{userCId}`, `{accountCId}` below with the actual numbers
returned by those calls.

---

## 0. Setup

### Create User A
`POST /api/users`
```json
{
  "username": "qa_user_a",
  "password": "Passw0rd!",
  "confirmPassword": "Passw0rd!"
}
```
Expected: `201 Created` — note the returned `userId` as `{userAId}`.

### Create User B
`POST /api/users`
```json
{
  "username": "qa_user_b",
  "password": "Passw0rd!",
  "confirmPassword": "Passw0rd!"
}
```
Expected: `201 Created` — note the returned `userId` as `{userBId}`.

### Create User C (stays idle, used for empty-list checks)
`POST /api/users`
```json
{
  "username": "qa_user_c",
  "password": "Passw0rd!",
  "confirmPassword": "Passw0rd!"
}
```
Expected: `201 Created` — note the returned `userId` as `{userCId}`.

### Create Account A (for User A)
`POST /api/accounts`
```json
{
  "userId": {userAId},
  "accountType": "SAVINGS"
}
```
Expected: `201 Created`, `balance: 0.00` — note the returned `accountId` as `{accountAId}`.

### Create Account B (for User B)
`POST /api/accounts`
```json
{
  "userId": {userBId},
  "accountType": "SAVINGS"
}
```
Expected: `201 Created` — note the returned `accountId` as `{accountBId}`.

### Create Account C (for User C, stays untouched)
`POST /api/accounts`
```json
{
  "userId": {userCId},
  "accountType": "SAVINGS"
}
```
Expected: `201 Created` — note the returned `accountId` as `{accountCId}`.

---

## 1. Deposit

### TC-01 Deposit — happy path
`POST /api/transactions/deposit`
```json
{
  "accountId": {accountAId},
  "amount": 1000.00
}
```
Expected: `201 Created`, `transactionType: "DEPOSIT"`, `amount: 1000.00`.
Account A balance becomes `1000.00`.

### TC-02 Deposit — zero amount
`POST /api/transactions/deposit`
```json
{
  "accountId": {accountAId},
  "amount": 0
}
```
Expected: `400 Bad Request`.

### TC-03 Deposit — negative amount
`POST /api/transactions/deposit`
```json
{
  "accountId": {accountAId},
  "amount": -50
}
```
Expected: `400 Bad Request`.

### TC-04 Deposit — missing accountId
`POST /api/transactions/deposit`
```json
{
  "amount": 100
}
```
Expected: `400 Bad Request`.

### TC-05 Deposit — non-existent account
`POST /api/transactions/deposit`
```json
{
  "accountId": 999999999,
  "amount": 100
}
```
Expected: `404 Not Found`.

---

## 2. Withdraw

### TC-06 Withdraw — happy path, within balance
`POST /api/transactions/withdraw`
```json
{
  "accountId": {accountAId},
  "amount": 200.00
}
```
Expected: `201 Created`, `transactionType: "WITHDRAWAL"`.
Account A balance becomes `800.00` (assuming TC-01 ran first).

### TC-07 Withdraw — more than balance
`POST /api/transactions/withdraw`
```json
{
  "accountId": {accountAId},
  "amount": 999999.00
}
```
Expected: `409 Conflict` — "Insufficient balance".

### TC-08 Withdraw — zero amount
`POST /api/transactions/withdraw`
```json
{
  "accountId": {accountAId},
  "amount": 0
}
```
Expected: `400 Bad Request`.

### TC-09 Withdraw — non-existent account
`POST /api/transactions/withdraw`
```json
{
  "accountId": 999999999,
  "amount": 50
}
```
Expected: `404 Not Found`.

---

## 3. Transfer

### TC-10 Transfer — happy path, A to B
`POST /api/transactions/transfer`
```json
{
  "fromAccountId": {accountAId},
  "toAccountId": {accountBId},
  "amount": 300.00
}
```
Expected: `201 Created`, `transactionType: "TRANSFER_OUT"`, `receiverAccountId: {accountBId}`.
Account A balance becomes `500.00`, Account B balance becomes `300.00`
(assuming TC-01 and TC-06 ran first).

### TC-11 Transfer — to the same account
`POST /api/transactions/transfer`
```json
{
  "fromAccountId": {accountAId},
  "toAccountId": {accountAId},
  "amount": 10.00
}
```
Expected: `400 Bad Request` — "Cannot transfer to the same account".

### TC-12 Transfer — insufficient balance
`POST /api/transactions/transfer`
```json
{
  "fromAccountId": {accountAId},
  "toAccountId": {accountBId},
  "amount": 999999.00
}
```
Expected: `409 Conflict` — "Insufficient balance".

### TC-13 Transfer — non-existent source account
`POST /api/transactions/transfer`
```json
{
  "fromAccountId": 999999999,
  "toAccountId": {accountBId},
  "amount": 10.00
}
```
Expected: `404 Not Found`.

### TC-14 Transfer — non-existent destination account
`POST /api/transactions/transfer`
```json
{
  "fromAccountId": {accountAId},
  "toAccountId": 999999999,
  "amount": 10.00
}
```
Expected: `404 Not Found`.

### TC-15 Transfer — zero/negative amount
`POST /api/transactions/transfer`
```json
{
  "fromAccountId": {accountAId},
  "toAccountId": {accountBId},
  "amount": -10
}
```
Expected: `400 Bad Request`.

### TC-16 Transfer — concurrent transfers (manual race-condition check)
`POST /api/transactions/transfer`
```json
{
  "fromAccountId": {accountAId},
  "toAccountId": {accountBId},
  "amount": 250.00
}
```
Fire this exact request twice in quick succession (two Postman tabs, or no-delay
Runner with 2 iterations) with an amount close to half of Account A's current
balance. Expected: exactly one call succeeds (`201`), the other fails with
`409 Insufficient balance` — the balance should never go negative. This
exercises the `PESSIMISTIC_WRITE` lock in `AccountRepository.findByIdForUpdate`.

---

## 4. Transaction Queries

### TC-17 Get transaction by id
`GET /api/transactions/{transactionId}`

Use a `transactionId` returned by TC-01 or TC-10.
Expected: `200 OK`, body matches that transaction.

### TC-18 Get transaction by id — not found
`GET /api/transactions/999999999`

Expected: `404 Not Found`.

### TC-19 Get statement for Account A
`GET /api/transactions/account/{accountAId}`

Expected: `200 OK`, array of 3 entries (`DEPOSIT`, `WITHDRAWAL`, `TRANSFER_OUT`
from TC-01/TC-06/TC-10), newest first.

### TC-20 Get statement for Account B
`GET /api/transactions/account/{accountBId}`

Expected: `200 OK`, array of 1 entry: `TRANSFER_IN`, `receiverAccountId: {accountAId}`.

### TC-21 Get statement for an account with no activity
`GET /api/transactions/account/{accountCId}`

Expected: `200 OK`, empty array `[]`.

---

## 5. Audit Trail

### TC-22 Get audit trail for User A
`GET /api/audits/user/{userAId}`

Expected: `200 OK`, array of 3 entries whose `actions` text mentions
"Deposited", "Withdrew" and "Transferred" (from TC-01/TC-06/TC-10).

### TC-23 Get audit trail for User B
`GET /api/audits/user/{userBId}`

Expected: `200 OK`, array of 1 entry whose `actions` text mentions "Received"
(from TC-10).

### TC-24 Get audit trail for User C — no actions
`GET /api/audits/user/{userCId}`

Expected: `200 OK`, empty array `[]`.

---

## 6. Notes — not exercisable through the API yet

### TC-25 Inactive account rejection
`POST /api/transactions/deposit`
```json
{
  "accountId": {accountAId},
  "amount": 10.00
}
```
There's no endpoint yet to deactivate an account. To exercise this path, run
directly against Postgres first:
```sql
UPDATE accounts SET is_active = false WHERE account_id = {accountAId};
```
Then send the request above — expected `409 Conflict` — "Account is not active".
Afterwards, flip `is_active` back to `true` to keep using Account A for other
test cases.
