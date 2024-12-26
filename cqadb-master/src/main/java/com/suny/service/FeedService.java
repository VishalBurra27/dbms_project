package com.suny.service;

import com.suny.dao.FeedDAO;
import com.suny.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by admin on 23-2-10.10:15 pm
 */
@Service
public class FeedService {

    private final FeedDAO feedDAO;

    @Autowired
    public FeedService(FeedDAO feedDAO) {
        this.feedDAO = feedDAO;
    }

    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectUserFeeds(maxId, userIds, count);
    }

    public boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    public Feed getById(int id) {
        return feedDAO.getFeedById(id);
    }


}













