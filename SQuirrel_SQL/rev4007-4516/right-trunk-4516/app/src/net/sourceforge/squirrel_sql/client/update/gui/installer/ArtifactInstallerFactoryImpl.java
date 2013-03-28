
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.FileNotFoundException;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ArtifactInstallerFactoryImpl implements ArtifactInstallerFactory, ApplicationContextAware
{
	private final static String ARTIFACT_INSTALLER_ID = 
		"net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller";
	
	private UpdateUtil updateUtil;

	
	private ApplicationContext applicationContext = null;
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}	
	
	
	public ArtifactInstaller create(ChangeListXmlBean changeList) throws FileNotFoundException
	{
		ArtifactInstaller result = 
			(ArtifactInstaller) applicationContext.getBean(ARTIFACT_INSTALLER_ID);
		result.setChangeList(changeList);
		return result;
	}

	
	public ArtifactInstaller create(FileWrapper changeList) throws FileNotFoundException
	{
		ChangeListXmlBean changeListBean = updateUtil.getChangeList(changeList);
		ArtifactInstaller result = create(changeListBean);
		result.setChangeListFile(changeList);
		return result;
	}

	
	public void setUpdateUtil(UpdateUtil updateUtil)
	{
		this.updateUtil = updateUtil;
	}


}
