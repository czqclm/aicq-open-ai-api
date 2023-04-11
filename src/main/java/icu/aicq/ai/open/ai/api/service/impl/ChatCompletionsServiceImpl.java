package icu.aicq.ai.open.ai.api.service.impl;

import icu.aicq.ai.open.ai.api.common.OpenAIConstant;
import icu.aicq.ai.open.ai.api.config.OpenAIConfigStorage;
import icu.aicq.ai.open.ai.api.exception.AicqException;
import icu.aicq.ai.open.ai.api.exception.OpenAIStreamClosedUnexpectedlyException;
import icu.aicq.ai.open.ai.api.pojo.req.ChatCompletionRequest;
import icu.aicq.ai.open.ai.api.pojo.rsp.ChatCompletionResponse;
import icu.aicq.ai.open.ai.api.service.ChatCompletionsService;
import icu.aicq.ai.open.ai.api.utils.HandleOpenAIStreamResponseUtils;
import icu.aicq.ai.open.ai.api.utils.OkHttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author zhiqi
 * @date 2023-03-19
 */
@Slf4j
public class ChatCompletionsServiceImpl extends OpenAIServiceImpl implements ChatCompletionsService {
    @Override
    public ChatCompletionResponse chatCompletions(ChatCompletionRequest chatCompletionRequest) {
        OkHttpClientUtils okHttpClient = this.getOpenAIConfigStorage().getOkHttpClient();

        Map<String, String> headerMap = new HashMap<>(8);
        headerMap.put("Authorization", "Bearer " + openAIConfigStorage.getApiKey());
        return okHttpClient.postJson(this.openAIConfigStorage.getApiUrl(OpenAIConstant.CHAT_COMPLETIONS_API_PATH), chatCompletionRequest, headerMap, ChatCompletionResponse.class);
    }


    @Override
    public void chatCompletionsStream(ChatCompletionRequest request, BiFunction<String, AicqException, Boolean> streamResponse) {
        OkHttpClientUtils okHttpClientUtils = this.getOpenAIConfigStorage().getOkHttpClient();

        Map<String, String> headerMap = new HashMap<>(8);
        headerMap.put("Authorization", "Bearer " + openAIConfigStorage.getApiKey());
        request.setStream(true);

        okHttpClientUtils.postStream(this.openAIConfigStorage.getApiUrl(OpenAIConstant.CHAT_COMPLETIONS_API_PATH), request, headerMap, streamResponse);
    }

    public ChatCompletionsServiceImpl(OpenAIConfigStorage openAIConfigStorage) {
        super(openAIConfigStorage);
    }


    @Override
    public Flux<String> handleStream2SSEResponse(ChatCompletionRequest request) {
        return handleStream2SSEResponse(request, null);
    }

    @Override
    public Flux<String> handleStream2SSEResponse(ChatCompletionRequest request, BiConsumer<CopyOnWriteArrayList<String>, AicqException> finalResult) {
        // 设置 stream 传输
        request.setStream(true);

        // 记录每次一响应
        CopyOnWriteArrayList<String> lineList = new CopyOnWriteArrayList<>();
        return Flux.create(emitter -> {
            chatCompletionsStream(request, (line, aicqHttpException) -> {
                log.trace("" +
                        "--------------------------------\n" +
                        "request = {}\n" +
                        "line = {}\n" +
                        "aicqHttpException = {}\n" +
                        "--------------------------------", request, line, aicqHttpException);
                try {
                    // 响应发生异常
                    if (Objects.nonNull(aicqHttpException)) {
                        emitter.error(aicqHttpException);
                        finalResult.accept(lineList, aicqHttpException);
                        return true;
                    }
                    // 如果 openAI 没有主动终止响应, 持续进行监听
                    if (line.contains(OpenAIConstant.CHAT_COMPLETIONS_FINISH_FIELD)) {
                        lineList.add(line);
                        if (line.contains(OpenAIConstant.CHAT_COMPLETIONS_UNFINISHED_MARK)) {
                            HandleOpenAIStreamResponseUtils.streamLine2CleanContent(line, emitter::next);
                            return false;
                        } else {
                            finalResult.accept(lineList, null);
                            HandleOpenAIStreamResponseUtils.streamLine2CleanContent(line, emitter::next);
                            emitter.complete();
                            return true;
                        }
                    }
                    return false;
                } catch (Exception e) {
                    emitter.error(e);
                    emitter.complete();
                    finalResult.accept(lineList, new OpenAIStreamClosedUnexpectedlyException(e.getMessage(), e));
                    return true;
                }
            });
        });
    }
}
