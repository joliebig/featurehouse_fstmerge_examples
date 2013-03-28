
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;


public interface InstallStatusEventFactory
{

	
	InstallStatusEvent create(InstallEventType installEventType);

}