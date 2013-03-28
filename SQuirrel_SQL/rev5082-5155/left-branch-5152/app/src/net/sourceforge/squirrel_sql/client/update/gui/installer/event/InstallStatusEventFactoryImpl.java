
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;


public class InstallStatusEventFactoryImpl implements InstallStatusEventFactory
{
	
	public InstallStatusEvent create(InstallEventType installEventType) {
		return new InstallStatusEvent(installEventType);
	}
}
