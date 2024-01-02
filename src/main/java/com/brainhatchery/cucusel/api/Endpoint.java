package com.brainhatchery.cucusel.api;

import io.restassured.config.RestAssuredConfig;

public class Endpoint extends CucuselBaseEndpoint<Endpoint> {
    public Endpoint(String baseUri, String basePath, RestAssuredConfig config) {
        super(baseUri, basePath, config);
    }

    public Endpoint(String baseUri, String basePath) {
        super(baseUri, basePath);
    }
}
