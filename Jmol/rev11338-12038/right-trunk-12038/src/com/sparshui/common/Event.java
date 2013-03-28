package com.sparshui.common;

import java.io.Serializable;


public interface Event extends Serializable {

	
	public abstract int getEventType();
	
	
	public abstract byte[] serialize();
		
}
