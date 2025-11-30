package com.hemza.master.recsClient.algorithm;

import com.hemza.master.recsClient.data.Event;
import com.hemza.master.recsClient.data.Item;
import com.hemza.master.recsClient.data.Request;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public class RecentlyClicked implements Algorithm {
	public LongLinkedOpenHashSet clickedItems = new LongLinkedOpenHashSet();

	@Override
	public void handleEventNotification(Event event) {
		clickedItems.addAndMoveToFirst(event.getId_item());
	}

	@Override
	public void handleItemUpdate(Item item) {
				clickedItems.addAndMoveToFirst(item.getId());


	}

	@Override
	public LongArrayList getRecommendations(Request request) {

		LongArrayList recs = new LongArrayList();
		recs.addAll(clickedItems);

		if (recs.size() > request.getLimit()) {
			return new LongArrayList(recs.subList(0, request.getLimit()));
		} else {
			return recs;
		}
	}
}
