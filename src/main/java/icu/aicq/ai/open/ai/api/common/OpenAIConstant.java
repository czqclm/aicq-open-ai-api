package icu.aicq.ai.open.ai.api.common;

/**
 * openAI 相关常量
 *
 * @author zhiqi
 * @date 2023-03-19
 */
public interface OpenAIConstant {

    String DEFAULT_BASE_URL = "https://api.openai.com";

    String CHAT_COMPLETIONS_API_PATH = "/v1/chat/completions";

    String CHAT_COMPLETIONS_FINISH_FIELD = "\"finish_reason\"";

    String CHAT_COMPLETIONS_UNFINISHED_MARK = "\"finish_reason\":null";

    String STREAM_DATA_STARTS_STR_PREFIX = "data: ";
}
