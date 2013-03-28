package net.sourceforge.squirrel_sql.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.event.EventListenerList;

import com.jgoodies.forms.factories.ButtonBarFactory;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class OkClosePanel extends JPanel
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OkClosePanel.class);

	private boolean _executingMode;

	
	private EventListenerList _listenerList = new EventListenerList();

	private JButton _okBtn;
	private JButton _closeBtn = new JButton(s_stringMgr.getString("OkClosePanel.close"));

	public OkClosePanel()
	{
		super();
		createGUI(s_stringMgr.getString("OkClosePanel.ok"));
	}

	public OkClosePanel(String okButtonText)
	{
		super();
		createGUI(okButtonText != null ? okButtonText : s_stringMgr.getString("OkClosePanel.ok"));
	}

	
	public void setExecuting(boolean executingMode)
	{
		if (executingMode != _executingMode)
		{
			_executingMode = executingMode;
			_okBtn.setEnabled(!executingMode);
			_closeBtn.setText(executingMode ? s_stringMgr.getString("OkClosePanel.cancel") : s_stringMgr.getString("OkClosePanel.close"));
			if (!executingMode)
			{
				_closeBtn.setEnabled(true);
			}
		}
	}

	
	public void enableCloseButton(boolean enable)
	{
		_closeBtn.setEnabled(enable);
	}

	
	public synchronized void addListener(IOkClosePanelListener lis)
	{
		_listenerList.add(IOkClosePanelListener.class, lis);
	}

	
	public synchronized void makeOKButtonDefault()
			throws IllegalStateException
	{
		JRootPane root = getRootPane();
		if (root == null)
		{
			throw new IllegalStateException("Null RootPane so cannot set default button");
		}

	}

	public JButton getCloseButton()
	{
		return _closeBtn;
	}

	public JButton getOKButton()
	{
		return _okBtn;
	}

	private void fireButtonPressed(JButton btn)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		OkClosePanelEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IOkClosePanelListener.class)
			{
				
				if (evt == null)
				{
					evt = new OkClosePanelEvent(this);
				}
				IOkClosePanelListener lis = (IOkClosePanelListener)listeners[i + 1];
				if (btn == _okBtn)
				{
					lis.okPressed(evt);
				}
				else if (_executingMode)
				{
					lis.cancelPressed(evt);
				}
				else
				{
					lis.closePressed(evt);
				}
			}
		}
	}

	private void createGUI(String okButtonText)
	{
		_okBtn = new JButton(okButtonText);

		JPanel pnl = ButtonBarFactory.buildOKCancelBar(_okBtn, _closeBtn);
		add(pnl);


		_okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fireButtonPressed(_okBtn);
			}
		});
		_closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fireButtonPressed(_closeBtn);
			}
		});


	}
}
