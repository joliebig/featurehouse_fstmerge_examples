package net.sourceforge.squirrel_sql.client.session;

import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseListener;

import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.client.gui.dnd.FileEditorDropTargetListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DefaultSQLEntryPanel extends BaseSQLEntryPanel
{
	
	private static ILogger s_log =
		LoggerController.createLogger(DefaultSQLEntryPanel.class);

	
	private ISession _session;

	
	private MyTextArea _comp;

    @SuppressWarnings("unused")
    private DropTarget dt;
	
	public DefaultSQLEntryPanel(ISession session)
	{
		super(session.getApplication());
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_session = session;
		_comp = new MyTextArea(session, this);
		
		dt = new DropTarget(_comp, new FileEditorDropTargetListener(session));
	}

	
	public JTextComponent getTextComponent()
	{
		return _comp;
	}

	
	public boolean getDoesTextComponentHaveScroller()
	{
		return false;
	}

	public void addUndoableEditListener(UndoableEditListener lis)
	{
		_comp.getDocument().addUndoableEditListener(lis);
	}

	public void removeUndoableEditListener(UndoableEditListener lis)
	{
		_comp.getDocument().removeUndoableEditListener(lis);
	}

	
	public boolean hasOwnUndoableManager()
	{
		return false;
	}


	
	public String getText()
	{
		return _comp.getText();
	}

	
	public String getSelectedText()
	{
		return _comp.getSelectedText();
	}

	
	public void setText(String sqlScript)
	{
		setText(sqlScript, true);
	}

	
	public void setText(String sqlScript, boolean select)
	{
		_comp.setText(sqlScript);
		if (select)
		{
			setSelectionEnd(getText().length());
			setSelectionStart(0);
		}
      _comp.setCaretPosition(0);
	}

	
	public void appendText(String sqlScript)
	{
		appendText(sqlScript, false);
	}

	
	public void appendText(String sqlScript, boolean select)
	{
		final int start = select ? getText().length() : 0;
		_comp.append(sqlScript);
		if (select)
		{
			setSelectionEnd(getText().length());
			setSelectionStart(start);
		}
	}

	
	public void replaceSelection(String sqlScript)
	{
		_comp.replaceSelection(sqlScript);
	}

	
	public int getCaretPosition()
	{
		return _comp.getCaretPosition();
	}

	
	public void setTabSize(int tabSize)
	{
		_comp.setTabSize(tabSize);
	}

	public void setFont(Font font)
	{
		_comp.setFont(font);
	}


	
	public void addMouseListener(MouseListener lis)
	{
		_comp.addMouseListener(lis);
	}

	
	public void removeMouseListener(MouseListener lis)
	{
		_comp.removeMouseListener(lis);
	}

	
	public void setCaretPosition(int pos)
	{
		_comp.setCaretPosition(pos);
	}

	
	public int getCaretLineNumber()
	{
		try
		{
			return _comp.getLineOfOffset(_comp.getCaretPosition());
		}
		catch (BadLocationException ex)
		{
			return 0;
		}
	}

	public int getCaretLinePosition()
	{
		int caretPos = _comp.getCaretPosition();
		int caretLineOffset = caretPos;
		try
		{
			caretLineOffset = _comp.getLineStartOffset(getCaretLineNumber());
		}
		catch (BadLocationException ex)
		{
			s_log.error("BadLocationException in getCaretLinePosition", ex);
		}
		return caretPos - caretLineOffset;
	}

	
	public int getSelectionStart()
	{
		return _comp.getSelectionStart();
	}

	
	public void setSelectionStart(int pos)
	{
		_comp.setSelectionStart(pos);
	}

	
	public int getSelectionEnd()
	{
		return _comp.getSelectionEnd();
	}

	
	public void setSelectionEnd(int pos)
	{
		_comp.setSelectionEnd(pos);
	}

	
	public boolean hasFocus()
	{
		return _comp.hasFocus();
	}

	
	public void requestFocus()
	{
		_comp.requestFocus();
	}

	
	public void addCaretListener(CaretListener lis)
	{
		_comp.addCaretListener(lis);
	}

	
	public void removeCaretListener(CaretListener lis)
	{
		_comp.removeCaretListener(lis);
	}

	public void addSQLTokenListener(SQLTokenListener tl)
	{
		
	}

	public void removeSQLTokenListener(SQLTokenListener tl)
	{
		
	}

   public ISession getSession()
   {
      return _session;
   }

   private static class MyTextArea extends JTextArea
	{
		private DefaultSQLEntryPanel _pnl;

		private MyTextArea(ISession session, DefaultSQLEntryPanel pnl)
		{
			super();
			_pnl = pnl;
			SessionProperties props = session.getProperties();
			final FontInfo fi = props.getFontInfo();
			if (fi != null)
			{
				this.setFont(props.getFontInfo().createFont());
			}
		}












	}

    
    public void setUndoManager(UndoManager manager) {
        
    }
}
