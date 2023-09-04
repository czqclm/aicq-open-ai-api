package icu.aicq.ai.open.ai.api.service.impl;

import icu.aicq.ai.open.ai.api.config.OpenAIConfigStorage;
import icu.aicq.ai.open.ai.api.service.ChatCompletionsService;
import icu.aicq.ai.open.ai.api.service.OpenAIService;

/**
 * @author zhiqi
 * @since 2023-03-19
 */
public class OpenAIServiceImpl implements OpenAIService {

    public OpenAIConfigStorage openAIConfigStorage;

    public OpenAIServiceImpl(OpenAIConfigStorage openAIConfigStorage) {
        this.openAIConfigStorage = openAIConfigStorage;
    }

    @Override
    public OpenAIConfigStorage getOpenAIConfigStorage() {
        return openAIConfigStorage;
    }

    @Override
    public String getOpenAIApiKey() {
        return openAIConfigStorage.getApiKey();
    }

    public ChatCompletionsServiceImpl getChatCompletionsService() {
        return new ChatCompletionsServiceImpl(this.openAIConfigStorage);
    }
}
