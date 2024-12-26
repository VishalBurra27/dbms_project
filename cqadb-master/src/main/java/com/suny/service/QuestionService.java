package com.suny.service;

import com.suny.dao.QuestionDAO;
import com.suny.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by admin on 23-2-1.10:15 am
 */
@Service
public class QuestionService {

    private final QuestionDAO questionDAO;

    private final SensitiveService sensitiveService;

    @Autowired
    public QuestionService(QuestionDAO questionDAO, SensitiveService sensitiveService) {
        this.questionDAO = questionDAO;
        this.sensitiveService = sensitiveService;
    }


    public int addQuestion(Question question) {
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        // Sensitive word review
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public List<Question> getLatestQuestion(int userId, String keyword, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, keyword, offset, limit);
    }

    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }

    public Question getById(int id) {
        return questionDAO.getById(id);
    }
    
    public void deleteById(int id) {
    	questionDAO.deleteById(id);
    }
}














