-- Queries for peso_bank database

--Creating a new user
INSERT INTO users (username, password, user_status)
VALUES (?, ?, 'ACTIVE');

--Creating a user profile
INSERT INTO user_profiles (first_name, middle_name, last_name, email, contact_number, user_id)
VALUES (?, ?, ?, ?, ?, ?);

--Creating an account for a user
INSERT INTO accounts (account_number, balance, debt, account_type, is_active, user_id)
VALUES (?, ?, ?, ?, TRUE, ?);

--Selecting a user by username
SELECT user_id, username, password, user_status, created_at
FROM users
WHERE username = ?;

--Selecting a user by user id
SELECT user_id, username, password, user_status, created_at
FROM users
WHERE user_id = ?;

--Selecting all users
SELECT user_id, username, user_status, created_at
FROM users;

--Selecting a user's full profile with account details
SELECT u.user_id, u.username, u.user_status,
       p.first_name, p.middle_name, p.last_name, p.email, p.contact_number,
       a.account_id, a.account_number, a.balance, a.account_type, a.is_active
FROM users u
JOIN user_profiles p ON u.user_id = p.user_id
JOIN accounts a ON u.user_id = a.user_id
WHERE u.user_id = ?;

--Inquiry balance
SELECT account_id, account_number, balance, account_type, is_active
FROM accounts
WHERE account_id = ?;

--Inquiry balance by account number
SELECT account_id, account_number, balance, account_type, is_active
FROM accounts
WHERE account_number = ?;

--Withdraw funds
UPDATE accounts
SET balance = balance - ?
WHERE account_id = ? AND balance >= ?;

--Recording a withdraw transaction
INSERT INTO transactions (account_id, amount, transaction_type)
VALUES (?, ?, 'WITHDRAW');

--Deposit funds
UPDATE accounts
SET balance = balance + ?
WHERE account_id = ?;

--Recording a deposit transaction
INSERT INTO transactions (account_id, amount, transaction_type)
VALUES (?, ?, 'DEPOSIT');

--Transfer funds deduct from sender
UPDATE accounts
SET balance = balance - ?
WHERE account_id = ? AND balance >= ?;

--Transfer funds add to receiver
UPDATE accounts
SET balance = balance + ?
WHERE account_id = ?;

--Recording a transfer transaction
INSERT INTO transactions (account_id, receiver_account_id, amount, transaction_type)
VALUES (?, ?, ?, 'TRANSFER');

--Showcasing transaction history for an account
SELECT transaction_id, account_id, receiver_account_id, amount, transaction_type, transaction_date
FROM transactions
WHERE account_id = ?
ORDER BY transaction_date DESC;

--Showcasing transaction history within a date range
SELECT transaction_id, account_id, receiver_account_id, amount, transaction_type, transaction_date
FROM transactions
WHERE account_id = ? AND transaction_date BETWEEN ? AND ?
ORDER BY transaction_date DESC;

--Recording an audit trail entry
INSERT INTO audit_trails (actions, user_id)
VALUES (?, ?);

--Showcasing audit trail for a user
SELECT audit_id, actions, created_at
FROM audit_trails
WHERE user_id = ?
ORDER BY created_at DESC;

--Showcasing all audit trails
SELECT at.audit_id, at.actions, at.created_at, u.username
FROM audit_trails at
JOIN users u ON at.user_id = u.user_id
ORDER BY at.created_at DESC;
