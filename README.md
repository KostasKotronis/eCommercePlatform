# 🧾 Order Management Service

A Spring Boot microservice that manages **Orders** and **OrderLines**.  
A scheduler runs every 60 s to mark *unprocessed* orders as *processed*.

---

## 🚀 Quick Setup

### 🐳 Run with Docker
```bash
docker compose down -v   # optional reset
docker compose up --build
```

### 🧪 Run Locally

Requires Java 17+ and PostgreSQL 14+.

src/main/resources/application.properties

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/public_db
spring.datasource.username=public_user
spring.datasource.password=publicN3xt!
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
```
Run the app:

```bash
mvn spring-boot:run
```

### 🧩 API Overview
| Method | 	Endpoint | Description |
| -------- | ------- |------- |
|POST	|/orders	|Create a new order|
|GET	|/orders	|Get all orders|
|GET	|/orders/{id}	|Get order by ID|
|PUT	|/orders/{id}	|Update order|
|DELETE	|/orders/{id}	|Delete order|

### Example Request
```bash
{
  "customerName": "John Doe",
  "lines": [
    { "productId": 101, "quantity": 2, "price": 19.90 }
  ]
}
```

### Example Response
```bash
{
  "id": 1,
  "customerName": "John Doe",
  "status": "unprocessed",
  "orderDate": "2025-11-07T15:00:00",
  "lines": [
    { "id": 1, "productId": 101, "quantity": 2, "price": 19.90 }
  ]
}
```

### 🕒 Scheduler

Runs every 60 s, automatically updating all
status = "unprocessed" → status = "processed".

Logs example:
```bash
Scheduler processed 3 unprocessed orders
```

### 🧾 Testing with Postman
Feel free to import the collection in order to test it locally:
```bash
postman/eCommercePlatform.postman_collection.json
```
