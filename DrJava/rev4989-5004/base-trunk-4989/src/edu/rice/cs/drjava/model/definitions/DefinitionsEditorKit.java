

package edu.rice.cs.drjava.model.definitions;

import edu.rice.cs.util.UnexpectedException;

import javax.swing.text.*;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import edu.rice.cs.drjava.model.GlobalEventNotifier;


public class DefinitionsEditorKit extends StyledEditorKit {
  
  private GlobalEventNotifier _notifier;
  private Action[] _actions;
  
  
  public DefinitionsEditorKit(GlobalEventNotifier notifier) {
    _notifier = notifier;
    Action[] supActions = super.getActions();
    _actions = new Action[supActions.length];
    for(int i=0; i<_actions.length; ++i) {
      Action a = supActions[i];
      Object name = a.getValue("Name");
      if (name.equals(beginWordAction)) {
        _actions[i] = new BeginWordAction(beginWordAction, false);
      }
      else if (name.equals(endWordAction)) {
        _actions[i] = new EndWordAction(endWordAction, false);
      }
      else if (name.equals(nextWordAction)){
        _actions[i] = new NextWordAction(nextWordAction, false);
      }
      else if (name.equals(previousWordAction)) {
        _actions[i] = new PreviousWordAction(previousWordAction, false);
      }
      else if (name.equals(selectionNextWordAction)){
        _actions[i] = new NextWordAction(selectionNextWordAction, true);
      }
      else if (name.equals(selectionPreviousWordAction)) {
        _actions[i] = new PreviousWordAction(selectionPreviousWordAction, true);
      }
      else if (name.equals(selectWordAction)) {
        _actions[i] = new SelectWordAction();
      }
      else _actions[i] = a;
    }
  }
  
  public Action[] getActions() { return _actions; }
  
  private static ViewFactory _factory = new ViewFactory() {
    public View create(Element elem) {
      
      
      return new ColoringView(elem);
    }
  };
  
  
  public DefinitionsDocument createNewDocument() { return  _createDefaultTypedDocument(); }
  
  
  private DefinitionsDocument _createDefaultTypedDocument() { return new DefinitionsDocument(_notifier); }
  
  
  public String getContentType() { return "text/java"; }
  
  
  public final ViewFactory getViewFactory() { return _factory; }
  
  
  static class BeginWordAction extends TextAction {
    BeginWordAction(String nm, boolean select) {
      super(nm);
      this.select = select;
    }
    
    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getTextComponent(e);
      if (target != null) {
        try {
          int offs = target.getCaretPosition();
          final String text = target.getDocument().getText(0,offs);
          while(offs>0) {
            char chPrev = text.charAt(offs - 1);
            if (("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(chPrev)>=0) || (Character.isWhitespace(chPrev))) {
              break;
            }
            --offs;
            if (offs==0) break; 
            char ch = text.charAt(offs);
            chPrev = text.charAt(offs - 1);
            if (("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(ch)>=0) || ("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(chPrev)>=0) || Character.isWhitespace(ch) || Character.isWhitespace(chPrev)) {
              break;
            }
          }
          if (select) {
            target.moveCaretPosition(offs);
          } else {
            target.setCaretPosition(offs);
          }
        }
        catch(BadLocationException ble) { throw new UnexpectedException(ble); }
      }
    }
    private boolean select;
  }
  
  
  static class EndWordAction extends TextAction {
    EndWordAction(String nm, boolean select) {
      super(nm);
      this.select = select;
    }
    
    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getTextComponent(e);
      if (target != null) {
        try {
          int offs = target.getCaretPosition();
          final int iOffs = offs;
          final String text = target.getDocument().getText(iOffs,target.getDocument().getLength()-iOffs);
          while((offs-iOffs)<text.length()-1) {
            ++offs;
            char ch = text.charAt(offs-iOffs);
            if (("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(ch)>=0) || Character.isWhitespace(ch)) {
              break;
            }
          }
          if (select) {
            target.moveCaretPosition(offs);
          } else {
            target.setCaretPosition(offs);
          }
        }
        catch(BadLocationException ble) { throw new UnexpectedException(ble); }
      }
    }
    private boolean select;
  }
  
  
  static class PreviousWordAction extends TextAction {
    PreviousWordAction(String nm, boolean select) {
      super(nm);
      this.select = select;
    }
    
    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getTextComponent(e);
      if (target != null) {
        try {
          int offs = target.getCaretPosition();
          final String text = target.getDocument().getText(0,offs);
          while(offs>0) {
            --offs;
            if (offs == 0)
              break;
            char ch = text.charAt(offs);
            char chPrev = text.charAt(offs - 1);
            if (Character.isWhitespace(ch) && Character.isWhitespace(chPrev)){
              continue;
            }
            else if (("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(ch)>=0) || ("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(chPrev)>=0) || 
                     ((offs>=2) && Character.isWhitespace(chPrev) && !Character.isWhitespace(text.charAt(offs - 2)))) {
              break;
            }
            else if (Character.isWhitespace(chPrev) && !Character.isWhitespace(ch)){
              break;
            }
            else if (!Character.isWhitespace(chPrev) && ch == '\n'){
              break;
            }
          }
          if (select) {
            target.moveCaretPosition(offs);
          } else {
            target.setCaretPosition(offs);
          }
        }
        catch(BadLocationException ble) { throw new UnexpectedException(ble); }
      }
    }
    private boolean select;
  }
  
   
  static class NextWordAction extends TextAction {
    NextWordAction(String nm, boolean select) {
      super(nm);
      this.select = select;
    }
    
    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getTextComponent(e);
      if (target != null) {
        try {
          int offs = target.getCaretPosition();
          final int iOffs = offs;
          final String text = target.getDocument().getText(iOffs,target.getDocument().getLength()-iOffs);
          final int len = text.length();
          while((offs-iOffs) < len) {
            ++offs;
            if (offs-iOffs == len)
              break;
            char ch = text.charAt(offs-iOffs);
            char chPrev = text.charAt(offs-iOffs - 1);
            if (("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(ch)>=0) ||
                ("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(chPrev)>=0) ||
                Character.isWhitespace(chPrev) ||
                ch == '\n') {
              while((offs-iOffs<len) && Character.isWhitespace(ch) && ch != '\n'){
                if ("!@%^&*()-=+[]{};:'\",.<>/?".indexOf(chPrev)>=0)
                  break;
                ++offs;
                ch = text.charAt(offs-iOffs);
              }
              if (ch == '\n' && Character.isWhitespace(text.charAt(offs - iOffs - 1)))
                continue;
              else
                break;
            }
          }
          if (select) {
            target.moveCaretPosition(offs);
          } else {
            target.setCaretPosition(offs);
          }
        }
        catch(BadLocationException ble) { throw new UnexpectedException(ble); }
      }
    }
    private boolean select;
  }
  
  
  static class SelectWordAction extends TextAction {
    public SelectWordAction() {
      super(selectWordAction);
      start = new BeginWordAction("pigdog", false);
      end = new EndWordAction("pigdog", true);
    }
    public void actionPerformed(ActionEvent e) {
      start.actionPerformed(e);
      end.actionPerformed(e);
    }
    private Action start;
    private Action end;
  }
}
