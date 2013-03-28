package net.sourceforge.squirrel_sql.client.gui.db;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;

abstract class BaseListInternalFrame extends BaseInternalFrame
{
	protected interface IUserInterfaceFactory
	{
		ToolBar getToolBar();
		BasePopupMenu getPopupMenu();
		JList getList();
		String getWindowTitle();
		ICommand getDoubleClickCommand();
		void enableDisableActions();
	}

	
	private static ILogger s_log =
			LoggerController.createLogger(BaseListInternalFrame.class);

	private IUserInterfaceFactory _uiFactory;

	
	private BasePopupMenu _popupMenu;

	
	private ToolBar _toolBar;

	private boolean _hasBeenBuilt;

	private boolean _hasBeenSized = false;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(BaseListInternalFrame.class);
    
	public BaseListInternalFrame(IUserInterfaceFactory uiFactory)
	{
		super("", true, true);
		if (uiFactory == null)
		{
			throw new IllegalArgumentException("Null IUserInterfaceFactory passed");
		}
		_uiFactory = uiFactory;

		createUserInterface();
	}

	public void updateUI()
	{
		super.updateUI();
		if (_hasBeenBuilt)
		{
			_hasBeenSized = false;
			privateResize();
		}
	}

	protected IUserInterfaceFactory getUserInterfaceFactory()
	{
		return _uiFactory;
	}

	protected void setToolBar(ToolBar tb)
	{
		final Container content = getContentPane();
		if (_toolBar != null)
		{
			content.remove(_toolBar);
		}
		if (tb != null)
		{
			content.add(tb, BorderLayout.NORTH);
		}
		_toolBar = tb;
	}

	
	private void mousePress(MouseEvent evt)
	{
		if (evt.isPopupTrigger())
		{
			if (_popupMenu == null)
			{
				_popupMenu = _uiFactory.getPopupMenu();
			}
			_popupMenu.show(evt);
		}
	}

	private void privateResize()
	{
		if (!_hasBeenSized)
		{
			if (_toolBar != null)
			{
				_hasBeenSized = true;
				Dimension windowSize = getSize();
				int rqdWidth = _toolBar.getPreferredSize().width + 15;
				if (rqdWidth > windowSize.width)
				{
					windowSize.width = rqdWidth;
					setSize(windowSize);
				}
			}
		}
	}

	private void createUserInterface()
	{
		
		GUIUtils.makeToolWindow(this, true);

		setDefaultCloseOperation(HIDE_ON_CLOSE);

		
		final Container content = getContentPane();
		content.setLayout(new BorderLayout());

		String winTitle = _uiFactory.getWindowTitle();
		if (winTitle != null)
		{
			setTitle(winTitle);
		}

		
		setToolBar(_uiFactory.getToolBar());

		
		final JList list = _uiFactory.getList();

		
		final JScrollPane sp = new JScrollPane();
		sp.setViewportView(list);
		sp.setPreferredSize(new Dimension(100, 100));

		
		content.add(sp, BorderLayout.CENTER);

		
		list.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				mousePress(evt);
			}
			public void mouseReleased(MouseEvent evt)
			{
				mousePress(evt);
			}
		});

		
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					ICommand cmd = _uiFactory.getDoubleClickCommand();
					if (cmd != null)
					{
						try
						{
							cmd.execute();
						}
						catch (BaseException ex)
						{
                            
							s_log.error(s_stringMgr.getString("BaseListInternalFrame.error.execdoubleclick"), ex);
						}
					}
				}
			}
		});

		
		list.getModel().addListDataListener(new ListDataListener()
		{
			public void intervalAdded(ListDataEvent evt)
			{
				list.setSelectedIndex(evt.getIndex0()); 
				_uiFactory.enableDisableActions();
			}
			public void intervalRemoved(ListDataEvent evt)
			{
				int nextIdx = evt.getIndex0();
				int lastIdx = list.getModel().getSize() - 1;
				if (nextIdx > lastIdx)
				{
					nextIdx = lastIdx;
				}
				list.setSelectedIndex(nextIdx);
				_uiFactory.enableDisableActions();
			}
			public void contentsChanged(ListDataEvent evt)
			{
				
			}
		});

		
		
		
		
		addInternalFrameListener(new InternalFrameAdapter()
		{
			
			
			
				
				
				
				
				
				
			

			public void internalFrameOpened(InternalFrameEvent evt)
			{
				privateResize();
			}

		});

		validate();

	}
}
