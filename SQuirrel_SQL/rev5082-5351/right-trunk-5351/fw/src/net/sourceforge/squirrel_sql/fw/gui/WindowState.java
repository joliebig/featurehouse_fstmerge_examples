package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.Serializable;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.util.beanwrapper.RectangleWrapper;
import net.sourceforge.squirrel_sql.fw.xml.IXMLAboutToBeWritten;

public class WindowState implements IXMLAboutToBeWritten, Serializable
{
	private static final long serialVersionUID = 2664203798124718385L;

	
	private Window _window;

	
	private JInternalFrame _internalFrame;

	
	private Frame _frame;

	
	private RectangleWrapper _bounds = new RectangleWrapper(new Rectangle(600, 400));

	
	private boolean _visible = true;
	
	
	private int _frameExtendedState = 0;

	public interface IPropertyNames
	{
		String BOUNDS = "bounds";
		String FRAME_EXTENDED_STATE = "frameExtendedState";
		String VISIBLE = "visible";
	}

	
	public WindowState()
	{
		super();
	}

	
	public WindowState(Window window)
	{
		super();
		_window = window;
	}

	
	public WindowState(JInternalFrame internalFrame)
	{
		super();
		_internalFrame = internalFrame;
	}

	
	public WindowState(Frame frame)
	{
		super();
		_frame = frame;
	}

	
	public void copyFrom(WindowState obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("WindowState == null");
		}

		setBounds(obj.getBounds());
		setVisible(obj.isVisible());
		setFrameExtendedState(obj.getFrameExtendedState());
	}

	
	public void aboutToBeWritten()
	{
		refresh();
	}

	public RectangleWrapper getBounds()
	{
		refresh();
		return _bounds;
	}

	public void setBounds(RectangleWrapper value)
	{
		_bounds = value;
		_window = null;
		_internalFrame = null;
	}

	public boolean isVisible()
	{
		refresh();
		return _visible;
	}

	public void setVisible(boolean value)
	{
		_visible = value;
	}

	public int getFrameExtendedState()
	{
		refresh();
		return _frameExtendedState;
	}

	public void setFrameExtendedState(int value)
	{
		_frameExtendedState = value;
	}

	private void refresh()
	{
		Rectangle windRc = null;
		if (_window != null)
		{
			windRc = _window.getBounds();
			_visible = _window.isVisible();
		}
		else if (_internalFrame != null)
		{
			windRc = _internalFrame.getBounds();
			_visible = _internalFrame.isVisible();
		}
		else if (_frame != null)
		{
			windRc = _frame.getBounds();
			_visible = _frame.isVisible();
			_frameExtendedState = _frame.getExtendedState();
		}

		if (windRc != null)
		{
			if (_bounds == null)
			{
				_bounds = new RectangleWrapper();
			}
			_bounds.setFrom(windRc);
		}
	}
}
