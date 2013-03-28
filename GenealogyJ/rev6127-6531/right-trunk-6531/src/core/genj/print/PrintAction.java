
package genj.print;

import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.ImageIcon;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.PrintException;
import javax.swing.Action;


public abstract class PrintAction extends Action2 {
  
  private final static Resources RES = Resources.get(PrintAction.class);
  private final static ImageIcon IMG = new ImageIcon(PrintAction.class, "images/Print.png");
  private final static Logger LOG = Logger.getLogger("genj.print");
  
  private String title;
  
  
  public PrintAction(String title) {
    setText(RES.getString("print"));
    setTip(getText());
    setImage(IMG);
    this.title = RES.getString("title", title);
  }
  
  protected abstract PrintRenderer getRenderer();

  
  @Override
  public void actionPerformed(ActionEvent e) {
    
    PrintTask task;
    try {
      task = new PrintTask(title, getRenderer());
    } catch (PrintException pe) {
      LOG.log(Level.INFO, "can't setup print task", pe);
      DialogHelper.openDialog(title, DialogHelper.ERROR_MESSAGE, pe.getMessage(), Action2.okOnly(), e);
      return;
    }
    
    
    PrintWidget widget = new PrintWidget(task);

    
    Action[] actions = { 
        new Action2(RES.getString("print")),
        Action2.cancel() 
    };
    
    
    int choice = DialogHelper.openDialog(
        title, 
        DialogHelper.QUESTION_MESSAGE, 
        widget, actions, e);

    
    if (choice != 0 || task.getPages().width == 0 || task.getPages().height == 0)
      return;
    
    widget.commit();
    
    
    task.print();

    
    
  }

}
