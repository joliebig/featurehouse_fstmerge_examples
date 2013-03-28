
package net.sourceforge.squirrel_sql.client.update.gui.installer;


import net.sourceforge.squirrel_sql.client.ApplicationArguments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({})
@ContextConfiguration (locations={
	"/net/sourceforge/squirrel_sql/client/update/gui/installer/net.sourceforge.squirrel_sql.client.update.gui.installer.applicationContext.xml",
	"/net/sourceforge/squirrel_sql/client/update/gui/installer/event/net.sourceforge.squirrel_sql.client.update.gui.installer.event.applicationContext.xml",
	"/net/sourceforge/squirrel_sql/client/update/gui/installer/util/net.sourceforge.squirrel_sql.client.update.gui.installer.util.applicationContext.xml",
	"/net/sourceforge/squirrel_sql/client/update/util/net.sourceforge.squirrel_sql.client.update.util.applicationContext.xml"	
})
public class ArtifactInstallerImplIntegrationTest extends AbstractJUnit4SpringContextTests
{	

	static {
		ApplicationArguments.initialize(new String[] {});
	}
	
	public static final String beanIdToTest = 
		"net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller";
	
	@Test
	public void testLoadBean() throws Exception {
		super.applicationContext.getBean(beanIdToTest);
	}
	
}
