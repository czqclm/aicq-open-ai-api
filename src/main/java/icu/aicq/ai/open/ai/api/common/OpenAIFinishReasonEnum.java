package icu.aicq.ai.open.ai.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhiqi
 * @date 2023-03-18
 */
@Getter
@AllArgsConstructor
public enum OpenAIFinishReasonEnum {

    /**
     * API 返回完整的模型输出
     */
    STOP("stop"),

    /**
     * 由于参数或令牌限制max_tokens模型输出不完整
     */
    LENGTH("length"),

    /**
     * 由于内容过滤器中的标志而遗漏了内容
     */
    CONTENT_FILTER("content_filter"),

    /**
     * API 响应仍在进行中或不完整
     */
    NULL("null"),
    ;

    private final String code;

}
