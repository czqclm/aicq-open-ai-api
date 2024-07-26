package icu.aicq.ai.open.ai.api.config.impl;

import icu.aicq.ai.open.ai.api.common.OpenAIConstant;
import icu.aicq.ai.open.ai.api.config.OpenAIConfigStorage;
import icu.aicq.ai.open.ai.api.redis.RedisOps;
import icu.aicq.ai.open.ai.api.utils.OkHttpClientUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author zhiqi
 * @since 2023-03-19
 */
public class OpenAIRedisConfigStorageImpl implements OpenAIConfigStorage, Serializable {

    private static final long serialVersionUID = 123456789L;

    private final RedisOps redisOps;

    protected static final String OPEN_AI_API_KEY_REDIS_KEY = "open_ai_api_key:";

    protected static final String LOCK_REDIS_KEY = "open_ai_lock:";


    protected volatile String baseApiUrl;

    protected volatile String openAIApiKeyId;

    protected volatile OkHttpClientUtils okHttpClientUtils;


    private final String keyPrefix;

    protected volatile String lockRedisKey;

    protected volatile String apiKeyRedisKey;

    /**
     * 构造函数
     *
     * @param redisOps  redisOps
     * @param keyPrefix redis key 前缀
     */
    public OpenAIRedisConfigStorageImpl(RedisOps redisOps, String keyPrefix) {
        this.redisOps = redisOps;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void setBaseApiUrl(String baseApiUrl) {
        this.baseApiUrl = baseApiUrl;
    }

    @Override
    public String getApiUrl(String path) {
        if (StringUtils.isBlank(baseApiUrl)) {
            baseApiUrl = OpenAIConstant.DEFAULT_BASE_URL;
        }
        return baseApiUrl + path;
    }

    /**
     * 设置此 Storage 的 openAIApiKey
     *
     * @param openAIApiKeyId 唯一标识 (自建)
     * @param openApiKey     openAI 颁发的 openApiKey
     */
    public void setOpenAIApiKey(String openAIApiKeyId, String openApiKey) {
        this.openAIApiKeyId = openAIApiKeyId;
        lockRedisKey = getLockRedisKey();
        apiKeyRedisKey = getApiKeyRedisKey();
        redisOps.setValue(apiKeyRedisKey, openApiKey);
    }

    @Override
    public String getApiKey() {
        return redisOps.getValue(getApiKeyRedisKey());
    }

    public String getLockRedisKey() {
        String openAIApiKeyId = getOpenAIApiKeyId();
        String prefix = StringUtils.isNotBlank(keyPrefix) ?
                (StringUtils.endsWithIgnoreCase(keyPrefix, ":") ? keyPrefix : (keyPrefix + ":")) : "";
        return prefix + LOCK_REDIS_KEY.concat(openAIApiKeyId);
    }

    public String getApiKeyRedisKey() {
        String prefix = StringUtils.isNotBlank(keyPrefix) ?
                (StringUtils.endsWithIgnoreCase(keyPrefix, ":") ? keyPrefix : (keyPrefix + ":")) : "";
        return prefix + OPEN_AI_API_KEY_REDIS_KEY.concat(openAIApiKeyId);
    }

    @Override
    public String getOpenAIApiKeyId() {
        return openAIApiKeyId;
    }

    @Override
    public OkHttpClientUtils getOkHttpClient() {
        if (Objects.isNull(okHttpClientUtils)) {
            okHttpClientUtils = new OkHttpClientUtils();
            return okHttpClientUtils;
        }
        return okHttpClientUtils;
    }

    public void setOkHttpClient(OkHttpClientUtils okHttpClient) {
        this.okHttpClientUtils = okHttpClient;
    }
}
