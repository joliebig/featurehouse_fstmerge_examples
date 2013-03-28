package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class StatusBar extends JPanel
{
	
	private String _msgWhenEmpty = " ";

	
	private final JLabel _textLbl = new JLabel();

   private final JProgressBar _progressBar = new JProgressBar();

   private final JPanel _pnlLabelOrProgress = new JPanel();

   
	private final GridBagConstraints _gbc = new GridBagConstraints();

	private Font _font;

	
	public StatusBar()
	{
		super(new GridBagLayout());
		createGUI();
	}

	
	public synchronized void setFont(Font font)
	{
		if (font == null)
		{
			throw new IllegalArgumentException("Font == null");
		}
		super.setFont(font);
		_font = font;
		updateSubcomponentsFont(this);
	}

	
	public synchronized void setText(String text)
	{
		String myText = null;
		if (text != null)
		{
			myText = text.trim();
		}
		if (myText != null && myText.length() > 0)
		{
			_textLbl.setText(myText);
		}
		else
		{
			clearText();
		}
	}

    
    public synchronized String getText() {
        return _textLbl.getText();
    }
    
	public synchronized void clearText()
	{
		_textLbl.setText(_msgWhenEmpty);
	}

	public synchronized void setTextWhenEmpty(String value)
	{
		final boolean wasEmpty = _textLbl.getText().equals(_msgWhenEmpty);
		if (value != null && value.length() > 0)
		{
			_msgWhenEmpty = value;
		}
		else
		{
			_msgWhenEmpty = " ";
		}
		if (wasEmpty)
		{
			clearText();
		}
	}

	public synchronized void addJComponent(JComponent comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("JComponent == null");
		}
		comp.setBorder(createComponentBorder());
		if (_font != null)
		{
			comp.setFont(_font);
			updateSubcomponentsFont(comp);
		}
		super.add(comp, _gbc);
	}

	public static Border createComponentBorder()
	{
		return BorderFactory.createCompoundBorder(
			BorderFactory.createBevelBorder(BevelBorder.LOWERED),
			BorderFactory.createEmptyBorder(0, 4, 0, 4));
	}

	private void createGUI()
	{
		clearText();

      Dimension progSize = _progressBar.getPreferredSize();
      progSize.height = _textLbl.getPreferredSize().height;
      _progressBar.setPreferredSize(progSize);

      _progressBar.setStringPainted(true);

      _pnlLabelOrProgress.setLayout(new GridLayout(1,1));
      _pnlLabelOrProgress.add(_textLbl);

      
		
		_gbc.anchor = GridBagConstraints.WEST;
		_gbc.weightx = 1.0;
		_gbc.fill = GridBagConstraints.HORIZONTAL;
		_gbc.gridy = 0;
		_gbc.gridx = 0;
		addJComponent(_pnlLabelOrProgress);

		
		_gbc.weightx = 0.0;
		_gbc.anchor = GridBagConstraints.CENTER;
		_gbc.gridx = GridBagConstraints.RELATIVE;
	}

	private void updateSubcomponentsFont(Container cont)
	{
		Component[] comps = cont.getComponents();
		for (int i = 0; i < comps.length; ++i)
		{
			comps[i].setFont(_font);
			if (comps[i] instanceof Container)
			{
				updateSubcomponentsFont((Container)comps[i]);
			}
		}
	}

   public void setStatusBarProgress(String msg, int minimum, int maximum, int value)
   {
      if(false == _pnlLabelOrProgress.getComponent(0) instanceof JProgressBar)
      {
         _pnlLabelOrProgress.remove(0);
         _pnlLabelOrProgress.add(_progressBar);
         validate();
      }

      _progressBar.setMinimum(minimum);
      _progressBar.setMaximum(maximum);
      _progressBar.setValue(value);

      if(null != msg)
      {
         _progressBar.setString(msg);
      }
      else
      {
         _progressBar.setString("");
      }
   }

   public void setStatusBarProgressFinished()
   {
      if(_pnlLabelOrProgress.getComponent(0) instanceof JProgressBar)
      {
         _pnlLabelOrProgress.remove(0);
         _pnlLabelOrProgress.add(_textLbl);
         validate();
         repaint();
      }
   }
}
