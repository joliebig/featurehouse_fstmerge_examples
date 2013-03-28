package net.sourceforge.squirrel_sql.client.session;

import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.text.JTextComponent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;

public interface ISQLEntryPanel extends IHasIdentifier
{
	
	JTextComponent getTextComponent();

	
	boolean getDoesTextComponentHaveScroller();

	String getText();
	String getSelectedText();

	
	void setText(String sqlScript);

	
	void setText(String sqlScript, boolean select);

	
	void appendText(String text);

	
	void appendText(String sqlScript, boolean select);

	
	void replaceSelection(String sqlScript);

	String getSQLToBeExecuted();
   int[] getBoundsOfSQLToBeExecuted();

	void moveCaretToPreviousSQLBegin();

	void moveCaretToNextSQLBegin();

   void selectCurrentSql();


	int getSelectionStart();
	void setSelectionStart(int pos);

	int getSelectionEnd();
	void setSelectionEnd(int pos);

	int getCaretPosition();
	void setCaretPosition(int pos);

	
	int getCaretLineNumber();

	int getCaretLinePosition();

	boolean hasFocus();
	void requestFocus();

	void setFont(Font font);
	void setTabSize(int tabSize);

	
	void addToSQLEntryAreaMenu(JMenu menu);

	
	JMenuItem addToSQLEntryAreaMenu(Action action);

	void addMouseListener(MouseListener lis);
	void removeMouseListener(MouseListener lis);

	boolean hasOwnUndoableManager();

    void setUndoManager(UndoManager manager);
    
	void addUndoableEditListener(UndoableEditListener listener);

	void removeUndoableEditListener(UndoableEditListener listener);

	void setUndoActions(Action undo, Action redo);

	void addCaretListener(CaretListener lis);
	void removeCaretListener(CaretListener lis);

	void addSQLTokenListener(SQLTokenListener tl);
	void removeSQLTokenListener(SQLTokenListener tl);

	void dispose();

   ISession getSession();
}
