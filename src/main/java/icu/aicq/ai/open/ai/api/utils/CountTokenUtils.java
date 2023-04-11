package icu.aicq.ai.open.ai.api.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import icu.aicq.ai.open.ai.api.pojo.dto.OpenAIUsageDTO;
import icu.aicq.ai.open.ai.api.pojo.req.ChatCompletionRequest;
import icu.aicq.ai.open.ai.api.pojo.rsp.ChatCompletionResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhiqi
 * @date 2023-04-10
 */
public class CountTokenUtils {
    private final static EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    /**
     * 计算
     * ChatCompletionRequest#messages + ChatCompletionResponse#choices#message <br/>
     * 会根据请求的 model 切换计算方式, 默认为 gpt-3.5-turbo <br/>
     * 如果你当前的结果为 stream 建议使用 {@link ChatStreamResultResolver#convertStreamData2ChatCompletionResponse} 进行转换
     *
     * @param request  请求
     * @param response 响应
     * @return 计算结果
     * @throws UnsupportedOperationException if the text contains special tokens which are not supported for now
     */
    public static OpenAIUsageDTO countTokensByRequestAndResponse(ChatCompletionRequest request, ChatCompletionResponse response) {
        Encoding secondEnc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);
        int promptTokens = Optional.ofNullable(request)
                .map(ChatCompletionRequest::getMessages)
                .orElse(Collections.emptyList())
                .stream()
                .mapToInt(message -> secondEnc.countTokensOrdinary(message.getContent()))
                .sum();

        int completionTokens = Optional.ofNullable(response)
                .map(ChatCompletionResponse::getChoices)
                .orElse(Collections.emptyList())
                .stream()
                .filter(choice -> Objects.nonNull(choice.getMessage()) && StringUtils.isNotBlank(choice.getMessage().getContent()))
                .mapToInt(choice -> secondEnc.countTokensOrdinary(choice.getMessage().getContent()))
                .sum();

        OpenAIUsageDTO usageDTO = OpenAIUsageDTO.builder().promptTokens(promptTokens).completionTokens(completionTokens).totalTokens(promptTokens + completionTokens).build();
        // 写入响应
        Optional.ofNullable(response).ifPresent(e -> e.setUsage(usageDTO));
        return usageDTO;
    }

}
