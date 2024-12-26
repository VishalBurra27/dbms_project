package com.suny.utils;

/**
 * Created by admin on 23-2-16.8:05 am
 */
public class RedisKeyUtil {

    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_TIMELINE = "TIMELINE";

    // get followers
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // Object of concern
    private static String BIZ_FOLOWEE = "FOLLOWEE";

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    /**
     * A user's fan key
     *
     * @param entityType entity type
     * @param entityId   Entity ID
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    /**
     * Each user's attention key to a certain type of entity
     *
     * @param userId     User ID
     * @param entityType entity type
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }


    public static String getTImeline(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }


}















