## Prerequisites

- Java 17
- Maven

---

## Build and Run Locally

1. Build the application:
mvn clean package

2. Run the application:
mvn spring-boot:run


3. Access Database
http://localhost:9002/h2-console/

- JDBC-URL: jdbc:h2:mem:library
- Username: sa

4. Postman collection under resources folder to access all the api endpoints