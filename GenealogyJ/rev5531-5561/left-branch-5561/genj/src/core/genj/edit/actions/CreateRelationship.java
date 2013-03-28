
package genj.edit.actions;

import genj.common.SelectEntityWidget;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Property;
import genj.util.WordBuffer;
import genj.util.swing.NestedBlockLayout;
import genj.view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;


public abstract class CreateRelationship extends AbstractChange {

  
  private Entity existing;

  
  private JCheckBox checkID;
  
  
  private JTextField requestID;
  
  
  protected String targetType;
  
  
  public CreateRelationship(String name, Gedcom gedcom, String targetType) {
    super(gedcom, Gedcom.getEntityImage(targetType).getOverLayed(imgNew), resources.getString("link", name));
    this.targetType = targetType;
  }

  
  protected String getConfirmMessage() {

    WordBuffer result = new WordBuffer("\n");
    
    
    
    result.append( existing==null ?
      resources.getString("confirm.new", new Object[]{ Gedcom.getName(targetType,false), gedcom}) :
      resources.getString("confirm.use", new Object[]{ existing.getId(), gedcom})
    );
    
    
    result.append( resources.getString("confirm.new.related", getDescription()) );

    
    String warning = getWarning(existing);
    if (warning!=null) 
      result.append( "**Note**: " + warning );

    
    return result.toString();
  }
  
  
  public abstract String getDescription();

  
  public String getWarning(Entity target) {
    return null;
  }

  
  protected JPanel getDialogContent() {
    
    JPanel result = new JPanel(new NestedBlockLayout("<col><row><select wx=\"1\"/></row><row><text wx=\"1\" wy=\"1\"/></row><row><check/><text/></row></col>"));

    
    final SelectEntityWidget select = new SelectEntityWidget(gedcom, targetType, resources.getString("select.new"));
 
    
    requestID = new JTextField(gedcom.getNextAvailableID(targetType), 8);
    requestID.setEditable(false);
    
    checkID = new JCheckBox(resources.getString("assign_id"));
    checkID.getModel().addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        requestID.setEditable(checkID.isSelected());
        if (checkID.isSelected())  requestID.requestFocusInWindow();
      }
    });
    
    
    result.add(select);
    result.add(getConfirmComponent());
    result.add(checkID);
    result.add(requestID);

    
    select.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        existing = select.getSelection();
        
        if (existing!=null) checkID.setSelected(false);
        checkID.setEnabled(existing==null);
        refresh();
      }
    });
    




    
    
    return result;
  }

  
  public void perform(Gedcom gedcom) throws GedcomException {
    
    Entity change;
    if (existing!=null) {
      change = existing;
    } else {
      
      String id = null;
      if (requestID.isEditable()) {
        id = requestID.getText();
        if (gedcom.getEntity(targetType, id)!=null)
          throw new GedcomException(resources.getString("assign_id_error", id));
      }
      
      change = gedcom.createEntity(targetType, id);
      change .addDefaultProperties();
    }
    
    
    Property focus = change(change, change!=existing);
    
    


    
    
    fireSelection(new Context(focus), false);
    
    
  }
  
  
  protected abstract Property change(Entity target, boolean targetIsNew) throws GedcomException;

}
