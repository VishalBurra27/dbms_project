package com.suny.controller;

import com.suny.async.EventModel;
import com.suny.async.EventProducer;
import com.suny.async.EventType;
import com.suny.model.*;
import com.suny.service.CommentService;
import com.suny.service.FollowService;
import com.suny.service.QuestionService;
import com.suny.service.UserService;
import com.suny.utils.CqadbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 23-2-9.1:06 pm
 */
@Controller
public class FollowController {

    private final FollowService followService;

    private final CommentService commentService;

    private final QuestionService questionService;

    private final UserService userService;

    private final HostHolder hostHolder;

    private final EventProducer eventProducer;

    @Autowired
    public FollowController(FollowService followService, CommentService commentService, QuestionService questionService, UserService userService, HostHolder hostHolder, EventProducer eventProducer) {
        this.followService = followService;
        this.commentService = commentService;
        this.questionService = questionService;
        this.userService = userService;
        this.hostHolder = hostHolder;
        this.eventProducer = eventProducer;
    }

    @RequestMapping(path = "/followUser", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return CqadbUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        // 
        EventModel eventModel = new EventModel(EventType.FOLLOW);
        eventModel.setActorId(hostHolder.getUser().getId());
        eventModel.setEntityId(userId);
        eventModel.setEntityType(EntityType.ENTITY_USER);
        eventModel.setEntityOwnerId(userId);

        // Return the number of followers
        return CqadbUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    @RequestMapping(path = "/unfollowUser", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return CqadbUtil.getJSONString(999);
        }

        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        // 
        EventModel eventModel = new EventModel(EventType.FOLLOW);
        eventModel.setActorId(hostHolder.getUser().getId());
        eventModel.setEntityId(userId);
        eventModel.setEntityType(EntityType.ENTITY_USER);
        eventModel.setEntityOwnerId(userId);

        // Return the number of followers
        return CqadbUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    @RequestMapping(path = "/followQuestion", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return CqadbUtil.getJSONString(999);
        }

        Question question = questionService.getById(questionId);
        if (question == null) {
            return CqadbUtil.getJSONString(1, "problem does not exist");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        // 
        EventModel eventModel = new EventModel(EventType.FOLLOW);
        eventModel.setActorId(hostHolder.getUser().getId());
        eventModel.setEntityId(questionId);
        eventModel.setEntityType(EntityType.ENTITY_QUESTION);
        eventModel.setEntityOwnerId(question.getUserId());

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return CqadbUtil.getJSONString(ret ? 0 : 1, info);
    }


    @RequestMapping(path = "/unfollowQuestion", method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return CqadbUtil.getJSONString(999);
        }

        Question question = questionService.getById(questionId);
        if (question == null) {
            return CqadbUtil.getJSONString(1, "problem does not exist");
        }

        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        // 
        EventModel eventModel = new EventModel(EventType.FOLLOW);
        eventModel.setActorId(hostHolder.getUser().getId());
        eventModel.setEntityId(questionId);
        eventModel.setEntityType(EntityType.ENTITY_QUESTION);
        eventModel.setEntityOwnerId(question.getUserId());

        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFolloweeCount(EntityType.ENTITY_QUESTION, questionId));
        return CqadbUtil.getJSONString(ret ? 0 : 1, info);
    }


    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userid) {
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userid, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userid));
        model.addAttribute("curUser", userService.getUser(userid));
        return "followers";
    }


    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userid) {
        List<Integer> followeeIds = followService.getFollowers(EntityType.ENTITY_USER, userid, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userid, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userid));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer userId : userIds) {
            User user = userService.getUser(userId);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(userId));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, userId));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;

    }


}






















