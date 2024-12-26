package com.suny.service;

import org.springframework.stereotype.Service;

/**
 * Created by admin on 23-2-1.10:45 p.m.
 */
@Service
public class WendaService {

    public String getMessage(int userId) {
        return "HelloMessage" + String.valueOf(userId);
    }
}
