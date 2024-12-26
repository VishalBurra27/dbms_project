package com.suny.controller;

import com.suny.model.*;
import com.suny.service.CommentService;
import com.suny.service.FollowService;
import com.suny.service.LikeService;
import com.suny.service.QuestionService;
import com.suny.service.UserService;
import com.suny.utils.CqadbUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Main page related classes of the control project
 * Created by admin on 23-2-1.10:21 am
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final QuestionService questionService;

    private final UserService userService;

    private final FollowService followService;

    private final HostHolder hostHolder;

    private final CommentService commentService;
    
    private final LikeService likeService;

    @Autowired
    public HomeController(QuestionService questionService, UserService userService, FollowService followService,
    		HostHolder hostHolder, CommentService commentService, LikeService likeService) {
        this.questionService = questionService;
        this.userService = userService;
        this.followService = followService;
        this.hostHolder = hostHolder;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    /**
     * Private method to get list of questions
     *
     * @param userId user id
     * @param offset Start querying from the first few entries in the database
     * @param limit  Limit query to a few pieces of data
     * @return query set of questions
     */
    private List<ViewObject> getQuestion(int userId, String keyword, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestion(userId, keyword, offset, limit);
        List<ViewObject> objectList = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject viewObject = new ViewObject();
            viewObject.set("question", question);
            //viewObject.set("followCount", followService.getFollowerCount(EntityType.ENTITY_COMMENT, question.getId()));
            viewObject.set("followCount", followService.getFollowerCount(EntityType.ENTITY_COMMENT, question.getId()));
            List<Comment> commentList = commentService.getCommentsByEntity(question.getId(), EntityType.ENTITY_QUESTION);
            long likeCount = 0;
            for (Comment comment : commentList) {
            	long likeCount2 = likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId());
            	likeCount = likeCount + likeCount2;
            }
            viewObject.set("likeCount", likeCount);
            
            viewObject.set("user", userService.getUser(question.getUserId()));
            objectList.add(viewObject);
        }
        return objectList;
    }


    /**
     * The main page of the project, get the list of the first 10 questions by default
     *
     * @param model store data model
     * @param pop   If it is 0, query all questions, otherwise, query the list of specified user questions
     * @return Corresponding main page
     */
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getQuestion(0, null, 0, 10));
        model.addAttribute("i18n", CqadbUtil.lang);
        return "index";
    }

    /**
     * Query the user's question list according to the user's id
     *
     * @param model  store data model
     * @param userId specify the user's id
     * @return query result page
     */
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestion(userId, null, 0, 10));
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

}
















