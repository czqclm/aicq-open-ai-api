package icu.aicq.ai.open.ai.api.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import icu.aicq.ai.open.ai.api.exception.AicqException;
import icu.aicq.ai.open.ai.api.exception.OpenAINoRouteToHostException;
import icu.aicq.ai.open.ai.api.exception.OpenAIResourceException;
import icu.aicq.ai.open.ai.api.exception.OpenAIStreamClosedUnexpectedlyException;
import icu.aicq.ai.open.ai.api.exception.OpenAIStreamEmptyException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static icu.aicq.ai.open.ai.api.common.OpenAIConstant.STREAM_DATA_STARTS_STR_PREFIX;

/**
 * @author zhiqi
 * @date 2023-03-19
 */
@Slf4j
public class OkHttpClientUtils {
    private final OkHttpClient client;

    public OkHttpClientUtils() {
        this.client = new OkHttpClient();
    }

    public OkHttpClientUtils(OkHttpClient client) {
        this.client = client;
    }

    public OkHttpClientUtils(Proxy.Type proxyType, String proxyHost, int proxyPort) {
        Proxy proxy = new Proxy(proxyType, new InetSocketAddress(proxyHost, proxyPort));
        this.client = new OkHttpClient.Builder()
                .proxy(proxy)
                .build();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public <R> R get(String url, Map<String, String> queryParams, Map<String, String> headerMap, Class<R> clazz) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        Optional.ofNullable(queryParams).ifPresent(params -> queryParams.forEach(urlBuilder::addQueryParameter));
        String finalUrl = urlBuilder.build().toString();

        Request.Builder builder = new Request.Builder().url(finalUrl);
        Optional.ofNullable(headerMap).ifPresent(map -> map.forEach(builder::addHeader));
        return handlerResponse(builder.build(), clazz);
    }

    public <R, D> R get(String url, D data, Class<R> clazz) {
        return get(url, data, null, clazz);
    }

    public <R, D> R get(String url, D data, Map<String, String> headerMap, Class<R> clazz) {
        if (Objects.isNull(data)) {
            return get(url, null, headerMap, clazz);
        }
        Map<String, String> queryParams = new HashMap<>(8);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(data);
        for (String key : jsonObject.keySet()) {
            queryParams.put(key, jsonObject.getString(key));
        }
        return get(url, queryParams, headerMap, clazz);
    }

    public <R> R post(String url, RequestBody body, Map<String, String> headerMap, Class<R> clazz) {
        Request.Builder builder = new Request.Builder().url(url).post(body);
        Optional.ofNullable(headerMap).ifPresent(map -> map.forEach(builder::addHeader));
        return handlerResponse(builder.build(), clazz);
    }

    // todo 优化超时请求
    public <D> void postStream(String url, D body, Map<String, String> headerMap, BiFunction<String, AicqException, Boolean> streamResponse) {
        RequestBody requestBody = generateRequestBody(body);
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        Optional.ofNullable(headerMap).ifPresent(map -> map.forEach(builder::addHeader));
        Request request = builder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                streamResponse.apply(null, new OpenAIStreamClosedUnexpectedlyException(e));
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    successFullCheck(response, request, errorResponse -> {
                        OpenAIResourceException resourceException = new OpenAIResourceException(errorResponse.code(), errorResponse, "Unexpected code");
                        streamResponse.apply(null, resourceException);
                        return resourceException;
                    });
                    if (Objects.isNull(responseBody)) {
                        streamResponse.apply(null, new OpenAIStreamEmptyException("Response body is null."));
                    }
                    BufferedSource source = Objects.requireNonNull(responseBody).source();
                    boolean endFlag = false;
                    while (!source.exhausted() && !endFlag) {
                        // 按行读取源中的数据
                        String line = source.readUtf8Line();
                        log.debug("Response line : {}", line);
                        if (StringUtils.isNotBlank(line)) {
                            if (line.startsWith(STREAM_DATA_STARTS_STR_PREFIX)) {
                                line = line.substring(STREAM_DATA_STARTS_STR_PREFIX.length());
                            }
                            endFlag = streamResponse.apply(line, null);
                        }
                    }
                    call.cancel();
                } catch (IOException e) {
                    streamResponse.apply(null, new OpenAIStreamClosedUnexpectedlyException());
                }
            }
        });
    }

    private static <E> void successFullCheck(@NonNull Response response, Request request, Function<Response, RuntimeException> exceptionSupplier) {
        log.debug("HTTP Request: {} {}{}", request.method(), request.url(), request.body() != null ? " " + request.body() : "");
        if (request.headers().size() > 0) {
            log.debug("Request Headers:");
            for (String name : request.headers().names()) {
                log.debug("{}: {}", name, request.headers().get(name));
            }
        }
        if (!response.isSuccessful()) {
            log.error("HTTP Response: Unexpected code {}", response.code());
            throw exceptionSupplier.apply(response);
        }
    }

    private <R> R handlerResponse(Request request, Class<R> clazz) {
        try (Response response = client.newCall(request).execute()) {
            successFullCheck(response, request, errorResponse -> new OpenAIResourceException(errorResponse.code(), errorResponse, "Unexpected code"));
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                String jsonString = responseBody.string();
                log.debug("HTTP Response Body: {}", jsonString);
                return JSON.parseObject(jsonString, clazz);
            }
            return null;
        } catch (NoRouteToHostException e) {
            log.error("HTTP Request Failed. Please set up the proxy!", e);
            throw new OpenAINoRouteToHostException("HTTP Request Failed. Please set up the proxy!", e);
        } catch (IOException e) {
            log.error("HTTP Request Failed!", e);
            throw new OpenAIStreamEmptyException("HTTP Request Failed!", e);
        }
    }

    public <R, D> R postJson(String url, D body, Map<String, String> headerMap, Class<R> clazz) {
        RequestBody requestBody = generateRequestBody(body);
        return post(url, requestBody, headerMap, clazz);
    }

    private static <D> RequestBody generateRequestBody(D body) {
        String requestJson;
        if (body instanceof String) {
            requestJson = (String) body;
        } else {
            requestJson = JSON.toJSONString(body);
        }
        return RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));
    }

}
