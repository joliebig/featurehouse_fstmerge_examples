package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.IOptionPanel;

public interface ISessionPropertiesPanel extends IOptionPanel
{
	
	void initialize(IApplication app, ISession session);
}
