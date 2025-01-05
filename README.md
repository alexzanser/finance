# Finances API Documentation

## 1. Budget API

### **POST** `api/budget?category=<string>&amount=<double>`

- **Description**: Used to set the budget for a specific category.
- **Request Headers**:
    - `X-User-Login` - Required user login.
    - `X-User-Password` - Required user password.
- **Query Params**:
    - `category` - The category of the budget (e.g., Food, Rent).
    - `amount` - The amount of the budget.

### **Example Request**:
```bash
curl --location --request POST 'http://localhost:8080/api/budget?category=%3Cstring%3E&amount=%3Cdouble%3E' \
--header 'X-User-Login: <string>' \
--header 'X-User-Password: <string>' \
--header 'Accept: */*'
```
```json
{
  "id": "<long>",
  "category": "<string>",
  "amount": "<double>",
  "wallet": {
    "id": "<long>",
    "user": {
      "id": "<long>",
      "login": "<string>",
      "password": "<string>",
      "wallet": {
        "value": "<string>"
      }
    },
    "transactions": [
      {
        "id": "<long>",
        "type": "<string>",
        "category": "<string>",
        "amount": "<double>",
        "date": "<dateTime>",
        "wallet": {
          "value": "<string>"
        }
      }
    ],
    "budgets": [
      {
        "value": "<string>"
      }
    ]
  }
}

```
## 2. Register API

### **POST** `api/register?login=<string>&password=<string>`

- **Description**: Used to register a new user.
- **Query Params**:
    - `login` - The login name of the user.
    - `password` - The password of the user.

### **Example Request**:
```bash
curl --location --request POST 'http://localhost:8080/api/register?login=user123&password=secretpassword' \
--header 'Accept: */*'
```
```json
{
"userLogin": "<string>",
"userPassword": "<string>"
}
```
## 3. Stats API

### **GET** `api/stats`

- **Description**: Used to fetch financial stats related to the user.
- **Request Headers**:
    - `X-User-Login` - Required user login.
    - `X-User-Password` - Required user password.

### **Example Request**:
```bash
curl --location 'http://localhost:8080/api/stats' \
--header 'X-User-Login: <string>' \
--header 'X-User-Password: <string>' \
--header 'Accept: */*'
```

## 4. Transactions API

### **POST** `api/transactions?type=<string>&category=<string>&amount=<double>`

- **Description**: Used to add a transaction.
- **Request Headers**:
    - `X-User-Login` - Required user login.
    - `X-User-Password` - Required user password.
- **Query Params**:
    - `type` - Type of transaction (e.g., expense, income).
    - `category` - The category of the transaction.
    - `amount` - The amount of money in the transaction.

### **Example Request**:
```bash
curl --location --request POST 'http://localhost:8080/api/transactions?type=Expense&category=Food&amount=50.00' \
--header 'X-User-Login: user123' \
--header 'X-User-Password: secretpassword' \
--header 'Accept: */*'
```
```json
{
  "id": "<long>",
  "type": "<string>",
  "category": "<string>",
  "amount": "<double>",
  "date": "<dateTime>",
  "wallet": {
    "value": "<string>"
  }
}
```
## 5. Transfer API

### **POST** `api/transfer?toLogin=<string>&amount=<double>`

- **Description**: Used to transfer funds between users.
- **Request Headers**:
    - `X-User-Login` - Required user login.
    - `X-User-Password` - Required user password.
- **Query Params**:
    - `toLogin` - The recipient's login name.
    - `amount` - The amount to transfer.

### **Example Request**:
```bash
curl --location --request POST 'http://localhost:8080/api/transfer?toLogin=anotherUser&amount=20.00' \
--header 'X-User-Login: user123' \
--header 'X-User-Password: secretpassword' \
--header 'Accept: */*'
```
