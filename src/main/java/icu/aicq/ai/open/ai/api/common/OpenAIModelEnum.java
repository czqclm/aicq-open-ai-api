package icu.aicq.ai.open.ai.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhiqi
 * @date 2023-03-18
 */
@Getter
@AllArgsConstructor
public enum OpenAIModelEnum {

    /**
     * chatGPT
     */
    GPT_3_5_TURBO("gpt-3.5-turbo");

    private final String code;
}
