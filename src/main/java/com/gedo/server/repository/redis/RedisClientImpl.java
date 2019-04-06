package com.gedo.server.repository.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gedo on 2019/4/4.
 */
@Component("redisClientImpl")
public class RedisClientImpl implements RedisClient {

    private static final Logger log = LoggerFactory
            .getLogger(RedisClientImpl.class);

    private static volatile JedisPool jedisPool;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String pwd;

    public Jedis getJedis() {
        if (jedisPool == null) {
            synchronized (RedisClientImpl.class) {
                if (jedisPool == null) {
                    jedisPool = createJedisPool();
                }
            }
        }
        return jedisPool.getResource();
    }

    private JedisPool createJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(false);
        config.setMaxTotal(10000);
        config.setMaxIdle(100);
        config.setMinIdle(50);
        config.setMaxWaitMillis(1000 * 10);
        if (pwd == null || pwd.trim().equals("")) {
            return new JedisPool(config, host, port, 10000);
        } else {
            return new JedisPool(config, host, port, 10000, pwd);
        }
    }

    public void set(String K, String V) {
        Jedis jedis = getJedis();
        try {
            jedis.set(K, V);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String K) {
        Jedis jedis = getJedis();
        String value = "";
        try {
            value = jedis.get(K);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public boolean tryLock(String key, long timeout, TimeUnit unit, String desc) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            long nano = System.nanoTime();
            do {
                Long r = jedis.setnx(key.getBytes(), desc.getBytes());
                if (r == 1) {
                    jedis.expire(key.getBytes(), 3);
                    return true;
                } else {
                    byte[] origin = jedis.get(key.getBytes());
                }
                if (timeout == 0) {
                    break;
                }
            } while ((System.nanoTime() - nano) < unit.toNanos(timeout));
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public Boolean hexists(byte[] key, byte[] field) {
        Jedis jedis = getJedis();
        try {
            return jedis.hexists(key, field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;

    }

    public String hget(String mapName, String key) {
        Jedis jedis = getJedis();
        String value = "";
        try {
            value = jedis.hget(mapName, key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    @Override
    public void sadd(String key, String member) {
        Jedis jedis = getJedis();
        try {
            jedis.sadd(key, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    @Override
    public void srem(String key, String member) {
        Jedis jedis = getJedis();
        try {
            jedis.srem(key, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    @Override
    public void lpush(byte[] key, byte[] strings) {
        Jedis jedis = getJedis();
        try {
            jedis.lpush(key, strings);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[] channels) {
        Jedis jedis = getJedis();
        jedis.subscribe(jedisPubSub, channels);
    }

    public Long llen(byte[] key) {
        Jedis jedis = getJedis();
        Long len = 0l;
        try {
            len = jedis.llen(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return len;
    }

    @Override
    public List<byte[]> brpop(int timeout, final byte[]... keys) {
        Jedis jedis = getJedis();
        List<byte[]> bytes = null;
        try {
            bytes = jedis.brpop(timeout, keys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return bytes;
    }

    public byte[] rpop(byte[] key) {
        Jedis jedis = getJedis();
        byte[] bytes = null;
        try {
            bytes = jedis.rpop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return bytes;
    }

    public Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        Set<String> set = new HashSet<String>();
        try {
            set = jedis.smembers(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return set;
    }

    public void subscribe(JedisPubSub jedispubSub, String... channel) {
        Jedis jedis = getJedis();
        jedis.subscribe(jedispubSub, channel);
    }

    public void publish(String channel, String message) {
        Jedis jedis = getJedis();
        try {
            jedis.publish(channel, message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void publish(byte[] channel, byte[] message) {
        Jedis jedis = getJedis();
        try {
            jedis.publish(channel, message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void incrBy(String key, long number) {
        Jedis jedis = getJedis();
        try {
            jedis.incrBy(key, number);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void smove(String srckey, String dstkey, String member) {
        Jedis jedis = getJedis();
        try {
            jedis.smove(srckey, dstkey, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void expire(String srckey, int time) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(srckey, time);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
