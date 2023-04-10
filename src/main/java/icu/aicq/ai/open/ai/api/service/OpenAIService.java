package icu.aicq.ai.open.ai.api.service;

import icu.aicq.ai.open.ai.api.config.OpenAIConfigStorage;

/**
 * @author zhiqi
 * @date 2023-03-19
 */
public interface OpenAIService {

    OpenAIConfigStorage getOpenAIConfigStorage();

    void setOpenAIConfigStorage(OpenAIConfigStorage openAIConfigStorage);

    String getOpenAIApiKey();

}
