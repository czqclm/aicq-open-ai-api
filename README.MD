[中文](README.MD) / [English](README.en-US.md)

# aicq-open-ai-api

> 在 java 中实现了 stream 调用 openAI 接口的能力,并且支持配置多个 apiKey

## TODO

- [X] 支持计算 tokens
- [ ] 支持其他接口
- [ ] 支持多 http client

## Demo

引入依赖

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

增加配置, `aicq-open-ai-api` 不会从你配置中读取数据,此处仅作为演示配置

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

    // 必须保障线程安全
    private static final Map<String, OpenAIServiceImpl> OPEN_AI_SERVICE_MAP = new ConcurrentHashMap<>(8);


    @PostConstruct
    public void initServices() {
        // 加载配置
        if (!CollectionUtils.isEmpty(openAIConfigForYaml.getApiKeys())) {
            for (String apiKey : openAIConfigForYaml.getApiKeys()) {
                OpenAIServiceImpl aiService = new OpenAIServiceImpl();

                OpenAIRedisConfigStorageImpl openAIRedisConfigStorage = new OpenAIRedisConfigStorageImpl(redissonClient, null);
                // 定义一个 apiKey 的 id, 方便找到对应的 apiKey
                openAIRedisConfigStorage.setOpenAIApiKey(RandomUtils.generateAlphaNumeric(4), apiKey);
                // 配置 httpClient, 如果部署在国内请加上 proxy
                openAIRedisConfigStorage.setOkHttpClient(new OkHttpClientUtils(Proxy.Type.SOCKS,
                        openAIConfigForYaml.getProxy().getSocks().getHost(), openAIConfigForYaml.getProxy().getSocks().getPort()));
                aiService.setOpenAIConfigStorage(openAIRedisConfigStorage);
                OPEN_AI_SERVICE_MAP.put(openAIRedisConfigStorage.getOpenAIApiKeyId(), aiService);
            }
        }
    }

    // 随机拿一个 apiKey
    public static OpenAIServiceImpl getRandomOpenAIService() {
        for (Map.Entry<String, OpenAIServiceImpl> stringOpenAIServiceEntry : OPEN_AI_SERVICE_MAP.entrySet()) {
            return stringOpenAIServiceEntry.getValue();
        }
        throw new AicqServerException(SERVER_RPC_BIZ_ERR).rewriteMsg("未找到任意 OpenAIService 对象, 请在调用前创建一个 OpenAIService 对象!");
    }
    // todo 轮序, 按照 apiKey id 获取, 和调用方绑定? 请自由发挥

}
```

调用

```java
public class ChatController {
    @PostMapping("/completions")
    public AicqResponse<ChatCompletionResponse> getOneCall(@RequestBody @Validated ChatCompletionRequest request) {
        // 这里随便拿一个
        OpenAIServiceImpl aiService = OpenAIConfiguration.getRandomOpenAIService();
        ChatCompletionResponse chatCompletionResponse = aiService.getChatCompletionsService().chatCompletions(request);
        log.info("Chat completion response: " + chatCompletionResponse);
        return AicqResponse.ok(chatCompletionResponse);
    }
}
```

stream 调用

```java
public class ChatController {
    @PostMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse(@RequestBody @Validated ChatCompletionRequest request) {
        request.setStream(true);
        ChatCompletionsServiceImpl chatCompletionsService = OpenAIConfiguration.getDefaultOpenAIService().getChatCompletionsService();
        return chatCompletionsService.handleStream2SSEResponse(request, (lineList, aicqException) -> {
            if (Objects.nonNull(aicqException)) {
                log.error("异常数据", aicqException);
                // 处理异常存储
            } else {
                log.trace("响应数据: {}", lineList);
                ChatCompletionResponse chatCompletionResponse = ChatStreamResultResolver.convertStreamData2ChatCompletionResponse(lineList);
                log.debug("转换的结果: {}", chatCompletionResponse);
                try {
                    CountTokenUtils.countTokensByRequestAndResponse(request, chatCompletionResponse);
                } catch (UnsupportedOperationException e) {
                    log.error("计算 token 失败", e);
                }
                // 处理存储
            }
        });
    }
}
```



