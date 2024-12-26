package com.suny.controller;

import com.suny.model.EntityType;
import com.suny.model.Question;
import com.suny.model.ViewObject;
import com.suny.service.FollowService;
import com.suny.service.QuestionService;
import com.suny.service.SearchService;
import com.suny.service.UserService;
import com.suny.utils.CqadbUtil;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 23-2-13.10:52 p.m.
 */
@Controller
public class SearchController {

    private static Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;
    private final FollowService followService;
    private final UserService userService;
    private final QuestionService questionService;


    @Autowired
    public SearchController(SearchService searchService, FollowService followService, UserService userService, QuestionService questionService) {
        this.searchService = searchService;
        this.followService = followService;
        this.userService = userService;
        this.questionService = questionService;
    }

    @RequestMapping(path = {"/search"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
    	
    	 List<Question> questionList = questionService.getLatestQuestion(0, keyword, offset, 10);
         List<ViewObject> objectList = new ArrayList<>();
         for (Question question : questionList) {
             ViewObject viewObject = new ViewObject();
             viewObject.set("question", question);
             viewObject.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
             viewObject.set("user", userService.getUser(question.getUserId()));
             objectList.add(viewObject);
         }
         
         model.addAttribute("vos", objectList);
         model.addAttribute("i18n", CqadbUtil.lang);
         
        return "index";
        
//        try {
//             List<Question> questionList = searchService.searchQuestion(keyword, offset, count, "<em>", "</em>");
//
//            List<ViewObject> vos = new ArrayList<>();
//            for (Question question : questionList) {
//                Question q = questionService.getById(question.getId());
//                ViewObject vo = new ViewObject();
//                if (question.getContent() != null) {
//                    q.setContent(question.getContent());
//                }
//                if (question.getTitle() != null) {
//                    q.setTitle(question.getTitle());
//                }
//                vo.set("question", vos);
//                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
//                vo.set("user", userService.getUser(q.getUserId()));
//                vos.add(vo);
//            }
//            model.addAttribute("vos", vos);
//            model.addAttribute("keyword", keyword);
//        } catch (IOException e) {
//            logger.error("transport stream error", e.getMessage());
//        } catch (SolrServerException e) {
//            logger.error("solr service failed", e.getMessage());
//        }
//        return "result";
    }


}


























