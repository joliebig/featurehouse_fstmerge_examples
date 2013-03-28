package net.sourceforge.squirrel_sql.client.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class HtmlViewerPanel extends JPanel
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(HtmlViewerPanel.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(HtmlViewerPanel.class);    
    
	
	private final JEditorPane _contentsTxt = new JEditorPane();

	
	private JScrollPane _contentsScrollPane;

	
	private URL _homeURL;

	
	private URL _currentURL;

	
	private final List<URL> _history = new LinkedList<URL>();

	
	private int _historyIndex = -1;

	
	private EventListenerList _listenerList = new EventListenerList();

	
	public HtmlViewerPanel(URL url)
	{
		super();
		createGUI();
		setHomeURL(url);
		setURL(url);
	}

	
	public URL getURL()
	{
		return _currentURL;
	}

	
	public URL getHomeURL()
	{
		return _homeURL;
	}

	
	public void setHomeURL(URL homeURL)
	{
		_homeURL = homeURL;
		fireHomeURLChanged();
	}

	
	public void addListener(IHtmlViewerPanelListener lis)
	{
		_listenerList.add(IHtmlViewerPanelListener.class, lis);
	}

	
	void removeListener(IHtmlViewerPanelListener lis)
	{
		_listenerList.remove(IHtmlViewerPanelListener.class, lis);
	}

	public synchronized void gotoURL(URL url) throws IOException
	{
		if (url == null)
		{
			throw new IllegalArgumentException("URL == null");
		}
		if (!url.equals(_currentURL))
		{
			ListIterator<URL> it = _history.listIterator(_historyIndex + 1);
			while (it.hasNext())
			{
				it.next();
				it.remove();
			}
			_history.add(url);
			_historyIndex = _history.size() - 1;
			_contentsTxt.setPage(url);
			_currentURL = url;
			fireURLChanged();
		}
	}

	public synchronized void goBack()
	{
		if (_historyIndex > 0 && _historyIndex < _history.size())
		{
			displayURL(_history.get(--_historyIndex));
		}
	}

	public synchronized void goForward()
	{
		if (_historyIndex > -1 && _historyIndex < _history.size() - 1)
		{
			displayURL(_history.get(++_historyIndex));
		}
	}

	public synchronized void goHome()
	{
		_historyIndex = 0;
		displayURL(_homeURL);
	}

	public void refreshPage()
	{
		final Point pos = _contentsScrollPane.getViewport().getViewPosition();
		displayURL(_currentURL);
		_contentsScrollPane.getViewport().setViewPosition(pos);
	}

	
	private synchronized void setURL(URL url)
	{
		if (url != null)
		{
			CursorChanger cursorChg = new CursorChanger(this);
			cursorChg.show();
			try
			{
				
				
				displayURL(url);
				_history.add(url);
				_historyIndex = 0;
			}
			finally
			{
				cursorChg.restore();
			}
		}
	}

	
	private void displayURL(URL url)
	{
		if (url != null)
		{
			try
			{
				_contentsTxt.setPage(url);
				_currentURL = url;
				fireURLChanged();
			}
			catch (Exception ex)
			{
                
				s_log.error(s_stringMgr.getString("HtmlViewerPanel.error.displayurl"), ex);
			}
		}
	}

	
	private void fireURLChanged()
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		HtmlViewerPanelListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IHtmlViewerPanelListener.class)
			{
				
				if (evt == null)
				{
					evt = new HtmlViewerPanelListenerEvent(this);
				}
				((IHtmlViewerPanelListener)listeners[i + 1]).currentURLHasChanged(evt);
			}
		}
	}

	
	private void fireHomeURLChanged()
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		HtmlViewerPanelListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IHtmlViewerPanelListener.class)
			{
				
				if (evt == null)
				{
					evt = new HtmlViewerPanelListenerEvent(this);
				}
				((IHtmlViewerPanelListener)listeners[i + 1]).homeURLHasChanged(evt);
			}
		}
	}

	
	private void createGUI()
	{
		setLayout(new BorderLayout());
		add(createMainPanel(), BorderLayout.CENTER);
	}

	
	private JPanel createMainPanel()
	{
		_contentsTxt.setEditable(false);
		_contentsTxt.setContentType("text/html");
		final TextPopupMenu pop = new TextPopupMenu();
		pop.setTextComponent(_contentsTxt);
		_contentsTxt.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
			public void mouseReleased(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		final JPanel pnl = new JPanel(new BorderLayout());
		_contentsTxt.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		_contentsTxt.addHyperlinkListener(createHyperLinkListener());
		_contentsScrollPane = new JScrollPane(_contentsTxt,
									JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pnl.add(_contentsScrollPane, BorderLayout.CENTER);

		return pnl;
	}

	private HyperlinkListener createHyperLinkListener()
	{
		return new HyperlinkListener()
		{
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					if (e instanceof HTMLFrameHyperlinkEvent)
					{
						((HTMLDocument)_contentsTxt.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
					}
					else
					{
						try
						{
							gotoURL(e.getURL());
						}
						catch (IOException ex)
						{
                            
							s_log.error(s_stringMgr.getString("HtmlViewerPanel.error.processhyperlink"), ex);
						}
					}
				}
			}
		};
	}
}
