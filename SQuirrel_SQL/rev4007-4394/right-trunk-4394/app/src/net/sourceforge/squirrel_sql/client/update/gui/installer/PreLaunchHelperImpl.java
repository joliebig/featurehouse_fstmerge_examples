
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListenerImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class PreLaunchHelperImpl implements PreLaunchHelper
{

	
	private String MESSAGE;

	
	private String TITLE;

	
	private StringManager s_stringMgr;

	
	private ILogger s_log;
	
	
	
	
	private UpdateUtil updateUtil = null;
	public void setUpdateUtil(UpdateUtil util) { this.updateUtil = util; }
	
		
	private ArtifactInstallerFactory artifactInstallerFactory = null;
	public void setArtifactInstallerFactory(ArtifactInstallerFactory artifactInstallerFactory)
	{
		this.artifactInstallerFactory = artifactInstallerFactory;
	}

	
	
	public PreLaunchHelperImpl() throws IOException {
		
		
		s_log = LoggerController.createLogger(PreLaunchHelperImpl.class);
		
		s_stringMgr = StringManagerFactory.getStringManager(PreLaunchHelperImpl.class);

		
		
		MESSAGE = s_stringMgr.getString("PreLaunchHelperImpl.message");

		
		TITLE = s_stringMgr.getString("PreLaunchHelperImpl.title");

	}
	
	
	public void installUpdates(boolean prompt)
	{
		try
		{
			File changeListFile = updateUtil.getChangeListFile();
			if (changeListFile.exists())
			{
				if (s_log.isInfoEnabled())
				{
					s_log.info("Pre-launch update app detected a changeListFile to be processed");
				}
				if (prompt)
				{
					if (showConfirmDialog())
					{
						installUpdates(changeListFile);
					} else
					{
						if (s_log.isInfoEnabled())
						{
							s_log.info("User cancelled update installation");
						}
					}
				} else
				{
					installUpdates(changeListFile);
				}
			} else {
				if (s_log.isInfoEnabled())
				{
					s_log.info("installUpdates: changeList file ("+changeListFile+") doesn't exist.");
				}				
			}
		} catch (Throwable e)
		{
			s_log.error("Unexpected error while attempting to install updates: " + e.getMessage(), e);
		} finally
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("Pre-launch update app finished");
			}
			LoggerController.shutdown();
			System.exit(0);
		}
	}

		
	













	
	
	private void installUpdates(File changeList) throws Exception
	{
		ArtifactInstaller installer = artifactInstallerFactory.create(changeList);
		installer.addListener(new InstallStatusListenerImpl());
		if (installer.backupFiles()) {
			installer.installFiles();
		} else {
			
		}
	}
	
	
	private boolean showConfirmDialog()
	{
		int choice =
			JOptionPane.showConfirmDialog(null,
				MESSAGE,
				TITLE,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return choice == JOptionPane.YES_OPTION;
	}
	
}
