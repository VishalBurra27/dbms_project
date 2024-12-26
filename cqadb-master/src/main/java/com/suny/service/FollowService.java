package com.suny.service;

import com.suny.utils.JedisAdapter;
import com.suny.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 23-2-9.12:38 pm
 */
@Service
public class FollowService {

    private final JedisAdapter jedisAdapter;


    @Autowired
    public FollowService(JedisAdapter jedisAdapter) {
        this.jedisAdapter = jedisAdapter;
    }


    /**
     * The user follows an entity, which can be any entity such as questions, users, comments, etc.
     *
     * @param userId     User ID
     * @param entityType type of entity
     * @param entityId   Entity ID
     * @return
     */
    public boolean follow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        // Entity's fans increase the current user
        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.multi(jedis);
        transaction.zadd(followeeKey, date.getTime(), String.valueOf(userId));
        // 
        transaction.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(transaction, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }


    public boolean unFollow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        // Entity's fans increase the current user
        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.multi(jedis);
        // Entity's followers remove the current user
        transaction.zrem(followeeKey, String.valueOf(userId));
        //
        transaction.zrem(followeeKey, String.valueOf(entityId));
        // 
        List<Object> ret = jedisAdapter.exec(transaction, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followerKey, 0, offset + count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followeeKey, 0, offset + count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFormSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }


    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }


    private List<Integer> getIdsFormSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }


    /**
     * Determine whether the user has followed an entity
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }


}

















