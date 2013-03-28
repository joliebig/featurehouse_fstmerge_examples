package com.sparshui.client;

import java.util.List;

import com.sparshui.common.Event;
import com.sparshui.common.Location;


public interface SparshClient {
	
	
	public int getGroupID(Location location);
	
	
	public List getAllowedGestures(int groupID);
	
	
	public void processEvent(int groupID, Event event);
}
