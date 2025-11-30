package com.hemza.master.recsClient.algorithm;


import com.hemza.master.recsClient.data.Event;
import com.hemza.master.recsClient.data.Item;
import com.hemza.master.recsClient.data.Request;

import it.unimi.dsi.fastutil.longs.LongArrayList;

public interface Algorithm {


    public void handleEventNotification(Event event);

    public void handleItemUpdate(Item item);

    public LongArrayList getRecommendations(Request request);
}
