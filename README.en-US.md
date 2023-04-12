[中文](README.MD) / [English](README.en-US.md)

# aicq-open-ai-api

> The ability to call the OpenAI API using stream has been implemented in Java, and multiple apiKey configurations are
> supported.

## TODO

- [x] Support for calculating tokens
- [ ] Support for other APIs
- [ ] Support for multiple HTTP clients.

## Demo

Introducing Dependencies.

```xml

<dependencies>
    <dependency>
        <groupId>icu.aicq</groupId>
        <artifactId>aicq-open-ai-api</artifactId>
        <version>${aicq-open-ai-api.version}</version>
    </dependency>

    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.10.0</version>
    </dependency>
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib</artifactId>
        <version>1.6.20</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

Adding configuration, `aicq-open-ai-api` will not read data from your configuration and is only used for demonstration
purposes.

```yaml
open-ai:
  api-keys:
    - sk-xxxxxxxxxxxxxxxxxxxxxx
  proxy:
    socks:
      host: xxx.xxx.xxx
      port: xxxx
```

```java

@Configuration
@ConfigurationProperties(prefix = "open-ai")
@Data
public class OpenAIConfigForYaml {

    private List<String> apiKeys;

    private Proxy proxy;

    @Data
    public static class Proxy {
        private Socks socks;

        @Data
        public static class Socks {
            private String host;
            private Integer port;
        }
    }
}
```

```java

@Configuration
@Slf4j
@AllArgsConstructor
public class OpenAIConfiguration {

    private final RedissonClient redissonClient;

    private final OpenAIConfigForYaml openAIConfigForYaml;

    // Thread safety must be ensured.
    private static final Map<String, OpenAIServiceImpl> OPEN_AI_SERVICE_MAP = new ConcurrentHashMap<>(8);


    @PostConstruct
    public void initServices() {
        // Loading configuration
        if (!CollectionUtils.isEmpty(openAIConfigForYaml.getApiKeys())) {
            for (String apiKey : openAIConfigForYaml.getApiKeys()) {
                OpenAIServiceImpl aiService = new OpenAIServiceImpl();

                OpenAIRedisConfigStorageImpl openAIRedisConfigStorage = new OpenAIRedisConfigStorageImpl(redissonClient, null);
                // Defining an id for an apiKey to easily locate the corresponding apiKey.
                openAIRedisConfigStorage.setOpenAIApiKey(RandomUtils.generateAlphaNumeric(4), apiKey);
                // Configure the httpClient, if deployed in China, please add a proxy.
                openAIRedisConfigStorage.setOkHttpClient(new OkHttpClientUtils(Proxy.Type.SOCKS,
                        openAIConfigForYaml.getProxy().getSocks().getHost(), openAIConfigForYaml.getProxy().getSocks().getPort()));
                aiService.setOpenAIConfigStorage(openAIRedisConfigStorage);
                OPEN_AI_SERVICE_MAP.put(openAIRedisConfigStorage.getOpenAIApiKeyId(), aiService);
            }
        }
    }

    // Randomly select an apiKey.
    public static OpenAIServiceImpl getRandomOpenAIService() {
        for (Map.Entry<String, OpenAIServiceImpl> stringOpenAIServiceEntry : OPEN_AI_SERVICE_MAP.entrySet()) {
            return stringOpenAIServiceEntry.getValue();
        }
        throw new AicqServerException(SERVER_RPC_BIZ_ERR).rewriteMsg("No OpenAIService object found. Please create an OpenAIService object before calling it! ");
    }
    // TODO in turn, according to the apiKey id for retrieval, and bind with the caller? Please use your imagination freely

}
```

```java
public class ChatController {
    @PostMapping("/completions")
    public AicqResponse<ChatCompletionResponse> getOneCall(@RequestBody @Validated ChatCompletionRequest request) {
        // Randomly select an apiKey.
        OpenAIServiceImpl aiService = OpenAIConfiguration.getRandomOpenAIService();
        ChatCompletionResponse chatCompletionResponse = aiService.getChatCompletionsService().chatCompletions(request);
        log.info("Chat completion response: " + chatCompletionResponse);
        return AicqResponse.ok(chatCompletionResponse);
    }
}
```

```java
public class ChatController {
    @PostMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse(@RequestBody @Validated ChatCompletionRequest request) {
        request.setStream(true);
        ChatCompletionsServiceImpl chatCompletionsService = OpenAIConfiguration.getDefaultOpenAIService().getChatCompletionsService();
        return chatCompletionsService.handleStream2SSEResponse(request, (lineList, aicqException) -> {
            if (Objects.nonNull(aicqException)) {
                log.error("Exceptional data", aicqException);
                // Store the processing of exceptions
            } else {
                log.trace("response: {}", lineList);
                ChatCompletionResponse chatCompletionResponse = ChatStreamResultResolver.convertStreamData2ChatCompletionResponse(lineList);
                log.debug("The result of the conversion: {}", chatCompletionResponse);
                try {
                    CountTokenUtils.countTokensByRequestAndResponse(request, chatCompletionResponse);
                } catch (UnsupportedOperationException e) {
                    log.error("Token calculation failed", e);
                }
                // Processing and storage
            }
        });
    }
}
```