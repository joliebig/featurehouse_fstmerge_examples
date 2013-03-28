package net.sourceforge.squirrel_sql.fw.completion;

import javax.swing.*;
import java.awt.event.*;


public class CompletionFocusHandler
{
	private TextComponentProvider _txtComp;
	private JList _completionList;
	private FocusListener _completionFocusListener;
	private Timer _timer;
	private ActionListener _timerActionListener;
	private FocusEvent _lastFocusEvent;


	public CompletionFocusHandler(TextComponentProvider txtComp, JList completionList)
	{
		_txtComp = txtComp;
		_completionList = completionList;

		_timerActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onTimerAction();
			}
		};

		_timer = new Timer(200, null);
		_timer.setRepeats(false);


		_completionList.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				
				onCompletionListFocusGained(e);
				onFocusGained(e);
			}

			public void focusLost(FocusEvent e)
			{
				
				onFocusLost(e);
			}
		});

		if (_txtComp.editorEqualsFilter())
		{
			_txtComp.getEditor().addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent e)
				{
					
					onFocusGained(e);
				}

				public void focusLost(FocusEvent e)
				{
					
					onFocusLost(e);
				}
			});
		}
		else
		{
			_txtComp.getFilter().addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent e)
				{
					
					onFocusGained(e);
				}

				public void focusLost(FocusEvent e)
				{
					
					onFocusLost(e);
				}
			});
		}

	}

	private void onFocusGained(FocusEvent e)
	{
		
		
		_timer.stop();
		_lastFocusEvent = e;
	}

	private void onFocusLost(FocusEvent e)
	{
		
		
		_lastFocusEvent = e;
		_timer.start();
	}

	private void onTimerAction()
	{
		_timer.stop();
		if (null != _completionFocusListener)
		{
			
			_completionFocusListener.focusLost(_lastFocusEvent);
		}
	}


	
	private void onCompletionListFocusGained(FocusEvent e)
	{
		if (false == e.isTemporary() && false == _txtComp.editorEqualsFilter())
		{
			_txtComp.getFilter().requestFocusInWindow();
		}
	}


	public void setFocusListener(FocusListener completionFocusListener)
	{
		_completionFocusListener = completionFocusListener;

		_timer.removeActionListener(_timerActionListener);
		if (null != _completionFocusListener)
		{
			_timer.addActionListener(_timerActionListener);
		}
	}
}
