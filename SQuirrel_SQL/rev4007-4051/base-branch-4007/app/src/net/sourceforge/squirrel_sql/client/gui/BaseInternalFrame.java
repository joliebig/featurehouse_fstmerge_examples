package net.sourceforge.squirrel_sql.client.gui;

import java.awt.event.KeyEvent;

import javax.swing.JInternalFrame;

public class BaseInternalFrame extends JInternalFrame
{
	
	public BaseInternalFrame()
	{
		super();
	}

	
	public BaseInternalFrame(String title)
	{
		super(title);
	}

	
	public BaseInternalFrame(String title, boolean resizable)
	{
		super(title, resizable);
	}

	
	public BaseInternalFrame(String title, boolean resizable, boolean closable)
	{
		super(title, resizable, closable);
	}

	
	public BaseInternalFrame(String title, boolean resizable,
							boolean closable, boolean maximizable)
	{
		super(title, resizable, closable, maximizable);
	}

	
	public BaseInternalFrame(String title, boolean resizable,
				boolean closable, boolean maximizable, boolean iconifiable)
	{
		super(title, resizable, closable, maximizable, iconifiable);
	}

	
	public void processKeyEvent(KeyEvent evt)
	{
		
		super.processKeyEvent(evt);
		
	}

}
