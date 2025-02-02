package specs;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;


import io.restassured.filter.log.RequestLoggingFilter;

import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.*;
import static io.restassured.filter.log.RequestLoggingFilter.with;
import static io.restassured.http.ContentType.JSON;

public class ApiSpec {

    static {
        RestAssured.basePath = "/api";
    }

    public static RequestSpecification baseRequestSpec = with()
            .filter(new RequestLoggingFilter())
            .filter(new ResponseLoggingFilter())
            .log().uri()
            .log().body()
            .log().headers()
            .contentType(JSON);

    public static RequestSpecification loginRequestSpec = baseRequestSpec
            .basePath("/login");

    public static RequestSpecification userRequestSpec = baseRequestSpec
            .basePath("/users");

    public static ResponseSpecification successResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(ALL)
            .build();

    public static ResponseSpecification createdResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .log(ALL)
            .build();

    public static ResponseSpecification errorResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(400)
            .log(ALL)
            .build();
}