
package genj.edit.actions;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.util.swing.NestedBlockLayout;
import genj.view.ContextSelectionEvent;
import genj.view.ViewContext;
import genj.window.WindowManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class CreateEntity extends AbstractChange {

  
  private String etag;
  
  
  private JTextField requestID;
  
  
  public CreateEntity(Gedcom ged, String tag) {
    super(ged, Gedcom.getEntityImage(tag).getOverLayed(imgNew), resources.getString("new", Gedcom.getName(tag, false) ));
    etag = tag;
  }
  
  
  protected JPanel getDialogContent() {
    
    JPanel result = new JPanel(new NestedBlockLayout("<col><row><text wx=\"1\" wy=\"1\"/></row><row><check/><text/></row></col>"));

    
    requestID = new JTextField(gedcom.getNextAvailableID(etag), 8);
    requestID.setEditable(false);
    
    final JCheckBox check = new JCheckBox(resources.getString("assign_id"));
    check.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        requestID.setEditable(check.isSelected());
        if (check.isSelected())  requestID.requestFocusInWindow();
      }
    });
    
    result.add(getConfirmComponent());
    result.add(check);
    result.add(requestID);
    
    
    return result;
  }
  
  
  protected String getConfirmMessage() {
    
    String about = resources.getString("confirm.new", new Object[]{ Gedcom.getName(etag,false), gedcom});
    
    String detail = resources.getString("confirm.new.unrelated");
    
    return about + '\n' + detail;
  }
  
  
  public void perform(Gedcom gedcom) throws GedcomException {
    
    String id = null;
    if (requestID.isEditable()) {
      id = requestID.getText();
      if (gedcom.getEntity(etag, id)!=null)
        throw new GedcomException(resources.getString("assign_id_error", id));
    }
    
    Entity entity = gedcom.createEntity(etag, id);
    entity.addDefaultProperties();
    
    WindowManager.broadcast(new ContextSelectionEvent(new ViewContext(entity), getTarget(), true));
    
  }
  
} 

