package com.hemza.master.recsClient.algorithm;

import java.util.Collections;

import com.hemza.master.recsClient.data.Event;
import com.hemza.master.recsClient.data.Item;
import com.hemza.master.recsClient.data.Request;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class Random implements Algorithm{
    private LongOpenHashSet items = new LongOpenHashSet();
    @Override
    public void handleEventNotification(Event id_event) {

    }

    @Override
    public void handleItemUpdate(Item item) {
        //if new items arrive, add their ids to the set of item ids
            this.items.add(item.getId());

    }

    @Override
    public LongArrayList getRecommendations(Request request) {
        //create a result list and copy the known item ids there
        LongArrayList recs = new LongArrayList(items);

        Collections.shuffle(recs);

        if (recs.size()>request.getLimit()) {
            return new LongArrayList(recs.subList(0, request.getLimit()));
        }else {
            return recs;
        }
    }
}
