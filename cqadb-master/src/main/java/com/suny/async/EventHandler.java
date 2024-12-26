package com.suny.async;

import java.util.List;

/**
 * Created by admin on 23-2-7.4:38 pm
 */
public interface EventHandler {

    void doHandle(EventModel model);


    List<EventType> getSupportEventTypes();
}
