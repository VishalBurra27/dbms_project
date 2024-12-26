package com.suny.controller;

import com.suny.model.HostHolder;
import com.suny.model.Message;
import com.suny.model.User;
import com.suny.model.ViewObject;
import com.suny.service.MessageService;
import com.suny.service.UserService;
import com.suny.utils.CqadbUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 23-2-4.7:38 pm
 */
@Controller
public class MessageController {
    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    private final UserService userService;

    private final HostHolder hostHolder;


    @Autowired
    public MessageController(MessageService messageService, UserService userService, HostHolder hostHolder) {
        this.messageService = messageService;
        this.userService = userService;
        this.hostHolder = hostHolder;
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            List<ViewObject> conversations = new ArrayList<>();
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", message);
                int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConversationUnreadCount(localUserId, message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("Failed to get the message list in the station" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @Param("conversationId") String conversationId) {
        if (StringUtils.isBlank(conversationId)) {
            return "/msg/list";
        }
        try {
            // Get the session list data between two accounts
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();

            // 
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                // Get the information of the user who sent the message
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                // Set profile picture and user ID
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                // Put each message into the message collection
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("Failed to get message details" + e.getMessage());
        }
        // 
        messageService.updateMessageReadStatus(conversationId);
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName, @RequestParam("content") String content) {

        try {
            if (hostHolder.getUser() == null) {
                return CqadbUtil.getJSONString(999, "not logged in");
            }
            User user = userService.selectByName(toName);
            if (user == null) {
                return CqadbUtil.getJSONString(1, "User does not exist");
            }
            Message message = new Message();
            message.setContent(content);
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setCreateDate(new Date());
            int fromId = hostHolder.getUser().getId();
            int toId = user.getId();
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));

            // 
            messageService.addMessage(message);
            return CqadbUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("Failed to add internal message" + e.getMessage());
            return CqadbUtil.getJSONString(1, "Insert message failed");
        }
    }


    @RequestMapping(path = {"/msg/jsonAddMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message message = new Message();
            message.setContent(content);
            message.setFromId(fromId);
            message.setToId(toId);
            message.setCreateDate(new Date());
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);
            return CqadbUtil.getJSONString(message.getId());
        } catch (Exception e) {
            logger.error("Failed to add comments" + e.getMessage());
            return CqadbUtil.getJSONString(1, "Failed to insert comment");
        }
    }


}


















