package icu.aicq.ai.open.ai.api.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author zhiqi
 * @date 2023-03-19
 */
@RequiredArgsConstructor
public class RedissonRedisOps implements RedisOps {

    private final RedissonClient redissonClient;

    @Override
    public String getValue(String key) {
        Object value = redissonClient.getBucket(key).get();
        return value == null ? null : value.toString();
    }

    @Override
    public void setValue(String key, String value) {
        redissonClient.getBucket(key).set(value);
    }

    @Override
    public void setValue(String key, String value, int expire, TimeUnit timeUnit) {
        if (expire <= 0) {
            setValue(key, value);
        } else {
            redissonClient.getBucket(key).set(value, expire, timeUnit);
        }
    }

    @Override
    public Long getExpire(String key) {
        return redissonClient.getBucket(key).remainTimeToLive();
    }

    @Override
    public void expire(String key, int expire, TimeUnit timeUnit) {
        redissonClient.getBucket(key).expire(expire, timeUnit);
    }

    @Override
    public Lock getLock(String key) {
        return redissonClient.getLock(key);
    }
}
