#
#Build stage
#
FROM openjdk:19 as build
WORKDIR tinkoff-test
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
ARG DB_PASSWORD
ARG DB_USERNAME
ARG YANDEX_TOKEN
RUN ./mvnw -DDB_PASSWORD=${DB_PASSWORD} -DDB_USERNAME=${DB_USERNAME} -DYANDEX_TOKEN=${YANDEX_TOKEN} clean package
#
#Package stage
#
FROM openjdk:19
WORKDIR tinkoff-test
ARG DB_PASSWORD
ARG DB_USERNAME
ARG YANDEX_TOKEN
ENV DB_PASSWORD ${DB_PASSWORD}
ENV DB_USERNAME ${DB_USERNAME}
ENV YANDEX_TOKEN ${YANDEX_TOKEN}

COPY --from=build /tinkoff-test/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]