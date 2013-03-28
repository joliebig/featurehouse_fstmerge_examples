package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ObjectTreeSearch;


public class ViewObjectAtCursorInObjectTreeAction extends SquirrelAction
   implements ISQLPanelAction
{
   private static final long serialVersionUID = 1L;

   
   private ISQLPanelAPI _panel;

   
   public ViewObjectAtCursorInObjectTreeAction(IApplication app)
   {
      super(app);
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel && _panel.isInMainSessionWindow());
   }

   
   public synchronized void actionPerformed(ActionEvent evt)
   {
      if (_panel == null)
      {
         return;
      }

      String stringAtCursor = getStringAtCursor();

      new ObjectTreeSearch().viewObjectInObjectTree(stringAtCursor, _panel.getSession());
   }

   private String getStringAtCursor()
   {
      String text = _panel.getSQLEntryPanel().getText();
      int caretPos = _panel.getSQLEntryPanel().getCaretPosition();

      int lastIndexOfText = Math.max(0,text.length()-1);
      int beginPos = Math.min(caretPos, lastIndexOfText); 
      while(0 < beginPos && false == isParseStop(text.charAt(beginPos), false))
      {
         --beginPos;
      }

      int endPos = caretPos;
      while(endPos < text.length() && false == isParseStop(text.charAt(endPos), true))
      {
         ++endPos;
      }

      return text.substring(beginPos, endPos).trim();


   }

   private boolean isParseStop(char c, boolean treatDotAsStop)
   {
      return
         '(' == c ||
         ')' == c ||
         '\'' == c ||
         Character.isWhitespace(c) ||
         (treatDotAsStop && '.' == c);

   }
}
