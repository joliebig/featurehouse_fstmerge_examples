package net.sourceforge.squirrel_sql.client.gui;

import java.util.EventObject;

public class HtmlViewerPanelListenerEvent extends EventObject
{
	
	private HtmlViewerPanel _pnl;
	
	HtmlViewerPanelListenerEvent(HtmlViewerPanel source)
	{
		super(checkParams(source));
		_pnl = source;
	}
	
	public HtmlViewerPanel getHtmlViewerPanel()
	{
		return _pnl;
	}
	private static HtmlViewerPanel checkParams(HtmlViewerPanel source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("HtmlViewerPanel == null");
		}
		return source;
	}
}