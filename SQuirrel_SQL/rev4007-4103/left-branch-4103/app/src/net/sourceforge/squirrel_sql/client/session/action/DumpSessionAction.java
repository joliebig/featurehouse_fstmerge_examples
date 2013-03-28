package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DumpSessionAction extends SquirrelAction
											implements ISessionAction
{
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DumpSessionAction.class);  
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(DumpSessionAction.class);

	
	private ISession _session;

	
	public DumpSessionAction(IApplication app)
	{
		super(app);
	}

	
	public void setSession(ISession session)
	{
		_session = session;
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		final Frame parentFrame = getParentFrame(evt);
		FileExtensionFilter[] filters = new FileExtensionFilter[1];
		filters[0] = new FileExtensionFilter("Text files", new String[] { ".txt" });
        
        String label = s_stringMgr.getString("DumpSessionAction.warning");
		final JLabel lbl = new JLabel(label);
		lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		final File outFile = Dialogs.selectFileForWriting(parentFrame, filters, lbl);
		if (outFile != null)
		{
			DumpSessionCommand cmd = new DumpSessionCommand(outFile);
			cmd.setSession(_session);
			try
			{
				cmd.execute();
                
                
				final String msg = 
                    s_stringMgr.getString("DumpSessionAction.success",
                                          outFile.getAbsolutePath()); 

                _session.showMessage(msg);
			}
			catch (Throwable ex)
			{
			    
                final String msg = 
                    s_stringMgr.getString("DumpSessionAction.error", ex);
				_session.showErrorMessage(msg);
				s_log.error(msg, ex);
			}
		}
	}
}
