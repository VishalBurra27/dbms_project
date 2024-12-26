package com.suny.service;

import com.suny.dao.CommentDAO;
import com.suny.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by admin on 23-2-4.8:39 am
 */
@Service
public class CommentService {

    private final CommentDAO commentDAO;

    @Autowired
    public CommentService(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    public List<Comment> getCommentsByEntity(int entity, int entityType) {
        return commentDAO.selectByEntity(entity, entityType);
    }

    public int addComment(Comment comment) {
        return commentDAO.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public void deleteComment(int entityId, int entityType) {
        // 
        commentDAO.updateStatus(entityId, entityType, 1);
    }


    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }


    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

}
















