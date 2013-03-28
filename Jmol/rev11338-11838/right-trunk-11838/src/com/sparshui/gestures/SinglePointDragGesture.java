package com.sparshui.gestures;

import java.util.Vector;



public class SinglePointDragGesture extends MultiPointDragGesture {

	
	public String getName() {
		return "SinglePointDragGesture";
	}
	
	
  protected Vector processMove(TouchData touchData) {
    if (_knownPoints.size() < 2) {
      return super.processMove(touchData);
    }
    adjustOffset();
    return null;
  }

}
