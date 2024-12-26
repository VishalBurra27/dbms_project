package com.suny.controller;

import com.suny.async.EventModel;
import com.suny.async.EventProducer;
import com.suny.async.EventType;
import com.suny.model.EntityType;
import com.suny.model.Question;
import com.suny.model.ViewObject;
import com.suny.service.FollowService;
import com.suny.service.QuestionService;
import com.suny.service.UserService;
import com.suny.utils.CqadbUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 23-2-16.6:46 pm
 */
@Controller
public class LoginController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    private final EventProducer eventProducer;
    
    private final QuestionService questionService;
    
    private final FollowService followService;

    @Autowired
    public LoginController(UserService userService, EventProducer eventProducer,
    		QuestionService questionService, FollowService followService) {
        this.userService = userService;
        this.eventProducer = eventProducer;
        this.questionService = questionService;
        this.followService = followService;
    }


    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "next", required = false) String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean remeberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (remeberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("registration exception" + e.getMessage());
            model.addAttribute("msg", "Server Error");
            return "login";
        }
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean remeberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (remeberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);

                logger.info("Start sending login action emails");
                // 

               /* eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("username", username)
                        .setExt("email", "demo@vip.qq.com")
                        .setActorId((Integer) map.get("userId")));*/

                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("Login exception" + e.getMessage());
            return "login";
        }
    }


    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
    
    @RequestMapping(value = "/language/{lang}", method = {RequestMethod.GET})
    public String language(Model model, @PathVariable("lang") String lang) {
    	if(lang==null && "".equals(lang)){
    		lang = "zh_CN";
    	}
    	
    	model.addAttribute("i18n", lang);
    	
    	// Temporary storage
    	CqadbUtil.lang = lang;
    	
		List<Question> questionList = questionService.getLatestQuestion(0, null, 0, 10);
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
