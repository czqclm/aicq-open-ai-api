package icu.aicq.ai.open.ai.api.config;

import icu.aicq.ai.open.ai.api.common.OpenAIConstant;
import icu.aicq.ai.open.ai.api.utils.OkHttpClientUtils;

/**
 * openAI 配置
 *
 * @author zhiqi
 * @date 2023-03-19
 */
public interface OpenAIConfigStorage {

    /**
     * 设置 baseApiUrl
     *
     * @param baseApiUrl baseApiUrl
     * @see OpenAIConstant#DEFAULT_BASE_URL
     */
    void setBaseApiUrl(String baseApiUrl);

    /**
     * 获取完整 url
     *
     * @param path 路径
     * @return 完整 url
     */
    String getApiUrl(String path);

    /**
     * 获取 apiKey
     *
     * @return apiKey
     */
    String getApiKey();

    /**
     * openAI 颁发的 apiKey 对应的 id
     *
     * @return openAIApiKeyId
     */
    String getOpenAIApiKeyId();

    /**
     * 获取 okHttpClient
     *
     * @return okHttpClient
     */
    OkHttpClientUtils getOkHttpClient();


}
