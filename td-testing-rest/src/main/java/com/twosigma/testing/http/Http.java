package com.twosigma.testing.http;

import com.google.gson.*;
import com.twosigma.testing.data.traceable.TraceableValue;
import com.twosigma.testing.http.config.HttpConfigurations;
import com.twosigma.testing.http.datanode.DataNode;
import com.twosigma.testing.http.datanode.DataNodeBuilder;
import com.twosigma.testing.http.datanode.DataNodeId;
import com.twosigma.testing.http.datanode.StructuredDataNode;
import com.twosigma.testing.http.render.DataNodeAnsiPrinter;
import com.twosigma.testing.reporter.StepReportOptions;
import com.twosigma.testing.reporter.TestStep;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.twosigma.testing.reporter.IntegrationTestsMessageBuilder.action;
import static com.twosigma.testing.reporter.IntegrationTestsMessageBuilder.urlValue;
import static com.twosigma.testing.reporter.TokenizedMessage.tokenizedMessage;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author mykola
 */
public class Http {
    private static final HttpResponseValidator EMPTY = (header, body) -> {
    };

    private static final Gson gson = createGson();

    public static final Http http = new Http();
    public final HttpDocumentation doc = new HttpDocumentation();

    private ThreadLocal<HttpValidationResult> lastValidationResult = new ThreadLocal<>();

    private Http() {
    }

    public <E> E get(String url, HttpResponseValidatorWithReturn validator) {
        return executeAndValidateHttpCall("GET", url,
                this::getToFullUrl,
                HttpRequestHeader.EMPTY, null, validator);
    }

    public void get(String url, HttpResponseValidator validator) {
        get(url, new HttpResponseValidatorIgnoringReturn(validator));
    }

    public <E> E get(String url, HttpQueryParams queryParams, HttpResponseValidatorWithReturn validator) {
        return get(url + "?" + queryParams.toString(), validator);
    }

    public void get(String url, HttpQueryParams queryParams, HttpResponseValidator validator) {
        get(url, queryParams, new HttpResponseValidatorIgnoringReturn(validator));
    }

    public <E> E post(String url, HttpRequestBody requestBody, HttpResponseValidatorWithReturn validator) {
        return executeAndValidateHttpCall("POST", url,
                (fullUrl, fullHeader) -> postToFullUrl(fullUrl, fullHeader, requestBody),
                HttpRequestHeader.EMPTY,
                requestBody,
                validator);
    }

    public void post(String url, HttpRequestBody requestBody, HttpResponseValidator validator) {
        post(url, requestBody, new HttpResponseValidatorIgnoringReturn(validator));
    }

    public void post(String url, HttpRequestBody requestBody) {
        post(url, requestBody, EMPTY);
    }

    public <E> E put(String url, HttpRequestBody requestBody, HttpResponseValidatorWithReturn validator) {
        return executeAndValidateHttpCall("PUT", url,
                (fullUrl, fullHeader) -> putToFullUrl(fullUrl, fullHeader, requestBody),
                HttpRequestHeader.EMPTY,
                requestBody,
                validator);
    }

    public void put(String url, HttpRequestBody requestBody, HttpResponseValidator validator) {
        put(url, requestBody, new HttpResponseValidatorIgnoringReturn(validator));
    }

    public <E> E delete(String url, HttpResponseValidatorWithReturn validator) {
        return executeAndValidateHttpCall("DELETE", url,
                this::deleteToFullUrl,
                HttpRequestHeader.EMPTY,
                null,
                validator);
    }

    public void delete(String url, HttpResponseValidator validator) {
        delete(url, new HttpResponseValidatorIgnoringReturn(validator));
    }

    public void delete(String url) {
        delete(url, EMPTY);
    }

    public HttpValidationResult getLastValidationResult() {
        return lastValidationResult.get();
    }

    public HttpResponse getToFullUrl(String fullUrl, HttpRequestHeader requestHeader) {
        return requestWithoutBody("GET", fullUrl, requestHeader);
    }

    public HttpResponse deleteToFullUrl(String fullUrl, HttpRequestHeader requestHeader) {
        return requestWithoutBody("DELETE", fullUrl, requestHeader);
    }

    public HttpResponse postToFullUrl(String fullUrl, HttpRequestHeader requestHeader, HttpRequestBody requestBody) {
        return requestWithBody("POST", fullUrl, requestHeader, requestBody);
    }

    public HttpResponse putToFullUrl(String fullUrl, HttpRequestHeader requestHeader, HttpRequestBody requestBody) {
        return requestWithBody("PUT", fullUrl, requestHeader, requestBody);
    }

    @SuppressWarnings("unchecked")
    private <E> E executeAndValidateHttpCall(String requestMethod, String url, HttpCall httpCall,
                                             HttpRequestHeader requestHeader,
                                             HttpRequestBody requestBody,
                                             HttpResponseValidatorWithReturn validator) {
        String fullUrl = HttpConfigurations.fullUrl(url);
        HttpRequestHeader fullHeader = HttpConfigurations.fullHeader(requestHeader);

        Object[] result = new Object[1];

        Runnable httpCallRunnable = () -> {
            try {
                HttpResponse response = httpCall.execute(fullUrl, fullHeader);
                result[0] = validateAndRecord(requestMethod, url, fullUrl, validator, requestBody, response);
            } catch (Exception e) {
                throw new RuntimeException("error during http." + requestMethod.toLowerCase() + "(" + fullUrl + ")", e);
            }
        };

        TestStep<E> step = TestStep.create(null, tokenizedMessage(action("executing HTTP " + requestMethod), urlValue(fullUrl)),
                () -> tokenizedMessage(action("executed HTTP " + requestMethod), urlValue(fullUrl)),
                httpCallRunnable);

        try {
            step.execute(StepReportOptions.REPORT_ALL);
        } finally {
            step.addPayload(lastValidationResult.get());
        }

        return (E) result[0];
    }

    @SuppressWarnings("unchecked")
    private <E> E validateAndRecord(String requestMethod, String url, String fullUrl,
                                    HttpResponseValidatorWithReturn validator,
                                    HttpRequestBody requestBody, HttpResponse response) {
        HeaderDataNode header = createHeaderDataNode(response);
        DataNode body = createBodyDataNode(response);

        HttpValidationResult result = new HttpValidationResult(requestMethod, url, fullUrl,
                requestBody, response, header, body);
        Object returnedValue = validator.validate(header, body);

        lastValidationResult.set(result);
        render(result);

        return (E) extractOriginalValue(returnedValue);
    }

    private void render(HttpValidationResult result) {
        new DataNodeAnsiPrinter().print(result.getBody());
    }

    private HttpResponse requestWithoutBody(String method, String fullUrl, HttpRequestHeader requestHeader) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            requestHeader.forEachProperty(connection::setRequestProperty);

            connection.connect();

            return extractHttpResponse(connection);
        } catch (IOException e) {
            throw new RuntimeException("couldn't " + method + ": " + fullUrl, e);
        }
    }

    private HttpResponse requestWithBody(String method, String fullUrl,
                                         HttpRequestHeader requestHeader,
                                         HttpRequestBody requestBody) {
        if (requestBody.isBinary()) {
            throw new UnsupportedOperationException("binary is not supported yet");
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", requestBody.type());
            connection.setRequestProperty("Accept", requestBody.type());
            requestHeader.forEachProperty(connection::setRequestProperty);
            connection.setDoOutput(true);

            IOUtils.write(requestBody.asString(), connection.getOutputStream(), UTF_8);

            return extractHttpResponse(connection);
        } catch (IOException e) {
            throw new RuntimeException("couldn't " + method + ": " + fullUrl, e);
        }
    }

    private HttpResponse extractHttpResponse(HttpURLConnection connection) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        InputStream inputStream = connection.getResponseCode() < 400 ? connection.getInputStream() : connection.getErrorStream();
        httpResponse.setStatusCode(connection.getResponseCode());
        httpResponse.setContent(inputStream != null ? IOUtils.toString(inputStream, StandardCharsets.UTF_8) : "");
        httpResponse.setContentType(connection.getContentType() != null ? connection.getContentType() : "");

        return httpResponse;
    }

    private static HeaderDataNode createHeaderDataNode(HttpResponse response) {
        Map<String, Object> headerData = new LinkedHashMap<>();
        headerData.put("statusCode", response.getStatusCode());
        headerData.put("contentType", response.getContentType());

        return new HeaderDataNode(DataNodeBuilder.fromMap(new DataNodeId("header"), headerData));
    }

    @SuppressWarnings("unchecked")
    private static DataNode createBodyDataNode(HttpResponse response) {
        try {
            DataNodeId id = new DataNodeId("body");
            if (response.getContent().isEmpty()) {
                return new StructuredDataNode(id, new TraceableValue(""));
            }

            if (!response.getContentType().contains("/json")) {
                return new StructuredDataNode(id, new TraceableValue(response.getContent()));
            }

            MapOrList mapOrList = gson.fromJson(response.getContent(), MapOrList.class);

            return mapOrList.list != null ?
                    DataNodeBuilder.fromList(id, mapOrList.list) :
                    DataNodeBuilder.fromMap(id, mapOrList.map);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("error parsing body: " + response.getContent(), e);
        }
    }

    /**
     * Response consist of DataNode and Traceable values but we need to return back a simple value that can be used for
     * regular calculations and to drive test flow
     *
     * @param v value returned from a validation callback
     * @return extracted regular value
     */
    private Object extractOriginalValue(Object v) {
        // TODO handle maps and list inside
        if (v instanceof DataNode) {
            return ((DataNode) v).get().getValue();
        }

        if (v instanceof TraceableValue) {
            return ((TraceableValue) v).getValue();
        }

        return v;
    }

    private interface HttpCall {
        HttpResponse execute(String fullUrl, HttpRequestHeader fullHeader);
    }

    private static Gson createGson() {
        return new GsonBuilder().registerTypeAdapter(MapOrList.class, new MapOrListDeserializer()).create();
    }

    private static class MapOrList {
        private Map map;
        private List list;
    }

    private static class MapOrListDeserializer implements JsonDeserializer<MapOrList> {
        @Override
        public MapOrList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            MapOrList result = new MapOrList();
            if (jsonElement.isJsonArray()) {
                result.list = jsonDeserializationContext.deserialize(jsonElement, List.class);
            } else {
                result.map = jsonDeserializationContext.deserialize(jsonElement, Map.class);
            }

            return result;
        }
    }
}
