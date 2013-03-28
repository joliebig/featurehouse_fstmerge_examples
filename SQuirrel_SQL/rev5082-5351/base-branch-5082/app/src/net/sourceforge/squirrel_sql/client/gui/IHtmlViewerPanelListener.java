package net.sourceforge.squirrel_sql.client.gui;

import java.util.EventListener;

public interface IHtmlViewerPanelListener extends EventListener
{
	
	void currentURLHasChanged(HtmlViewerPanelListenerEvent evt);

	
	void homeURLHasChanged(HtmlViewerPanelListenerEvent evt);
}
