package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.JTextArea;
import javax.swing.LookAndFeel;

public class MultipleLineLabel extends JTextArea
{
	public MultipleLineLabel()
	{
		this("");
	}

	public MultipleLineLabel(String title)
	{
		super();
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setText(title);
	}

	public void updateUI()
	{
		
		
		
		LookAndFeel.installBorder(this, "Label.border");
		LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground",
											"Label.font");
		super.updateUI();
		LookAndFeel.installColorsAndFont(this, "Label.background", "Label.foreground",
											"Label.font");
	}
}
