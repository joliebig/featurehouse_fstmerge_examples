package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PropertyPanel extends JPanel
{
	private final GridBagLayout _layout = new GridBagLayout();
	private boolean _singleColumn = true;
	private int _nbrComponents;
	
	private int _lastY;

	public PropertyPanel()
	{
		super();
		setLayout(_layout);
	}

	public void setSingleColumn(boolean value)
	{
		_singleColumn = value;
	}

	public void add(JLabel label, Component data)
	{
		add(label, data, null);
	}

	public void add(JLabel label, Component data, Component extra)
	{
		label.setLabelFor(data);
		pvtAdd(label, data, extra);
	}

	public void add(JLabel leftLabel, JLabel rightlabel)
	{
		pvtAdd(leftLabel, rightlabel, null);
	}

	public void add(Component left, Component right)
	{
		pvtAdd(left, right, null);
	}

	public void add(Component left, Component right, Component extra)
	{
		pvtAdd(left, right, extra);
	}

	private void pvtAdd(
		Component leftComp,
		Component rightComp,
		Component extra)
	{
		final boolean isOdd = ++_nbrComponents % 2 != 0;
		final GridBagConstraints cons = new GridBagConstraints();
		if (_singleColumn || isOdd)
		{
			cons.gridy = ++_lastY;
		}
		else
		{
			cons.gridy = _lastY;
		}
		cons.gridheight = 1;
		cons.gridwidth = 1;
		cons.insets = new Insets(4, 4, 4, 4);
		cons.fill = GridBagConstraints.BOTH;

		if (_singleColumn || isOdd)
		{
			cons.gridx = 0;
		}
		else
		{
			cons.gridx = 3;
		}
		cons.weightx = 0.0f;
		_layout.setConstraints(leftComp, cons);
		add(leftComp);

		++cons.gridx;
		cons.weightx = 1.0f;
		if (extra != null)
		{
			Box box = Box.createHorizontalBox();
			box.add(rightComp);
			box.add(extra);
			_layout.setConstraints(box, cons);
			add(box);
		}
		else
		{
			_layout.setConstraints(rightComp, cons);
			add(rightComp);
		}
	}
}
