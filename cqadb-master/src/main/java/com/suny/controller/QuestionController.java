package com.suny.controller;

import com.suny.model.*;
import com.suny.service.*;
import com.suny.utils.CqadbUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 23-2-3.6:22 pm
 */
@Controller
public class QuestionController {

    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;
    private final HostHolder hostHolder;
    private final UserService userService;

    private final CommentService commentService;

    private final FollowService followService;

    private final LikeService likeService;

    @Autowired
    public QuestionController(QuestionService questionService, HostHolder hostHolder, UserService userService, 
    		CommentService commentService, LikeService likeService, FollowService followService) {
        this.questionService = questionService;
        this.hostHolder = hostHolder;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.followService = followService;
    }


    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid) {

        Question question = questionService.getById(qid);
        model.addAttribute("question", question);
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        ArrayList<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);

        ArrayList<ViewObject> followUsers = new ArrayList<>();
        // Obtain follow user information
//        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
//        for (Integer userId : users) {
//            ViewObject vo = new ViewObject();
//            User user = userService.getUser(userId);
//            if (user == null) {
//                continue;
//            }
//            vo.set("name", user.getName());
//            vo.set("headUrl", user.getHeadUrl());
//            vo.set("id", user.getId());
//            followUsers.add(vo);
//        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }
        return "detail";
    }

    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreateDate(new Date());
            question.setTitle(title);
            if (hostHolder.getUser() == null) {
                // 
                question.setUserId(10000);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return CqadbUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("Failed to add topic" + e.getMessage());
        }
        return CqadbUtil.getJSONString(1, "fail");
    }
    
    @RequestMapping(value = "/questionDelete/{qid}", method = {RequestMethod.GET})
    public String questionDelete(Model model, @PathVariable("qid") int qid,  @RequestParam(value = "offset", defaultValue = "0") int offset) {
    	
    	// delete
    	questionService.deleteById(qid);

    	 List<Question> questionList = questionService.getLatestQuestion(0, null, offset, 10);
         List<ViewObject> objectList = new ArrayList<>();
         for (Question question : questionList) {
             ViewObject viewObject = new ViewObject();
             viewObject.set("question", question);
             viewObject.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
             viewObject.set("user", userService.getUser(question.getUserId()));
             objectList.add(viewObject);
         }
         
         model.addAttribute("vos", objectList);
         
        return "index";
    }
    
    @RequestMapping(value = "/questionUpdate/{qid}", method = {RequestMethod.GET})
    public String questionUpdate(Model model, @PathVariable("qid") int qid,  @RequestParam(value = "offset", defaultValue = "0") int offset) {
    	
    	// Revise
    	//questionService.deleteById(qid);

    	 List<Question> questionList = questionService.getLatestQuestion(0, null, offset, 10);
         List<ViewObject> objectList = new ArrayList<>();
         for (Question question : questionList) {
             ViewObject viewObject = new ViewObject();
             viewObject.set("question", question);
             viewObject.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
             viewObject.set("user", userService.getUser(question.getUserId()));
             objectList.add(viewObject);
         }
         
         model.addAttribute("vos", objectList);
         
        return "index";
    }
    
}


















