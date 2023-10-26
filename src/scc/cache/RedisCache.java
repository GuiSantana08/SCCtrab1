package scc.cache;

import java.util.concurrent.CancellationException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scc.utils.Constants;
import scc.utils.Session;

public class RedisCache {
    private static final String RedisHostname = Constants.camposConst.getRedisHostname();
    private static final String RedisKey = Constants.camposConst.getredisKey();

    private static JedisPool instance;

    public synchronized static JedisPool getCachePool() {
        if (instance != null)
            return instance;
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        instance = new JedisPool(poolConfig, RedisHostname, 6380, 1000, RedisKey, true);
        return instance;
    }

    public static void putSession(Session session) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            jedis.set(session.getUid(), session.getUsername());
        } catch (Exception e) {
            // TODO
        }
    }

    public static Session getSession(String uid) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(uid);

            if (res == null)
                throw new CancellationException();

            return new Session(uid, res);
        } catch (Exception e) {
            throw new CancellationException();
        }
    }
}
