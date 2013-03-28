package com.sparshui.gestures;



import java.util.Vector;

import com.sparshui.common.Location;
import com.sparshui.common.messages.events.DragEvent;
import com.sparshui.common.messages.events.FlickEvent;


public class Flick extends StandardDynamicGesture {

	
	
	
	class Locations{
		double X, Y;
		public Locations(){
			X=Y=0;}
		public Locations(double x, double y){
			X = x;
			Y= y;}
	}
	
	
	

	
	class Displacements{
		double dx, dy, disp;
		public Displacements(){
			dx=dy=disp;}
		public Displacements(Locations first, Locations second){
			double x1 = first.X;
			double y1 = first.Y;
			double x2 = second.X;
			double y2 = second.Y;
			
			dx = x2-x1;
			dy = y2-y1;
			disp = Math.sqrt((dx*dx) + (dy*dy));
		}
	}
	
	
	

	
	public class Velocity{
		double vx, vy, v;	
		public Velocity(){
			vx = 0;
			vy = 0;
			v = 0;
		}		
		public Velocity(double xVelocity, double yVelocity, double velocity){
			vx = xVelocity;
			vy = yVelocity;
			v= velocity;
		}
	}
	

	
	
	
	class Acceleration{
		double ax, ay;
		public Acceleration(){
			ax=0;
			ay=0;
		}
		public Acceleration(double xAcceleration, double yAcceleration){
			ax = xAcceleration;
			ay = yAcceleration;
		}
	}
	

	
	
	
	
	
	int xDirection, yDirection;
	int flickSpeed; 
	private Velocity[] runningVelocities;	
	
	
	
	
	
	
	
	protected Location _offset = null;
	protected Location _offsetCentroid = null;
	private long time[];
	private Locations[] location;
	int numMoves;
	
	private Displacements[]displacements;
	int displacementPointer;
	int rVelocityPointer;	
	int timePointer;		
	
	public Flick() {
		super();
		displacements = new Displacements[4];
		displacementPointer = 0;	
		rVelocityPointer = 0; 		
		timePointer = 0;			
		
		location = new Locations[2];	
		runningVelocities = new Velocity[7];
		numMoves = 0;
		time = new long[5];	
	}

	
	public String getName() {
		return "FlickGesture";
	}
	
	
	public int getGestureType() {
		return GestureType.FLICK_GESTURE;
	}

	
	protected Vector processBirth(TouchData touchData) {
		if(_offset == null) {
			_offset = new Location(0,0);
			_offsetCentroid = _newCentroid;
		} else {
			adjustOffset();
		}
		return null;
	}

	
	protected Vector processMove(TouchData touchData) {
		Vector events = new Vector();
		updateOffsetCentroid();
		events.add(new DragEvent(_offsetCentroid.getX(), _offsetCentroid.getY()));
		
		
		
		
		if(timePointer>4){
			for(int i=0;i<4;i++){
				time[i] = time[i+1];
			}
			time[4] = System.currentTimeMillis();
			timePointer++;
		}else{
			time[timePointer] = System.currentTimeMillis(); 
			timePointer++;
		}
		
		if (numMoves==0){
				
			
			location[0] = new Locations(touchData.getLocation().getX(), touchData.getLocation().getY());
			numMoves++;
		
		}else{
		
			numMoves++;
			
			
			if(numMoves>2){
				location[0] = location[1];
			}
			location[1] = new Locations(touchData.getLocation().getX(), touchData.getLocation().getY());
			
			
			if(displacementPointer>3){
				
				for(int i=0;i<3;i++){
					displacements[i] = displacements[i+1];
				}
				
				displacements[3] = new Displacements(location[0], location[1]);
				displacementPointer++;
			}else{
				displacements[displacementPointer] = new Displacements(location[0], location[1]);
				displacementPointer++;
			}

			
			if(displacementPointer>4){
				double sum=0,sum_x=0,sum_y = 0;			
				
				for(int i=0;i<4;i++){
					sum = sum+displacements[i].disp;
					sum_x = sum_x+displacements[i].dx;
					sum_y = sum_y+displacements[i].dy;
				}			
				
				double runningV = 1000000*(sum / ((time[4]-time[0])));	
				double runningV_x = 1000000*(sum_x / ((time[4]-time[0])));
				double runningV_y = 1000000*(sum_y / ((time[4]-time[0])));
				
				if(rVelocityPointer>6){
					
					for(int i=0; i<6;i++){
						runningVelocities[i] = runningVelocities[i+1];
					}
					runningVelocities[6] = new Velocity(runningV_x,runningV_y,runningV);
					rVelocityPointer++;
	
				}else{
									
					
					runningVelocities[rVelocityPointer] = new Velocity(runningV_x,runningV_y,runningV);
					rVelocityPointer++;
				}
			}

			
			
			
							
			
		}
		
		
		
		
		
		return events;
	}

	
	protected Vector processDeath(TouchData touchData) {
		
		
		
		
		if(_knownPoints.size() == 0) {
			_offset = null;
			_offsetCentroid = null;
		} else {
			adjustOffset();
		}
		
		
		
		Vector events = new Vector();
		


		int positive = 0;

		for(int j = 7, i=0; rVelocityPointer>=6 && j>2;j--, i++){
			double temp = runningVelocities[runningVelocities.length-j+1].v - runningVelocities[runningVelocities.length-j].v;
			if (temp>=0){
				positive++;
			}
		}
		
		if (positive>=3){
			

			
			
			double avgRV=0;
			for(int i=1; i<=5; i++){
				avgRV = avgRV + runningVelocities[runningVelocities.length-1-i].v;
			}
			avgRV = avgRV/5;
			
			int speedLevel;
			if(avgRV<=500)	
				speedLevel = 1;
			else if (avgRV<=1000)
				speedLevel = 2;
			else
				speedLevel = 3;
			
			
			
			
			int xDirection, yDirection;
			if(location[1].X - location[0].X>0)
				xDirection = 1;
			else
				xDirection = -1;
			
			if(location[1].Y - location[0].Y>0)
				yDirection = 1;
			else
				yDirection = -1;
			
			
			
			events.add(new FlickEvent(speedLevel, xDirection, yDirection));
			
			if (speedLevel==1){
				
			}else if (speedLevel ==2){
				
			}else{
				
			}

			
		}
				

		return events;
	}
	
	
	protected void adjustOffset() {
		_offset = new Location(
				_newCentroid.getX() - _oldCentroid.getX() + _offset.getX(),
				_newCentroid.getY() - _oldCentroid.getY() + _offset.getY()
		);
	}
	
	
	protected void updateOffsetCentroid() {
		float x = _newCentroid.getX() - _offset.getX();
		float y = _newCentroid.getY() - _offset.getY();
		_offsetCentroid = new Location(x, y);
	}

}