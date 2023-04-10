package icu.aicq.ai.open.ai.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhiqi
 * @date 2023-03-18
 */
@Getter
@AllArgsConstructor
public enum OpenAIRoleEnum {

    /**
     * 系统
     */
    SYSTEM("system"),

    /**
     * 用户
     */
    USER("user"),

    /**
     * 助手
     */
    ASSISTANT("assistant");

    private final String code;
}
