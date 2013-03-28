package com.sparshui.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import com.sparshui.common.Location;


class ClientConnection {

	
	private ServerToClientProtocol _protocol;

	
	private HashMap _groups;

	
	ClientConnection(Socket socket) throws IOException {
		_protocol = new ServerToClientProtocol(socket);
		_groups = new HashMap();
	}

	
  boolean processBirth(TouchPoint touchPoint) throws IOException {

    int groupID = getGroupID(touchPoint.getLocation());
    int jmolFlags = (groupID & 0xF0000000);
    if (jmolFlags != 0) {
      switch (jmolFlags) {
      case 0x10000000:
        
        _groups = new HashMap();
        break;
      }
      groupID &= ~jmolFlags;
    }

    
    Group group = getGroup(groupID);
    if (group != null) {
      
      
      touchPoint.setGroup(group);
      return true;
    }
    
    
    return false;
  }

	
	private Vector getGestures(int groupID) throws IOException {
		return _protocol.getGestures(groupID);
	}

	
	private int getGroupID(Location location) throws IOException {
		return _protocol.getGroupID(location);
	}

	
	private Group getGroup(int groupID) throws IOException {
		if (groupID == 0)
			return null;
		Group group = null;
		Integer gid = new Integer(groupID);
		if (_groups.containsKey(gid)) {
			group = (Group) _groups.get(gid);
		} else {
			

			
			
		  
			Vector gestureIDs = getGestures(groupID);
			group = new Group(groupID, gestureIDs, _protocol);
			_groups.put(gid, group);
		}

		
		return group;
	}

}
