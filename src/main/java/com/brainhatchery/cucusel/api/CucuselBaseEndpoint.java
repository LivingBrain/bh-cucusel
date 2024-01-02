package com.brainhatchery.cucusel.api;

import com.brainhatchery.cucusel.api.utils.JsonHandler;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import org.testng.Assert;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

@SuppressWarnings("unchecked")
public abstract class CucuselBaseEndpoint<T extends CucuselBaseEndpoint<T>> {
    @Getter
    private RequestSpecification requestSpecification;
    private RestAssuredConfig config;
    private Response response;

    public CucuselBaseEndpoint(String baseUri, String basePath, RestAssuredConfig config) {
        createRequestSpecification();
        configureUri(baseUri, basePath);
        if (config != null) {
            this.config = config;
            setConfig(config);
        }
    }

    public CucuselBaseEndpoint(String baseUri, String basePath) {
        this(baseUri, basePath, null);
    }

    public T clearRequestSpecification() {
        createRequestSpecification();
        return (T) this;
    }

    public T setConfig(RestAssuredConfig config) {
        requestSpecification
                .config(config);
        this.config = config;
        return (T) this;
    }

    public T setRequestBody(Class<T> valueType, String jsonPathWithName) {
        requestSpecification
                .body(new JsonHandler().loadFileAsInputStream(valueType, jsonPathWithName));
        return (T) this;
    }

    public T setRequestBody(File file) {
        requestSpecification
                .body(file);
        return (T) this;
    }

    public T setRequestBody(InputStream body) {
        requestSpecification
                .body(body);
        return (T) this;
    }

    public T refreshRequestSpecification() {
        createRequestSpecification();
        if (config != null) {
            requestSpecification
                    .config(config);
        }
        return (T) this;
    }

    public T setBasicAuth(String userName, String userPassword) {
        requestSpecification
                .auth()
                .preemptive()
                .basic(userName, userPassword);
        return (T) this;
    }

    public T sendGetRequest() {
        response = requestSpecification
                .request(Method.GET);
        refreshRequestSpecification();
        return (T) this;
    }

    public T sendPostRequest() {
        response = requestSpecification
                .request(Method.POST);
        refreshRequestSpecification();
        return (T) this;
    }

    public T sendPutRequest() {
        response = requestSpecification
                .request(Method.PUT);
        refreshRequestSpecification();
        return (T) this;
    }

    public T sendDeleteRequest() {
        response = requestSpecification
                .request(Method.DELETE);
        refreshRequestSpecification();
        return (T) this;
    }

    public T setRequestBody(Object requestBody) {
        requestSpecification
                .body(requestBody);
        return (T) this;
    }

    private void assertStatusCode(int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode, "Expected status code did not matched actual status code. Response was not deserialized.");
    }

    protected T getResponseAs(Class<T> clazz, int expectedStatusCode) {
        if (response != null) {
            assertStatusCode(expectedStatusCode);
            return response.as(clazz);
        } else {
            throw new NullPointerException("There is no response to deserialize");
        }
    }


    protected T getResponseAs(Class<T> clazz) {
        if (response != null) {
            return response.as(clazz);
        } else {
            throw new NullPointerException("There is no response to deserialize");
        }
    }

    protected List<T> getResponseAsListOf(Class<T> clazz, int expectedStatusCode) {
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

    protected T detachHeader(String key) {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) this.requestSpecification;
        requestSpecification.removeHeader(key);
        return (T) this;
    }

    protected T removeCookie(String cookie) {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) this.requestSpecification;
        requestSpecification.removeCookie(cookie);
        return (T) this;
    }

    protected T clearReqQueryParams() {
        FilterableRequestSpecification requestSpecification = (FilterableRequestSpecification) this.requestSpecification;
        new ArrayList<>(requestSpecification.getQueryParams().keySet()).forEach(requestSpecification::removeQueryParam);
        return (T) this;
    }
}
