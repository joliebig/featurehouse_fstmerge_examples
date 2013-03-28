  
package genj.edit.actions;

import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Submitter;
import genj.view.ViewManager;


public class SetSubmitter extends AbstractChange {

    
    private Submitter submitter;
    
    
    public SetSubmitter(Submitter sub, ViewManager mgr) {
      super(sub.getGedcom(), Gedcom.getEntityImage(Gedcom.SUBM), resources.getString("submitter", sub.getGedcom().getName()), mgr);
      submitter = sub;
      if (sub.getGedcom().getSubmitter()==submitter) 
        setEnabled(false);
    }

    
    public void perform(Gedcom gedcom) throws GedcomException {
      submitter.getGedcom().setSubmitter(submitter);
    }

} 

