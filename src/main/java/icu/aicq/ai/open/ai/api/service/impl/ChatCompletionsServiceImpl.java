package icu.aicq.ai.open.ai.api.service.impl;

import icu.aicq.ai.open.ai.api.common.OpenAIConstant;
import icu.aicq.ai.open.ai.api.config.OpenAIConfigStorage;
import icu.aicq.ai.open.ai.api.exception.AicqHttpException;
import icu.aicq.ai.open.ai.api.pojo.req.ChatCompletionRequest;
import icu.aicq.ai.open.ai.api.pojo.rsp.ChatCompletionResponse;
import icu.aicq.ai.open.ai.api.service.ChatCompletionsService;
import icu.aicq.ai.open.ai.api.utils.OkHttpClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
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
    public void chatCompletionsStream(ChatCompletionRequest request, BiFunction<String, AicqHttpException, Boolean> streamResponse) {
        OkHttpClientUtils okHttpClientUtils = this.getOpenAIConfigStorage().getOkHttpClient();

        Map<String, String> headerMap = new HashMap<>(8);
        headerMap.put("Authorization", "Bearer " + openAIConfigStorage.getApiKey());
        request.setStream(true);

        okHttpClientUtils.postStream(this.openAIConfigStorage.getApiUrl(OpenAIConstant.CHAT_COMPLETIONS_API_PATH), request, headerMap, streamResponse);
    }

    public ChatCompletionsServiceImpl(OpenAIConfigStorage openAIConfigStorage) {
        super(openAIConfigStorage);
    }

}
