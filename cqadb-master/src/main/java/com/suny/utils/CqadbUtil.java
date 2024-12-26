package com.suny.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by admin on 23-2-1.10:57 am
 */
public class CqadbUtil {

    private static final Logger logger = LoggerFactory.getLogger(CqadbUtil.class);
    
    public static String lang = "en_US";


    public static int ANONYMOUS_USERID = 10000;
    public static int SYSTEM_USERID = 10001;

    public static String getJSONString(int code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toJSONString();
    }


    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("Failed to generate MD5", e);
            return null;
        }

    }

}
