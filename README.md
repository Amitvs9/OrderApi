# ☕: Order-API
<a href="https://foojay.io/works-with-openjdk"><img align="right" src="https://github.com/foojayio/badges/raw/main/works_with_openjdk/Works-with-OpenJDK.png" width="100"></a>


![Java](https://img.shields.io/badge/-Java-000?&logo=Java&logoColor=007396)
![Spring](https://img.shields.io/badge/-Spring-000?&logo=Spring)


## Description:
The order API Backend allows performing following operation.
- REST APIS
  * get all orders
  * create an order for given productId and emailId


## :shield:	 Code coverage
<img width="752" alt="sonar" src="https://user-images.githubusercontent.com/45259611/162644047-e2c65410-9903-4886-8a85-774b64049c38.png">


## :balance_scale:	Assumptions :
- Validations for Order creation are as 
  - emailId should be valid email id format
  - productId cannot have negative number
 
## :hammer_and_wrench:	Tech-Stack
![Java](https://img.shields.io/badge/-Java-000?&logo=Java&logoColor=007396)
![Spring](https://img.shields.io/badge/-Spring-000?&logo=Spring)	
- Java 8 
- Spring-Boot
- JPA
- In-Memory Database H2
- Open Api 3.0 (swagger)
- Maven
- Git Hub

## :memo: Steps to run the application

- Checkout the code / Download from git repo()
- checkout : open git bash and run command `git clone https://github.com/amitvs9/orderAPI.git`
- Option 1: Docker way of running (If docker installed on machine)
    - Open terminal and run docker-build-run.sh


- Option 2: Maven way of running
  - after checkout project open command prompt(cmd) or terminal
  - navigate to the project folder
  - run command `mvn clean install`
  - once its successfully build run command `mvn spring-boot: run`

Now application is up and running on http://localhost:8080/

## :grey_question:	How to test endpoints
### :spider_web:  Order-API
 - Open the URL in your browser : http://localhost:8080
 - User will see a swagger page with all the defined specs of the service.
 - There will have 2 Tags you can see.


### 1. Order-controller
#### Description:
- Endpoint 1: `GET /order/v1`
  - get all the orders with pagination support 
- Endpoint 2: `POST /order/v1`
  - Allows user to create order with post request body


### :test_tube: Testing using Swagger UI
![swagger copy](https://user-images.githubusercontent.com/45259611/162643934-9f371589-4eb7-4a4e-9a96-780734b6fd89.png)



