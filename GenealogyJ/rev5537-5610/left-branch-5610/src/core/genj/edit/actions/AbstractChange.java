
package genj.edit.actions;

import genj.edit.Images;
import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.UnitOfWork;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.TextAreaWidget;
import genj.view.SelectionSink;
import genj.window.WindowManager;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public abstract class AbstractChange extends Action2 {
  
  
   final static Resources resources = Resources.get(AbstractChange.class);
  
  
  protected Gedcom gedcom;
  
  private Context selection;
  
  
  protected final static ImageIcon imgNew = Images.imgNewEntity;
  
  private JTextArea confirm;

  
  public AbstractChange(Gedcom ged, ImageIcon img, String text) {
    gedcom = ged;
    super.setImage(img);
    super.setText(text);
    super.setTip(text);
  }

  
  protected String getConfirmMessage() {
    return null;
  }
  
    protected JPanel getDialogContent() {
    JPanel result = new JPanel(new NestedBlockLayout("<col><text wx=\"1\" wy=\"1\"/></col>"));
    result.add(getConfirmComponent());
    return result;
  }
  
  protected JComponent getConfirmComponent() {
    if (confirm==null) {
      confirm = new TextAreaWidget(getConfirmMessage(), 6, 40);
      confirm.setWrapStyleWord(true);
      confirm.setLineWrap(true);
      confirm.setEditable(false);
    }
    return new JScrollPane(confirm);
  }
  
  
  protected void refresh() {
    
    if (confirm!=null)
      confirm.setText(getConfirmMessage());
  }
  
  
  public void actionPerformed(final ActionEvent event) {
	  
    
    String msg = getConfirmMessage();
    if (msg!=null) {
  
      
      Action[] actions = { 
          new Action2(resources.getString("confirm.proceed", getText())),
          Action2.cancel() 
      };
      
      
      int rc = WindowManager.getInstance().openDialog(getClass().getName(), getText(), WindowManager.QUESTION_MESSAGE, getDialogContent(), actions, event );
      if (rc!=0)
        return;
    }
        
    
    try {
      gedcom.doUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) throws GedcomException {
          selection = execute(gedcom, event);
        }
      });
    } catch (Throwable t) {
      WindowManager.getInstance().openDialog(getClass().getName(), null, WindowManager.ERROR_MESSAGE, t.getMessage(), Action2.okOnly(), event);
    }
    
    
    if (selection!=null)
    	SelectionSink.Dispatcher.fireSelection(event, selection);
      
    
  }
  
  
  protected abstract Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException;

} 

