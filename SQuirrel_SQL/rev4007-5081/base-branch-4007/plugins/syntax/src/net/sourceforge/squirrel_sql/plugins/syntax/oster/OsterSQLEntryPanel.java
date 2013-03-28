package net.sourceforge.squirrel_sql.plugins.syntax.oster;

import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseListener;

import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.dnd.FileEditorDropTargetListener;
import net.sourceforge.squirrel_sql.client.session.BaseSQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;

public class OsterSQLEntryPanel extends BaseSQLEntryPanel
{
	
	private static final ILogger s_log = LoggerController.createLogger(OsterSQLEntryPanel.class);

	
	private IApplication _app;

	
	private OsterTextControl _textArea;

    @SuppressWarnings("unused")
    private DropTarget dt;

   private ISession _session;


   OsterSQLEntryPanel(ISession session, SyntaxPreferences prefs)
	{
		super(session.getApplication());
      _session = session;

      if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		if (prefs == null)
		{
			throw new IllegalArgumentException("Null Preferences passed");
		}

		_app = session.getApplication();

		_textArea = new OsterTextControl(session, prefs, getIdentifier());
		
		dt = new DropTarget(_textArea, new FileEditorDropTargetListener(session));
	}


   public void endColorerThread()
   {
      _textArea.endColorerThread();
   }


	
	public JTextComponent getTextComponent()
	{
		return _textArea;
	}

	
	public boolean getDoesTextComponentHaveScroller()
	{
		return false;
	}

	
	public String getText()
	{
		return _textArea.getText();
	}

	public void setFont(Font font)
	{
		_textArea.setFont(font);
	}

	
	public String getSelectedText()
	{
		return _textArea.getSelectedText();
	}

	
	public void setText(String text)
	{
		setText(text, true);
	}

	
	public void setText(String text, boolean select)
	{
		_textArea.setText(text);

      if(select)
      {
         setSelectionEnd(_textArea.getDocument().getLength());
         setSelectionStart(0);
      }
   }

	
	public void appendText(String sqlScript)
	{
		appendText(sqlScript, false);
	}

	
	public void appendText(String sqlScript, boolean select)
	{
		Document doc = _textArea.getDocument();

		try
		{
			int start = 0;
			if (select)
			{
				start = doc.getLength();
			}

			doc.insertString(doc.getLength(), sqlScript, null);

			if (select)
			{
				setSelectionEnd(doc.getLength());
				setSelectionStart(start);
			}
		}
		catch (Exception ex)
		{
			s_log.error("Error appending text to text area", ex);
		}
	}

	
	public int getCaretPosition()
	{
		return _textArea.getCaretPosition();
	}

	public void setCaretPosition(int value)
	{
		_textArea.setCaretPosition(value);
	}

	
	public void setTabSize(int tabSize)
	{
		_textArea.getDocument().putProperty(PlainDocument.tabSizeAttribute,
												Integer.valueOf(tabSize));
	}

	
	public int getSelectionStart()
	{
		return _textArea.getSelectionStart();
	}

	
	public void setSelectionStart(int pos)
	{
		_textArea.setSelectionStart(pos);
	}

	
	public int getSelectionEnd()
	{
		return _textArea.getSelectionEnd();
	}

	
	public void setSelectionEnd(int pos)
	{
		_textArea.setSelectionEnd(pos);
	}

	
	public void replaceSelection(String sqlScript)
	{
		_textArea.replaceSelection(sqlScript);
	}

	
	public boolean hasFocus()
	{
		return _textArea.hasFocus();
	}

	
	public void requestFocus()
	{
		_textArea.requestFocus();
	}



	
	public void addMouseListener(MouseListener lis)
	{
		_textArea.addMouseListener(lis);
	}

	
	public void removeMouseListener(MouseListener lis)
	{
		_textArea.removeMouseListener(lis);
	}

	public void updateFromPreferences()
	{
		_textArea.updateFromPreferences();
	}

	
	public boolean hasOwnUndoableManager()
	{
		return false;
	}

	
	public void addUndoableEditListener(UndoableEditListener listener)
	{
		_textArea.getDocument().addUndoableEditListener(listener);
	}

	
	public void removeUndoableEditListener(UndoableEditListener listener)
	{
		_textArea.getDocument().removeUndoableEditListener(listener);
	}


	
    public void setUndoManager(UndoManager manager) {
        
    }

    
	public int getCaretLineNumber()
	{
		final int pos = getCaretPosition();
		final Document doc = _textArea.getStyledDocument();
		final Element docElem = doc.getDefaultRootElement();
		return docElem.getElementIndex(pos);
	}

	
	public int getCaretLinePosition()
	{
      String textTillCarret = getText().substring(0, getCaretPosition());

      int lineFeedIndex = textTillCarret.lastIndexOf('\n');
      if(- 1 == lineFeedIndex)
      {
         return getCaretPosition();
      }
      else
      {
         return getCaretPosition() - lineFeedIndex - 1;
      }







	}

	
	public void addCaretListener(CaretListener lis)
	{
		_textArea.addCaretListener(lis);
	}

	
	public void removeCaretListener(CaretListener lis)
	{
		_textArea.removeCaretListener(lis);
	}

	public void addSQLTokenListener(SQLTokenListener tl)
	{
		_textArea.addSQLTokenListener(tl);
	}

	public void removeSQLTokenListener(SQLTokenListener tl)
	{
		_textArea.removeSQLTokenListener(tl);
	}

   public ISession getSession()
   {
      return _session;
   }

}
