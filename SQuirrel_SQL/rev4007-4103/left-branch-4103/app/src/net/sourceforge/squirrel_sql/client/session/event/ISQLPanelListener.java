package net.sourceforge.squirrel_sql.client.session.event;

import java.util.EventListener;

public interface ISQLPanelListener extends EventListener
{
	
	void sqlEntryAreaInstalled(SQLPanelEvent evt);

   void sqlEntryAreaClosed(SQLPanelEvent evt);
}