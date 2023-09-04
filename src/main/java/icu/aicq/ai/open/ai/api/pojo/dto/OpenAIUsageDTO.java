package icu.aicq.ai.open.ai.api.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhiqi
 * @since 2023-04-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenAIUsageDTO {

    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    @JsonProperty("total_tokens")
    private Integer totalTokens;
}
