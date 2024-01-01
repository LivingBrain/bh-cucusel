package com.brainhatchery.cucusel.api;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public abstract class BaseEndpoint {

    private RequestSpecification requestSpecification;
    private RestAssuredConfig config;
    private Response response;
    private String pathToRequestBodyJson;
    private String baseUri;
    private String basePath;

    public BaseEndpoint(String baseUri, String basePath, String pathToRequestBodyJson, RestAssuredConfig config) {
        this.baseUri = baseUri;
        this.basePath = basePath;
        this.pathToRequestBodyJson = pathToRequestBodyJson;
        this.config = config;
        createRequestSpecification();
        configureUri(baseUri, basePath);
    }

    public BaseEndpoint(String baseUri, String basePath, String pathToRequestBodyJson) {
        this(baseUri, basePath, pathToRequestBodyJson, null);
    }

    public BaseEndpoint(String baseUri, String basePath, RestAssuredConfig config) {
        this(baseUri, basePath, null, config);
    }

    public BaseEndpoint(String baseUri, String basePath) {
        this(baseUri, basePath, null, null);
    }

    public BaseEndpoint refreshRequestSpecification() {
        createRequestSpecification();
        return this;
    }

    public RequestSpecification getConfiguredRequestSpecification() {
        if (config != null) {
            requestSpecification.config(config);
        } else {
            throw new NullPointerException("Rest Assured config was not provided. Provide config with BaseEndpoint constructor or with setConfig() method.");
        }
        return requestSpecification;
    }

    public BaseEndpoint setConfig(RestAssuredConfig config) {
        this.config = config;
        return this;
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    public BaseEndpoint setPathToRequestBodyJson(String pathToRequestBodyJson) {
        this.pathToRequestBodyJson = pathToRequestBodyJson;
        return this;
    }

    public BaseEndpoint setBasicAuth(String userName, String userPassword) {
        requestSpecification
                .auth()
                .preemptive()
                .basic(userName, userPassword);
        return this;
    }

    public BaseEndpoint sendGetRequest() {
        response = requestSpecification
                .request(Method.GET);
        return this;
    }

    public BaseEndpoint setRequestBody(Object requestBody) {
        requestSpecification
                .body(requestBody);
        return this;
    }

    public BaseEndpoint sendPostRequest() {
        response = requestSpecification
                .request(Method.POST);
        return this;
    }

    private void assertStatusCode(int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode, "Expected status code did not matched actual status code. Response was not deserialized.");
    }

    protected <T>T getResponseAs(Class<T> clazz, int expectedStatusCode) {
        if (response != null) {
            assertStatusCode(expectedStatusCode);
            return response.as(clazz);
        } else {
            throw new NullPointerException("There is no response to deserialize");
        }
    }


    protected <T>T getResponseAs(Class<T> clazz) {
        if (response != null) {
            return response.as(clazz);
        } else {
            throw new NullPointerException("There is no response to deserialize");
        }
    }

    protected <T> List<T> getResponseAsListOf(Class<T> clazz, int expectedStatusCode) {
        if (response != null) {
            assertStatusCode(expectedStatusCode);
            return response.as(TypeFactory.defaultInstance().constructCollectionLikeType(ArrayList.class, clazz));
        } else {
            throw new NullPointerException("There is no response to deserialize");
        }
    }

    private void createRequestSpecification() {
        requestSpecification = given();
    }

    private void configureUri(String baseUri, String basePath) {
        requestSpecification
                .baseUri(baseUri)
                .basePath(basePath);
    }

    protected void detachHeader(String key) {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) this.requestSpecification;
        requestSpecification.removeHeader(key);
    }

    protected void removeCookie(String cookie) {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) this.requestSpecification;
        requestSpecification.removeCookie(cookie);
    }

    protected void clearReqQueryParams() {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) this.requestSpecification;
        new ArrayList<>(requestSpecification.getQueryParams().keySet()).forEach(requestSpecification::removeQueryParam);
    }
}
