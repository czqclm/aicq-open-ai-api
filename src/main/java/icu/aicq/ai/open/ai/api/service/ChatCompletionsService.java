package icu.aicq.ai.open.ai.api.service;

import icu.aicq.ai.open.ai.api.exception.AicqException;
import icu.aicq.ai.open.ai.api.pojo.req.ChatCompletionRequest;
import icu.aicq.ai.open.ai.api.pojo.rsp.ChatCompletionResponse;
import reactor.core.publisher.Flux;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
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
     * @param streamResponse 函数, 处理每一条的响应, 如果 streamResponse return true 则会终止监听
     */
    void chatCompletionsStream(ChatCompletionRequest request, BiFunction<String, AicqException, Boolean> streamResponse);

    /**
     * 处理 stream 响应到 SSE 的响应
     *
     * @param request 请求
     * @return Flux<String>
     */
    Flux<String> handleStream2SSEResponse(ChatCompletionRequest request);

    /**
     * 处理 stream 响应到 SSE 的响应
     *
     * @param request     请求
     * @param finalResult 最终组装好的结果
     *                    每个 line 数据 CopyOnWriteArrayList<String>
     *                    响应中发生的异常 AicqHttpException
     * @return Flux<String>
     */
    Flux<String> handleStream2SSEResponse(ChatCompletionRequest request, BiConsumer<CopyOnWriteArrayList<String>, AicqException> finalResult);
}
