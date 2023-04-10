package icu.aicq.ai.open.ai.api.service;

import icu.aicq.ai.open.ai.api.exception.AicqHttpException;
import icu.aicq.ai.open.ai.api.pojo.req.ChatCompletionRequest;
import icu.aicq.ai.open.ai.api.pojo.rsp.ChatCompletionResponse;

import java.util.function.BiFunction;

/**
 * @author zhiqi
 * @date 2023-03-19
 */
public interface ChatCompletionsService {

    /**
     * 聊天基于 GPT 3.5
     *
     * @param request 请求
     * @return ChatCompletionResponse
     */
    ChatCompletionResponse chatCompletions(ChatCompletionRequest request);

    /**
     * 聊天基于 GPT 3.5 stream
     *
     * @param request        请求
     * @param streamResponse 函数, 处理每一条的响应
     */
    void chatCompletionsStream(ChatCompletionRequest request, BiFunction<String, AicqHttpException, Boolean> streamResponse);
}
