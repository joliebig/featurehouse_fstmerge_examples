package net.sourceforge.squirrel_sql.client.gui.mainframe;

import java.awt.Rectangle;
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.gui.WindowState;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.RectangleWrapper;

import net.sourceforge.squirrel_sql.client.gui.WindowManager;

public class MainFrameWindowState extends WindowState implements Serializable
{
	private static final long serialVersionUID = -7443323389797901005L;

	
	public interface IPropertyNames
	{
		String ALIASES_WINDOW_STATE = "aliasesWindowState";
		String DRIVERS_WINDOW_STATE = "driversWindowState";
	}

	private WindowState _driversWindowState = new WindowState();
	private WindowState _aliasesWindowState = new WindowState();

	private transient WindowManager _mgr;

	public MainFrameWindowState()
	{
		_driversWindowState.setBounds(new RectangleWrapper(new Rectangle(5, 5, 250, 250)));
		_aliasesWindowState.setBounds(new RectangleWrapper(new Rectangle(400, 5, 250, 250)));
	}

	public MainFrameWindowState(WindowManager mgr)
	{
		super(mgr.getMainFrame());
		_mgr = mgr;
	}

	
	public void aboutToBeWritten()
	{
		super.aboutToBeWritten();
		refresh();
	}

	public WindowState getAliasesWindowState()
	{
		refresh();
		return _aliasesWindowState;
	}

	public WindowState getDriversWindowState()
	{
		refresh();
		return _driversWindowState;
	}

	public void setAliasesWindowState(WindowState value)
	{
		_aliasesWindowState = value;
	}

	public void setDriversWindowState(WindowState value)
	{
		_driversWindowState = value;
	}

	private void refresh()
	{
		if (_aliasesWindowState == null)
		{
			_aliasesWindowState = new WindowState();
		}
		if (_driversWindowState == null)
		{
			_driversWindowState = new WindowState();
		}

		if (_mgr != null)
		{
			_aliasesWindowState.copyFrom(_mgr.getAliasesWindowState());
			_driversWindowState.copyFrom(_mgr.getDriversWindowState());
		}
	}
}
