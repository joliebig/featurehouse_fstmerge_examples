

package edu.rice.cs.drjava.ui;

import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.util.swing.Utilities;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

public class DetachedFrame extends SwingFrame {
  
  public static class FrameState {
    private Point _loc;
    private Dimension _dim;
    public FrameState(Point l, Dimension d) {
      _loc = l;
      _dim = d;
    }
    public FrameState(String s) {
      StringTokenizer tok = new StringTokenizer(s);
      try {
        int x = Integer.valueOf(tok.nextToken());
        int y = Integer.valueOf(tok.nextToken());
        int w = Integer.valueOf(tok.nextToken());
        int h = Integer.valueOf(tok.nextToken());
        _loc = new Point(x, y);
        _dim = new Dimension(w, h);
      }
      catch(NoSuchElementException nsee) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nsee);
      }
      catch(NumberFormatException nfe) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nfe);
      }
    }
    public FrameState(DetachedFrame comp) {
      _loc = comp.getLocation();
      _dim = comp.getSize();
    }
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(_loc.x);
      sb.append(' ');
      sb.append(_loc.y);
      sb.append(' ');
      sb.append(_dim.width);
      sb.append(' ');
      sb.append(_dim.height);
      return sb.toString();
    }
    public Point getLocation() { return _loc; }
    public Dimension getDimension() { return _dim; }
  }
  
  
  Runnable1<DetachedFrame> _detach;
  
  Runnable1<DetachedFrame> _reattach;
  
  private FrameState _lastState = null;
  
  private MainFrame _mainFrame;  
  
  private WindowAdapter _wa = new WindowAdapter() {
    public void windowClosing(WindowEvent we) {
      setDisplayInFrame(false);
    }
  };

  
  public FrameState getFrameState() {
    if (isVisible()) {
      return new FrameState(this);
    }
    else {
      return _lastState;
    }
  }
  
  
  public void setFrameState(FrameState ds) {
    _lastState = ds;
    if (_lastState != null) {
      setLocation(_lastState.getLocation());
      setSize(_lastState.getDimension());
      validate();
    }
  }  
  
  
  public void setFrameState(String s) {
    try { _lastState = new FrameState(s); }
    catch(IllegalArgumentException e) { _lastState = null; }
    if (_lastState != null) {
      setLocation(_lastState.getLocation());
      setSize(_lastState.getDimension());
    }
    else {
      Utilities.setPopupLoc(this, _mainFrame);
      setSize(700,400);
    }
    validate();
  }
  
  
  public DetachedFrame(String name, MainFrame mf, Runnable1<DetachedFrame> detach, Runnable1<DetachedFrame> reattach) {
    super(name);
    _mainFrame = mf;
    _detach = detach;
    _reattach = reattach;
    initDone(); 
  }
  
  
  public void setVisible(boolean vis) {
    super.setVisible(vis);
    _lastState = new FrameState(this);
  }

  
  public void setDisplayInFrame(boolean b) {
    if (b) {
      _detach.run(this);
      setVisible(true);
      addWindowListener(_wa);
    }
    else {
      removeWindowListener(_wa);
      setVisible(false);
      getContentPane().removeAll();
      _reattach.run(this);
    }
  }
}
