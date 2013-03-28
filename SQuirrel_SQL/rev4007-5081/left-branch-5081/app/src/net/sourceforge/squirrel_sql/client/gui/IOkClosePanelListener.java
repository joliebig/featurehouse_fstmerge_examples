package net.sourceforge.squirrel_sql.client.gui;

import java.util.EventListener;

public interface IOkClosePanelListener extends EventListener
{
	
	void okPressed(OkClosePanelEvent evt);

	
	void closePressed(OkClosePanelEvent evt);

	
	void cancelPressed(OkClosePanelEvent evt);
}
