package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Dimension;

import javax.swing.JLabel;

public class OutputLabel extends JLabel
{
	
	public static final int PREF_WIDTH = 200;

	
	public OutputLabel()
	{
		super();
		commonCtor();
	}

	
	public OutputLabel(String text)
	{
		super(text);
		commonCtor();
		setToolTipText(text);
	}

	
	public void setText(String text)
	{
		super.setText(text);
		setToolTipText(text);
	}

	
	private void commonCtor()
	{
		Dimension ps = getPreferredSize();
		ps.width = PREF_WIDTH;
		setPreferredSize(ps);
	}
}
