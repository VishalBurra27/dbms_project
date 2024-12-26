package com.suny.async.handler;

import com.suny.async.EventHandler;
import com.suny.async.EventModel;
import com.suny.async.EventType;
//import com.suny.utils.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 23-2-8.9:31 am
 */
@Component
public class LoginExceptionHandler implements EventHandler {

//    private final MailSender mailSender;
//
//    @Autowired
//    public LoginExceptionHandler(MailSender mailSender) {
//        this.mailSender = mailSender;
//    }

    @Override
    public void doHandle(EventModel model) {
        // Determine whether the user is logged in abnormally
        Map<String, Object> map = new HashMap<>();
        map.put("username", model.getExt("username"));
//        mailSender.sendWithHTMLTemplate(model.getExt("email"), "login ip exception", "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}


















