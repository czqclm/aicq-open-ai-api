package icu.aicq.ai.open.ai.api.pojo.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import icu.aicq.ai.open.ai.api.common.OpenAIModelEnum;
import icu.aicq.ai.open.ai.api.pojo.dto.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @author zhiqi
 * @since 2023-03-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatCompletionRequest {

    /**
     * 模型
     *
     * @see OpenAIModelEnum
     */
    @NonNull
    private String model;

    /**
     * 消息
     */
    @NonNull
    private List<MessageDTO> messages;

    /**
     * 采样温度
     * 范围 0-2
     * 较高的值（如 0.8）将使输出更加随机
     * 而较低的值（如 0.2）将使其更加集中和确定
     */
    private Float temperature;

    /**
     * 核心采样
     * 0.1 意味着只考虑包含前 10% 概率质量的 token
     */
    @JsonProperty("top_p")
    private Float topP;

    /**
     * 每次聊天生成的条数
     */
    private Integer n;

    /**
     * 流式传输
     */
    private Boolean stream;

    /**
     * 最大令牌数
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 正反馈
     * 范围 [-2, 2]
     * 正值会根据新标记到目前为止是否出现在文本中来惩罚它们,
     * 从而增加模型讨论新主题的可能性。
     */
    @JsonProperty("presence_penalty")
    private Float presencePenalty;

    /**
     * 负反馈
     * 范围 [-2, 2]
     * 正值会根据新标记到目前为止在文本中的现有频率来惩罚新标记
     * 从而降低模型逐字重复同一行的可能性。
     */
    @JsonProperty("frequency_penalty")
    private Float frequencyPenalty;

    /**
     * 对数偏差
     */
    @JsonProperty("logit_bias")
    private Map<String, String> logitBias;


    /**
     * 标记用户
     * 帮助 OpenAI 监控和检测滥用行为
     */
    private String user;


    public ChatCompletionRequest(@NonNull String model, @NonNull List<MessageDTO> messages) {
        this.model = model;
        this.messages = messages;
    }
}
