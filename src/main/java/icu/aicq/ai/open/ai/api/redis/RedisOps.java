package icu.aicq.ai.open.ai.api.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author zhiqi
 * @since 2023-03-19
 */
public interface RedisOps {

    /**
     * 获取一个 value
     *
     * @param key 键
     * @return value
     */
    String getValue(String key);

    void setValue(String key, String value);

    /**
     * 设置 value
     *
     * @param key      键
     * @param value    值
     * @param expire   过期时间
     * @param timeUnit 时间单位
     */
    void setValue(String key, String value, int expire, TimeUnit timeUnit);

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间
     */
    Long getExpire(String key);

    /**
     * 设置过期时间
     *
     * @param key      键
     * @param expire   过期时间
     * @param timeUnit 时间单位
     */
    void expire(String key, int expire, TimeUnit timeUnit);

    /**
     * 获取锁
     *
     * @param key 键
     * @return lock
     */
    Lock getLock(String key);
}
