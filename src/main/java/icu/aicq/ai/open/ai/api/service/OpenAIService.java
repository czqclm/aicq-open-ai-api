package icu.aicq.ai.open.ai.api.service;

import icu.aicq.ai.open.ai.api.config.OpenAIConfigStorage;

/**
 * @author zhiqi
 * @since 2023-03-19
 */
public interface OpenAIService {

    OpenAIConfigStorage getOpenAIConfigStorage();

    String getOpenAIApiKey();

}
