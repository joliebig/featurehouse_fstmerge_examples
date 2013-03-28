package com.sparshui.server;

import org.jmol.util.Logger;

import com.sparshui.gestures.Gesture;












class GestureFactory {

	
  static Gesture createGesture(Object gid) {
   if (gid instanceof String) {
     String name = (String) gid;
     try {
       return (Gesture) Class.forName(name).newInstance();
     } catch (Exception e) {
       Logger.error("[GestureFactory] Error creating instance for " + name + ": \n" + e.getMessage());
       return null;
     }
   }
   int gestureID = ((Integer) gid).intValue();
   
	  Logger.error("[GestureFactory] Gesture not recognized: " + gestureID);
		return null;
	}

}
