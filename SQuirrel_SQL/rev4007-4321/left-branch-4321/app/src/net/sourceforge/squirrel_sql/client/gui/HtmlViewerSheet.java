package net.sourceforge.squirrel_sql.client.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

public class HtmlViewerSheet extends JFrame
{
	
	private final IApplication _app;

	
	private URL _documentURL;

 	public HtmlViewerSheet(IApplication app, String title)
	{
		this(app, title, null);
	}

 	public HtmlViewerSheet(IApplication app, String title, URL url)
	{
		super(title);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		_app = app;
		_documentURL = url;
		createGUI();
	}

	
	public URL getURL()
	{
		return _documentURL;
	}

	
	private void createGUI() 
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new HtmlViewerPanel(_documentURL), BorderLayout.CENTER);
		final SquirrelResources rsrc = _app.getResources();
		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);
		if (icon != null)
		{
			setIconImage(icon.getImage());
		}
		pack();
	}
}

