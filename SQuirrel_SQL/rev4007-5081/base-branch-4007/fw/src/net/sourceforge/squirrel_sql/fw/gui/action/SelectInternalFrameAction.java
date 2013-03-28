package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SelectInternalFrameAction extends BaseAction
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SelectInternalFrameAction.class);

	private static final String FRAME_PTR = "FRAME_PTR";

	private static final int MAX_TITLE_LENGTH = 50;

	private MyPropertyChangeListener _myLis = null;

	public SelectInternalFrameAction(JInternalFrame child)
	{
		super(getTitle(child));
		putValue(FRAME_PTR, child);
		putValue(SHORT_DESCRIPTION,
				s_stringMgr.getString("SelectInternalFrameAction.description"));
		
		_myLis = new MyPropertyChangeListener();
		child.addPropertyChangeListener(JInternalFrame.TITLE_PROPERTY, _myLis);
	}

	public void actionPerformed(ActionEvent evt)
	{
		final JInternalFrame fr = getInternalFrame();
		if (fr != null)
		{
			new SelectInternalFrameCommand(fr).execute();
		}
	}

	private JInternalFrame getInternalFrame() throws IllegalStateException
	{
		final JInternalFrame fr = (JInternalFrame) getValue(FRAME_PTR);
		if (fr == null)
		{
			throw new IllegalStateException("No JInternalFrame associated with SelectInternalFrameAction");
		}
		return fr;
	}

	private static String getTitle(JInternalFrame child)
	{
		if (child == null)
		{
			throw new IllegalArgumentException("JInternalFrame == null");
		}

		String myTitle = child.getTitle();
		if (myTitle.length() > MAX_TITLE_LENGTH)
		{
			myTitle = myTitle.substring(0, MAX_TITLE_LENGTH) + "...";
		}

		return myTitle;
	}

	
	
	private class MyPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			putValue(BaseAction.NAME, getTitle(getInternalFrame()));
		}
	}
}

