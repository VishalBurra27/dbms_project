package com.suny.service;

import com.suny.dao.LoginTicketDAO;
import com.suny.dao.UserDAO;
import com.suny.model.LoginTicket;
import com.suny.model.User;
import com.suny.utils.CqadbUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by admin on 23-2-1.10:27 am
 */
@Service
public class UserService {

    private final UserDAO userDAO;

    private final LoginTicketDAO loginTicketDAO;

    @Autowired
    public UserService(UserDAO userDAO, LoginTicketDAO loginTicketDAO) {
        this.userDAO = userDAO;
        this.loginTicketDAO = loginTicketDAO;
    }

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    @Transactional
    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "Username can not be empty");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "password can not be blank");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msg", "Username has already been registered");
            return map;
        }

        // password strength
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString());
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(CqadbUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        // Log in
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        String ticket;
        if (StringUtils.isBlank(username)) {
            map.put("msg", "Username can not be empty");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "password can not be blank");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msg", "Username does not exist");
            return map;
        }
        if (!CqadbUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "Incorrect password");
            return map;
        }
        LoginTicket loginTicket = loginTicketDAO.selectByUserId(user.getId());
        boolean expiredStatus = checkTicketExpired(loginTicket);
        if (!expiredStatus) {
            ticket = addLoginTicket(user.getId());
        } else {
            ticket = loginTicket.getTicket();
        }
        map.put("ticket", ticket);
        map.put("userId", user.getId());
        return map;
    }

    private boolean checkTicketExpired(LoginTicket loginTicket) {
        if (loginTicket == null) {
            return false;
        }
        // 0 invalid, 1 valid
        Date expiredDate = loginTicket.getExpired();
        // 
        if (expiredDate.before(new Date()) || loginTicket.getStatus() == 0) {
            loginTicketDAO.deleteTicket(loginTicket.getId());
            return false;
        }
        return true;
    }

    private String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        loginTicket.setExpired(date);
        loginTicket.setStatus(1);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }


    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 0);
    }

}







































