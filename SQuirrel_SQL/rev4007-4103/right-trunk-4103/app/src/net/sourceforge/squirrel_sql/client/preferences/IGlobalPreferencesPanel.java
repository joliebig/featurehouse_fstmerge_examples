package net.sourceforge.squirrel_sql.client.preferences;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.IOptionPanel;

public interface IGlobalPreferencesPanel extends IOptionPanel
{
	void initialize(IApplication app);

   void uninitialize(IApplication app);
}
