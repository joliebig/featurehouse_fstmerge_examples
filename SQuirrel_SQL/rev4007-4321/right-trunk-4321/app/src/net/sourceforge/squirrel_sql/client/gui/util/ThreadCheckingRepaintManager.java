package net.sourceforge.squirrel_sql.client.gui.util;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ThreadCheckingRepaintManager extends RepaintManager
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(ThreadCheckingRepaintManager.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ThreadCheckingRepaintManager.class);
    
	public synchronized void addInvalidComponent(JComponent comp)
	{
		try
		{
			checkThread(comp);
		}
		catch (Exception ex)
		{
            
			s_log.debug(s_stringMgr.getString("ThreadCheckingRepaintManager.workinwrongthread"), ex);
		}
		super.addInvalidComponent(comp);
	}

	private void checkThread(JComponent comp) throws Exception
	{
		if (comp.isVisible())
		{
			final Component root = comp.getRootPane();
			if (root != null && root.isVisible())
			{
				if (!SwingUtilities.isEventDispatchThread())
				{
                    
					
				}
			}
		}
	}

	public synchronized void addDirtyRegion(JComponent comp, int i, int i1,
												int i2, int i3)
	{
		try
		{
			checkThread(comp);
		}
		catch (Exception ex)
		{
            
			s_log.debug(s_stringMgr.getString("ThreadCheckingRepaintManager.workinwrongthread"), ex);
		}
		super.addDirtyRegion(comp, i, i1, i2, i3);
	}
}
