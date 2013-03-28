package net.sourceforge.squirrel_sql.fw.gui;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MaximizeInternalFramePositioner
	implements IInternalFramePositioner
{
	
	private static ILogger s_log =
		LoggerController.createLogger(MaximizeInternalFramePositioner.class);

	public MaximizeInternalFramePositioner()
	{
		super();
	}

	public void positionInternalFrame(JInternalFrame child)
	{
		if (child == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}
		try
		{
			child.setMaximum(true);
		}
		catch (PropertyVetoException ex)
		{
			s_log.error("Error maximizing JInternalFrame", ex);
		}
	}
}