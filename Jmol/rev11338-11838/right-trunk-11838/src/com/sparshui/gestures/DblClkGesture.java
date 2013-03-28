package com.sparshui.gestures;


import java.util.Vector;

import com.sparshui.common.TouchState;
import com.sparshui.common.messages.events.DblClkEvent;
import com.sparshui.server.TouchPoint;

public class DblClkGesture implements Gesture {

	
	
	public class TouchPtData{
		TouchPoint touchPt;
		long time;

		
		public TouchPtData(){
			time = -1;
			
		}

		
		public TouchPtData(TouchPoint tp, long timeP){
			touchPt = tp;
			time = timeP;
		}
	}
	
	


	private TouchPtData[] tpData;
	private int nextElement;
	private int numElems;


	
	public DblClkGesture(){
		tpData = new TouchPtData[4];
		nextElement = 0;
		numElems = 0;
	}

	
	public int getGestureType() {
		
		return GestureType.DBLCLK_GESTURE;
	}

	
	public String getName() {
		return "DblClkGesture";
	}


	
	
	public Vector processChange(Vector touchPoints,
			TouchPoint changedTouchPoint) {

		Vector retEvents = new Vector();
		
		
	
			
	

		
		long currTime = System.currentTimeMillis(); 	

	    TouchPoint tpt = (TouchPoint) changedTouchPoint.clone();
		tpData[nextElement] = new TouchPtData(tpt,currTime) ;
		if(numElems<4){
			numElems++;
		
		}
		if(nextElement<3){
			nextElement++;	
		}

		
     
		
	 if(numElems ==4){
        
        
			
			boolean isMove = false;
			if(!(tpData[0].touchPt.getState()==TouchState.BIRTH) || !(tpData[1].touchPt.getState()==TouchState.DEATH)
					|| !(tpData[2].touchPt.getState()==TouchState.BIRTH) 
					|| !(tpData[3].touchPt.getState()==TouchState.DEATH)
					&& !(isMove = (tpData[3].touchPt.getState()==TouchState.MOVE))){
				shiftArr();
				return retEvents;
			}

				
				long duration = Math.abs(tpData[0].time - tpData[3].time);
				
				
				if(duration <= 1000){

					
					
					if(Math.abs(tpData[0].touchPt.getLocation().getX()-tpData[2].touchPt.getLocation().getX())<=0.1 &&	
							Math.abs(tpData[0].touchPt.getLocation().getY()-tpData[2].touchPt.getLocation().getY())<=0.1){ 
						

						
						retEvents.add(new DblClkEvent(changedTouchPoint.getID(),
								changedTouchPoint.getLocation().getX(),
								changedTouchPoint.getLocation().getY(), isMove ? 0 : 1));
				
				
				
				
						
						
						
						TouchPtData[] temp = new TouchPtData[4];
						tpData = temp;
						numElems = 0;
						nextElement = 0;
					}
				}
				
				
	 }
	
	 if(numElems==4){ 
		 shiftArr();
	 }
		return retEvents;
}


	private void shiftArr(){
		TouchPtData[] temp =  new TouchPtData[4];
		for(int i=0; i<temp.length; i++){
	
		}
		System.arraycopy(tpData, 1, temp, 0, 3);
	
		tpData = temp;	
		numElems =3;
		nextElement = 3;
	
		
		for(int i=0; i<temp.length; i++){
	
			
		}
	
	}
}
