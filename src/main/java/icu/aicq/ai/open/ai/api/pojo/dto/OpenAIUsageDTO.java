package icu.aicq.ai.open.ai.api.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhiqi
 * @date 2023-04-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenAIUsageDTO {

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;
}
