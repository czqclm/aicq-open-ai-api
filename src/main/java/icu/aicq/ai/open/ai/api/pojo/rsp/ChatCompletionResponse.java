package icu.aicq.ai.open.ai.api.pojo.rsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import icu.aicq.ai.open.ai.api.common.OpenAIFinishReasonEnum;
import icu.aicq.ai.open.ai.api.common.OpenAIModelEnum;
import icu.aicq.ai.open.ai.api.pojo.dto.MessageDTO;
import icu.aicq.ai.open.ai.api.pojo.dto.OpenAIUsageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author zhiqi
 * @date 2023-03-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatCompletionResponse {

    private String id;

    /**
     * 对象
     */
    private String object;

    /**
     * 发起时间
     */
    private Long created;

    /**
     * 模型类型
     *
     * @see OpenAIModelEnum
     */
    private String model;

    /**
     * 结果集
     */
    private List<Choice> choices;

    /**
     * 使用量
     */
    private OpenAIUsageDTO usage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Choice {

        private Delta delta;

        private Integer index;

        /**
         * 消息
         */
        private MessageDTO message;

        /**
         * 响应终止的原因
         *
         * @see OpenAIFinishReasonEnum
         */
        @JsonProperty("finish_reason")
        private String finishReason;

        private String text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Delta {
        private String content;
    }

    public String getOnlyOneAnswer() {
        if (Objects.nonNull(this.getChoices()) && !this.getChoices().isEmpty()) {
            return this.getChoices().get(this.getChoices().size() - 1).getMessage().getContent();
        }
        return null;
    }
}
