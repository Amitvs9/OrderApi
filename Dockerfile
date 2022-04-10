FROM openjdk:8
EXPOSE 8080
ADD target/order-api.jar order-api.jar
ENTRYPOINT ["java","-jar","/order-api.jar"]