
package genj.edit.actions;

import java.awt.event.ActionEvent;

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
import genj.view.View;
import genj.window.WindowManager;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public abstract class AbstractChange extends Action2 implements UnitOfWork {
  
  
   final static Resources resources = Resources.get(AbstractChange.class);
  
  
  protected Gedcom gedcom;
  
  
  protected final static ImageIcon imgNew = Images.imgNewEntity;
  
  private JTextArea confirm;

  
  public AbstractChange(Gedcom ged, ImageIcon img, String text) {
    gedcom = ged;
    super.setImage(img);
    super.setText(text);
  }

    
  protected void handleThrowable(String phase, Throwable t) {
    
    String message = ""+t.getMessage();
    
    getWindowManager().openDialog("err", "Error", WindowManager.ERROR_MESSAGE, message, Action2.okOnly(), getTarget());
  }
  
  protected WindowManager getWindowManager() {
    return WindowManager.getInstance(getTarget());    
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
  
  
  public void actionPerformed(ActionEvent event) {
    
    
    String msg = getConfirmMessage();
    if (msg!=null) {
  
      
      Action[] actions = { 
          new Action2(resources.getString("confirm.proceed", getText())),
          Action2.cancel() 
      };
      
      
      int rc = getWindowManager().openDialog(getClass().getName(), getText(), WindowManager.QUESTION_MESSAGE, getDialogContent(), actions, getTarget() );
      if (rc!=0)
        return;
    }
        
    
    try {
      gedcom.doUnitOfWork(this);
    } catch (Throwable t) {
      getWindowManager().openDialog(getClass().getName(), null, WindowManager.ERROR_MESSAGE, t.getMessage(), Action2.okOnly(), getTarget());
    }
    
    
  }
  
  
  public abstract void perform(Gedcom gedcom) throws GedcomException;

  protected void fireSelection(Context context, boolean isActionPerformed) {
    if (getTarget()!=null)
      View.fireSelection(getTarget(), context, isActionPerformed);
  }
  
} 

