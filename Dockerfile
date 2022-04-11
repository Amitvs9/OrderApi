FROM openjdk:17-ea-22-jdk-oracle
EXPOSE 8080
ADD target/order-api.jar order-api.jar
ENTRYPOINT ["java","-jar","/order-api.jar"]