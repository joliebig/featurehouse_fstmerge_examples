  
package genj.edit.actions;

import java.awt.event.ActionEvent;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.PropertyPlace;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.TextFieldWidget;

import javax.swing.JPanel;


public class SetPlaceHierarchy extends AbstractChange {

    
    private PropertyPlace place;
    
    
    private TextFieldWidget hierarchy;
    
    
    public SetPlaceHierarchy(PropertyPlace place) {
      super(place.getGedcom(), place.getImage(false), resources.getString("place.hierarchy"));

      this.place = place;
    }

        
    protected String getConfirmMessage() {
      return resources.getString("place.hierarchy.msg", place.getGedcom().getName());
   }
    
    
    protected JPanel getDialogContent() {
      
      JPanel result = new JPanel(new NestedBlockLayout("<col><confirm wx=\"1\" wy=\"1\"/><enter wx=\"1\"/></col>"));

      
      hierarchy = new TextFieldWidget(place.getFormatAsString());
      
      result.add(getConfirmComponent());
      result.add(hierarchy);
      
      
      return result;
    }
    
    
    protected Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException {
      place.setFormatAsString(true, hierarchy.getText().trim());
      return null;
    }

} 

