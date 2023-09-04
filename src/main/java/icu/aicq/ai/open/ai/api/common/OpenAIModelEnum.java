package icu.aicq.ai.open.ai.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhiqi
 * @since 2023-03-18
 */
@Getter
@AllArgsConstructor
public enum OpenAIModelEnum {

    /**
     * chatGPT
     */
    GPT_3_5_TURBO("gpt-3.5-turbo"),

    GPT_4("gpt-4");

    private final String code;
}
