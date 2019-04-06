package com.gedo.server.repository.redis;

import org.springframework.stereotype.Repository;
import redis.clients.jedis.BinaryJedisPubSub;

import java.util.List;
import java.util.Set;

/**
 * Created by Gedo on 2019/4/4.
 */
@Repository
public interface RedisClient {
    void set(String K, String V);

    String get(String K);

    void sadd(String key, String member);

    void srem(String key, String member);

    Set<String> smembers(String key);

    void lpush(byte[] key, byte[] strings);

    List<byte[]> brpop(int timeout, byte[]... keys);

    void subscribe(BinaryJedisPubSub jedisPubSub, byte[] channels);

    void publish(byte[] channel, byte[] message);

    void incrBy(String key, long number);

    void expire(String srckey, int time);

    String hget(String mapName, String key);
}
