package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class CharField extends JTextField
{
	
	public CharField()
	{
		super(" ");
	}

	
	public CharField(char ch)
	{
		super("" + ch);
	}

	
	public char getChar()
	{
		final String text = getText();
		if (text == null || text.length() == 0)
		{
			return ' ';
		}
		return text.charAt(0);
	}

	
	public void setChar(char ch)
	{
		setText(String.valueOf(ch));
	}

	
	protected Document createDefaultModel()
	{
		return new CharacterDocument();
	}

	
	static class CharacterDocument extends PlainDocument
	{
		public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
		{
			if (str != null)
			{
				char ch = str.length() > 0 ? str.charAt(0) : ' ';
				super.remove(0, getLength());
				super.insertString(0, String.valueOf(ch), a);
			}
		}
	}
}
