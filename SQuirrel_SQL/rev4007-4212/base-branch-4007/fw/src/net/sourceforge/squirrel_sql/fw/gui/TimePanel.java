package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.border.Border;

public class TimePanel extends JLabel implements ActionListener
{
	
	private Timer _timer;

	
	private DateFormat _fmt = DateFormat.getTimeInstance(DateFormat.LONG);
	private Dimension _prefSize;
	private Calendar _calendar = Calendar.getInstance();

	
	public TimePanel()
	{
		super("", JLabel.CENTER);
	}

	
	public void addNotify()
	{
		super.addNotify();
		_timer = new Timer(1000, this);
		_timer.start();
	}

	
	public void removeNotify()
	{
		super.removeNotify();
		if (_timer != null)
		{
			_timer.stop();
			_timer = null;
		}
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		_calendar.setTimeInMillis(System.currentTimeMillis());
		setText(_fmt.format(_calendar.getTime()));
	}

	
	public Dimension getPreferredSize()
	{
		if(null == _prefSize)
		{
			
			
			_prefSize = new Dimension();
			_prefSize.height = 20;
			FontMetrics fm = getFontMetrics(getFont());
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			_prefSize.width = fm.stringWidth(_fmt.format(cal.getTime()));
			Border border = getBorder();
			if (border != null)
			{
				Insets ins = border.getBorderInsets(this);
				if (ins != null)
				{
					_prefSize.width += (ins.left + ins.right);
				}
			}
			Insets ins = getInsets();
			if (ins != null)
			{
				_prefSize.width += (ins.left + ins.right) + 20;
			}
		}
		return _prefSize;
	}
}
