  
package genj.edit.actions;

import java.awt.event.ActionEvent;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Submitter;


public class SetSubmitter extends AbstractChange {

    
    private Submitter submitter;
    
    
    public SetSubmitter(Submitter sub) {
      super(sub.getGedcom(), Gedcom.getEntityImage(Gedcom.SUBM), resources.getString("submitter", sub.getGedcom().getName()));
      submitter = sub;
      if (sub.getGedcom().getSubmitter()==submitter) 
        setEnabled(false);
    }

    
    protected Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException {
      submitter.getGedcom().setSubmitter(submitter);
      return null;
    }

} 

