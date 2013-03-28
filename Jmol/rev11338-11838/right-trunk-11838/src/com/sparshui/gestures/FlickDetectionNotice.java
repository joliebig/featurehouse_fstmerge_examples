package com.sparshui.gestures;

import java.awt.*; 
 
public class FlickDetectionNotice { 
  public static void main(String[] args) {

  }
  
  public void show(String text){
	    Frame frame=new Frame("FLICK!");
	    frame.setAlwaysOnTop(true);
	    Button button = new Button(text); 
	    frame.add(button); 
	    frame.setLayout(new FlowLayout());
	    frame.setSize(150,80);
	    frame.setBackground(Color.red);
	    frame.setLocation(500,500);
	    frame.setVisible(true);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		} 
	    frame.dispose();
	   
  }
}