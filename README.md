# Product Catalog Service  

## Features  

- Create / update / delete product  
- Retrieve product by ID or category  
- Pagination support for product list  
- Caching with Redis (by ID and category)  
- API rate limiting by user (based on `X-User-Id` header)  
- Input validation with meaningful error responses  
- Swagger/OpenAPI documentation  
 
---  

## Setup Instructions  

```bash
git clone https://github.com/merfonteen/product-catalog-test-task.git
cd product-catalog-test-task  
mvn clean install
docker-compose-up --build
spring-boot:run
````

---

## Access at application at:  

**API Documentation:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui.html)

---

## Assumptions:  

- Each user is identified by a header X-User-Id, passed with each request that modifies data (create/update).
- Maximum allowed requests per user for product creation or update is 10 per minute.
- Redis is used to cache data and limit certain actions.


