package net.sourceforge.squirrel_sql.client.gui.builders;

import java.util.EventListener;

public interface IUIFactoryListener extends EventListener
{
	
	void tabbedPaneCreated(UIFactoryComponentCreatedEvent evt);
}
