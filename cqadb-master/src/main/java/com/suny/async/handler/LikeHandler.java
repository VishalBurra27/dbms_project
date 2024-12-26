package com.suny.async.handler;

import com.suny.async.EventHandler;
import com.suny.async.EventModel;
import com.suny.async.EventType;
import com.suny.model.Message;
import com.suny.model.User;
import com.suny.service.MessageService;
import com.suny.service.UserService;
import com.suny.utils.CqadbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 23-2-7.4:41 p.m.
 */
@Component
public class LikeHandler implements EventHandler {

    private final MessageService messageService;

    private final UserService userService;

    @Autowired
    public LikeHandler(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(CqadbUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreateDate(new Date());
        User user = userService.getUser(model.getActorId());
        message.setContent("user" + user.getName() + "liked your comment,http://127.0.0.1:8080/question/" + model.getExt("questionId"));
        // The session ID here must be the session between the system administrator and the notification user
        message.setConversationId(CqadbUtil.SYSTEM_USERID + "_" + model.getEntityOwnerId());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}


















