package com.suny.async;

import com.alibaba.fastjson.JSONObject;
import com.suny.utils.JedisAdapter;
import com.suny.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 23-2-7.4:13 pm
 */
@Service
public class EventProducer {


    private final JedisAdapter jedisAdapter;


    @Autowired
    public EventProducer(JedisAdapter jedisAdapter) {
        this.jedisAdapter = jedisAdapter;
    }


    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}














