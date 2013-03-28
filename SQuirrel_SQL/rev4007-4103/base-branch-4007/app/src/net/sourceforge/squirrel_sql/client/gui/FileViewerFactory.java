package net.sourceforge.squirrel_sql.client.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;

public class FileViewerFactory
{
	
	private static final FileViewerFactory s_instance = new FileViewerFactory();

	
	private final HashMap<String, HtmlViewerSheet> _sheets = 
        new HashMap<String, HtmlViewerSheet>();

	
	private MyInternalFrameListener _lis = new MyInternalFrameListener();

	
	private FileViewerFactory()
	{
		super();
	}

	
	public static FileViewerFactory getInstance()
	{
		return s_instance;
	}

	
	public synchronized HtmlViewerSheet getViewer(MainFrame parent, URL url)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException("MainFrame == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("URL == null");
		}

		HtmlViewerSheet viewer = _sheets.get(url.toString());
		if (viewer == null)
		{
			viewer = new HtmlViewerSheet(parent.getApplication(),
											url.toString(), url);

			viewer.addWindowListener(_lis);
			viewer.setSize(600, 400);


			GUIUtils.centerWithinParent(viewer);
			_sheets.put(url.toString(), viewer);
		}
		return viewer;
	}

	public synchronized void closeAllViewers()
	{
		final Map<String, HtmlViewerSheet> viewers = 
            new HashMap<String, HtmlViewerSheet>(_sheets);
		final Iterator<HtmlViewerSheet> it = viewers.values().iterator();
		while (it.hasNext())
		{
			final HtmlViewerSheet v = it.next();
			removeViewer(v);
			v.dispose();
		}
	}

	private synchronized void removeViewer(HtmlViewerSheet viewer)
	{
		
		viewer.removeWindowListener(_lis);
		_sheets.remove(viewer.getURL().toString());
	}


	private final class MyInternalFrameListener extends WindowAdapter
	{
		

		public void windowClosed(WindowEvent evt)
		{

			removeViewer((HtmlViewerSheet)evt.getWindow());

		}
	}
    
}
