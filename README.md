# Banking Management System

**Tools:** Java 17, Spring Boot 3, Hibernate (Spring Data JPA), MySQL, Spring Security

Banking application with account management, fund transfers and transaction history,
exposed through REST APIs secured with Spring Security (HTTP Basic + BCrypt).

## Features
- User registration and authentication (BCrypt-hashed passwords, stateless HTTP Basic)
- Create / view accounts (SAVINGS, CURRENT) with unique account numbers
- Deposit, withdraw, and atomic fund transfers (pessimistic row locks, ordered to avoid deadlocks)
- Transaction history per account
- Ownership checks: users only access their own accounts (ADMIN role can access all)
- Bean validation + centralized JSON error handling

## Run
1. Install Java 17+, Maven, and MySQL.
2. Set credentials (or edit `src/main/resources/application.properties`):
   ```
   export DB_USERNAME=root
   export DB_PASSWORD=yourpassword
   ```
3. Start: `mvn spring-boot:run` (database `bankdb` is auto-created; tables via Hibernate).

## API

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | Register a user |
| POST | `/api/accounts` | yes | Open an account |
| GET | `/api/accounts` | yes | List my accounts |
| GET | `/api/accounts/{no}` | yes | Account details |
| POST | `/api/accounts/{no}/deposit` | yes | Deposit |
| POST | `/api/accounts/{no}/withdraw` | yes | Withdraw |
| POST | `/api/transfers` | yes | Transfer funds |
| GET | `/api/accounts/{no}/transactions` | yes | Transaction history |

## Try it
```bash
curl -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"secret123"}'

curl -u alice:secret123 -X POST localhost:8080/api/accounts -H 'Content-Type: application/json' \
  -d '{"holderName":"Alice","accountType":"SAVINGS","initialDeposit":1000}'

curl -u alice:secret123 -X POST localhost:8080/api/transfers -H 'Content-Type: application/json' \
  -d '{"fromAccount":"BK0000000001","toAccount":"BK0000000002","amount":250.00,"description":"Rent"}'

curl -u alice:secret123 localhost:8080/api/accounts/BK0000000001/transactions
```

To create an admin, set `role` to `ADMIN` for a user directly in the `users` table.
