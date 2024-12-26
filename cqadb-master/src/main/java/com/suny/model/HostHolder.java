package com.suny.model;

import org.springframework.stereotype.Component;

/**
 * Created by admin on 23-2-1.10:38 pm
 */
@Component
public class HostHolder {

    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }


}















