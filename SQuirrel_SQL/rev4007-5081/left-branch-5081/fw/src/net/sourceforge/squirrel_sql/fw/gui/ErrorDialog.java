package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.*;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class ErrorDialog extends JDialog
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ErrorDialog.class);

	private interface IStringKeys
	{
		String ERROR = "ErrorDialog.error";
		String UNKNOWN_ERROR = "ErrorDialog.unknownerror";
	}

	
	private JButton _closeBtn;

	
	private JButton _stackTraceBtn;

	
	private JButton _moreBtn;

	
	private JScrollPane _stackTraceScroller;

	
	private JScrollPane _moreErrorsScroller;

	
	private ActionListener _stackTraceHandler = new StackTraceButtonHandler();

	
	private ActionListener _closeHandler = new CloseButtonHandler();

	
	private ActionListener _moreHandler = new MoreButtonHandler();

	public ErrorDialog(Throwable th)
	{
		this((Frame) null, th);
	}

	public ErrorDialog(Frame owner, Throwable th)
	{
		super(owner, s_stringMgr.getString(IStringKeys.ERROR), true);
		createUserInterface(null, th);
	}

	public ErrorDialog(Dialog owner, Throwable th)
	{
		super(owner, s_stringMgr.getString(IStringKeys.ERROR), true);
		createUserInterface(null, th);
	}

	public ErrorDialog(Frame owner, String msg)
	{
		super(owner, s_stringMgr.getString(IStringKeys.ERROR), true);
		createUserInterface(msg, null);
	}

	public ErrorDialog(Frame owner, String msg, Throwable th)
	{
		super(owner, s_stringMgr.getString(IStringKeys.ERROR), true);
		createUserInterface(msg, th);
	}

	public ErrorDialog(Dialog owner, String msg)
	{
		super(owner, s_stringMgr.getString(IStringKeys.ERROR), true);
		createUserInterface(msg, null);
	}

	
	public void dispose()
	{
		if (_closeBtn != null && _closeHandler != null)
		{
			_closeBtn.removeActionListener(_closeHandler);
		}
		if (_stackTraceBtn != null && _stackTraceHandler != null)
		{
			_stackTraceBtn.removeActionListener(_stackTraceHandler);
		}
		if (_moreBtn != null && _moreHandler != null)
		{
			_moreBtn.removeActionListener(_moreHandler);
		}
		super.dispose();
	}

	
	private void createUserInterface(String msg, Throwable th)
	{
		if (msg == null || msg.length() == 0)
		{
			if (th != null)
			{
				msg = th.getMessage();
				if (msg == null || msg.length() == 0)
				{
					msg = th.toString();
				}
			}
		}
		if (msg == null || msg.length() == 0)
		{
			msg = s_stringMgr.getString(IStringKeys.UNKNOWN_ERROR);
		}

		_stackTraceScroller = new JScrollPane(new StackTracePanel(th));
		_stackTraceScroller.setVisible(false);

		final MoreErrorsPanel moreErrPnl = createMoreErrorsPanel(th);
		if (moreErrPnl != null)
		{
			_moreErrorsScroller = new JScrollPane(moreErrPnl);
			_moreErrorsScroller.setVisible(false);
		}

		Container content = getContentPane();
		content.setLayout(new GridBagLayout());

		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
		content.add(createMessagePanel(msg, th), gbc);

		gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5),0,0);
		content.add(createButtonsPanel(th), gbc);

		gbc = new GridBagConstraints(0,2,1,1,3,3,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
		content.add(_stackTraceScroller, gbc);

		if (_moreErrorsScroller != null)
		{
			gbc = new GridBagConstraints(0,2,1,1,3,3,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
			content.add(_moreErrorsScroller, gbc);
		}


		getRootPane().setDefaultButton(_closeBtn);

		if(null == th)
		{
			setSize(400,200);
		}
		else
		{
			setSize(400,500);
		}

		AbstractAction closeAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				setVisible(false);
				dispose();
			}
		};
		KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
		getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", closeAction);
		

		GUIUtils.centerWithinParent(ErrorDialog.this);
	}

	
	private JComponent createMessagePanel(String msg, Throwable th)
	{
		if (msg == null || msg.length() == 0)
		{
			if (th != null)
			{
				msg = th.getMessage();
				if (msg == null || msg.length() == 0)
				{
					msg = th.toString();
				}
			}
		}
		if (msg == null || msg.length() == 0)
		{
			msg = s_stringMgr.getString(IStringKeys.UNKNOWN_ERROR);
		}
		JScrollPane sp = new JScrollPane(new MessagePanel(msg));




		return sp;
	}

	
	private JPanel createButtonsPanel(Throwable th)
	{
		JPanel btnsPnl = new JPanel();
		if (th != null)
		{
			_stackTraceBtn = new JButton(s_stringMgr.getString("ErrorDialog.stacktrace"));
			_stackTraceBtn.addActionListener(_stackTraceHandler);
			btnsPnl.add(_stackTraceBtn);
			if (_moreErrorsScroller != null)
			{
				_moreBtn = new JButton(s_stringMgr.getString("ErrorDialog.more"));
				_moreBtn.addActionListener(_moreHandler);
				btnsPnl.add(_moreBtn);
			}
		}
		_closeBtn = new JButton(s_stringMgr.getString("ErrorDialog.close"));
		_closeBtn.addActionListener(_closeHandler);
		btnsPnl.add(_closeBtn);

		return btnsPnl;
	}

	private static Color getTextAreaBackgroundColor()
	{
		return (Color)UIManager.get("TextArea.background");
	}

	private MoreErrorsPanel createMoreErrorsPanel(Throwable th)
	{
		if (th instanceof SQLException)
		{
			SQLException ex = ((SQLException)th).getNextException();
			if (ex != null)
			{
				return new MoreErrorsPanel(ex);
			}
		}
		return null;
	}

	
	private final class MessagePanel extends MultipleLineLabel
	{
		MessagePanel(String msg)
		{
			super();
			setText(msg);
			setBackground(ErrorDialog.getTextAreaBackgroundColor());




		}
	}

	
	private final class StackTracePanel extends MultipleLineLabel
	{
		StackTracePanel(Throwable th)
		{
			super();
			setBackground(ErrorDialog.getTextAreaBackgroundColor());
			if (th != null)
			{
				setText(Utilities.getStackTrace(th));
			}
		}
	}

	private final class MoreErrorsPanel extends MultipleLineLabel
	{
		MoreErrorsPanel(SQLException ex)
		{
			super();
			StringBuffer buf = new StringBuffer();
			setBackground(ErrorDialog.getTextAreaBackgroundColor());
			while (ex != null)
			{
				String msg = ex.getMessage();
				if (msg != null && msg.length() > 0)
				{
					buf.append(msg).append('\n');
				}
				else
				{
					buf.append(ex.toString()).append('\n');
				}
				ex = ex.getNextException();
			}
			setText(buf.toString());
		}
	}

	
	private final class CloseButtonHandler implements ActionListener
	{
		
		public void actionPerformed(ActionEvent evt)
		{
			ErrorDialog.this.dispose();
		}

	}

	
	private final class StackTraceButtonHandler implements ActionListener
	{
		
		public void actionPerformed(ActionEvent evt)
		{
			boolean currentlyVisible = _stackTraceScroller.isVisible();
			if (!currentlyVisible)
			{
				if (_moreErrorsScroller != null)
				{
					_moreErrorsScroller.setVisible(false);
				}
			}
			_stackTraceScroller.setVisible(!currentlyVisible);
			ErrorDialog.this.validate();
		}
	}

	
	private final class MoreButtonHandler implements ActionListener
	{
		
		public void actionPerformed(ActionEvent evt)
		{
			boolean currentlyVisible = _moreErrorsScroller.isVisible();
			if (!currentlyVisible)
			{
				_stackTraceScroller.setVisible(false);
			}
			_moreErrorsScroller.setVisible(!currentlyVisible);
			ErrorDialog.this.validate();
		}
	}
}
