package com.sparshui.inputdevice;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.swing.SwingUtilities;

import org.jmol.api.JmolTouchSimulatorInterface;
import org.jmol.util.Logger;

import com.sparshui.common.ConnectionType;
import com.sparshui.common.NetworkConfiguration;
import com.sparshui.common.TouchState;


public class JmolTouchSimulator implements JmolTouchSimulatorInterface {

  private TreeSet _events = new TreeSet(new TouchDataComparator());
	protected HashMap _active = new HashMap();
	private boolean _recording = false;
	private int _touchID = 0;
	private long _when = 0;
	private Timer _timer;
	
	private Component _display;
	
	private DataOutputStream _out;

	public JmolTouchSimulator() {
	}

	public void dispose() {
	  try {
	    _out.close();
	  } catch (Exception e) {
	    
	  }
	}
	
	
	public boolean startSimulator(Component display) {
	   _display = display;
	   String address = "localhost";
	    _timer = new Timer();
	    try {
	      Socket socket = new Socket(address, NetworkConfiguration.PORT);
	      _out = new DataOutputStream(socket.getOutputStream());
	      _out.writeByte(ConnectionType.INPUT_DEVICE);
	      return true;
	    } catch (UnknownHostException e) {
	      Logger.error("Could not locate a server at " + address);
	    } catch (IOException e) {
	      Logger.error("Failed to connect to server at " + address);
	    }
	    return false;
	}
	
	
	public void toggleMode() {
		if(_recording) {
			endRecording();
		} else {
		  startRecording();
		}

	}
	
	public void startRecording() {
		_recording = true;
		_active.clear();
	}
	
	public void endRecording() {
		_recording = false;
		dispatchTouchEvents();
	}

	
	public void mousePressed(long time, int x, int y) {
		handleMouseEvent(time, x, y, TouchState.BIRTH);
	}

	
	public void mouseReleased(long time, int x, int y) {
		handleMouseEvent(time, x, y, TouchState.DEATH);
	}

	
	public void mouseDragged(long time, int x, int y) {
		handleMouseEvent(time, x, y, TouchState.MOVE);
	}

	private void handleMouseEvent(long time, int x, int y, int type) {
		
		
		TouchData te = new TouchData();
		te.id = (type == TouchState.BIRTH) ? ++_touchID : _touchID;
		Point p = new Point(x, y);
		SwingUtilities.convertPointToScreen(p, _display);
		te.x = p.x;
		te.y = p.y;
		
		
		te.type = type;
		te.when = time;
		
		if(_recording) {
			
			if(type == TouchState.BIRTH) {
				te.delay = 0;
				_when = te.when;
			} else {
				te.delay = te.when - _when;
			}
			_events.add(te);
		} else {
			
			dispatchTouchEvent(te);
			if (Logger.debugging)
			  Logger.debug("[JmolTouchSimulator] dispatchTouchEvent("+te.id+", "+te.x+", "+te.y+", "+te.type+")");
		}
	}
	
	private void dispatchTouchEvents() {
	  Iterator it = _events.iterator();
	  while (it.hasNext()) {
		  TouchData e = (TouchData) it.next();
			TouchTimerTask task = new TouchTimerTask(e);
			_timer.schedule(task, e.delay + 250);
		}
		_events.clear();
		_touchID = 0;
	}
	
	protected void dispatchTouchEvent(TouchData e) {
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension dim = tk.getScreenSize();
    if (Logger.debugging)
      Logger.debug("[JmolTouchSimulator] dispatchTouchEvent("+e.id+", "+e.x+", "+e.y+", "+e.type+")");
    try {
      _out.writeInt(1);
      _out.writeInt(e.id);
      _out.writeFloat(((float) e.x / (float) dim.width));
      _out.writeFloat(((float) e.y / (float) dim.height));
      _out.writeByte((byte) e.type);
    } catch (IOException e1) {
      System.err.println("Failed to send event to server.");
    }
  }

	protected class TouchData {
		public int type;
		public int id;
		public int x;
		public int y;
		public long when;
		public long delay;
	}
	
	protected class TouchDataComparator implements Comparator {

		public int compare(Object oo1, Object oo2) {
      TouchData o1 = (TouchData) oo1;
      TouchData o2 = (TouchData) oo2;
      return (o1.delay == o2.delay ? (o1.when < o2.when ? -1 : 1) 
          : o1.delay < o2.delay ? -1 : 1);
    }
	}
	
	private class TouchTimerTask extends TimerTask {
		private TouchData e;
		
		TouchTimerTask(TouchData e) {
			this.e = e;
		}

		
		public void run() {
		  Thread.currentThread().setName("JmolTouchSimulator for type " + e.id);
			dispatchTouchEvent(e);
			Integer iid = new Integer(e.id);
			if(e.type == TouchState.DEATH) {
        _active.remove(iid);
			} else {
        _active.put(iid, e);
			}
      Thread.currentThread().setName("JmolTouchSimulator idle");
		}
	}	
}
