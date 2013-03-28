
package genj.view;

import genj.gedcom.Gedcom;
import genj.window.WindowBroadcastEvent;

import java.awt.Component;


public class CommitRequestedEvent extends WindowBroadcastEvent {

  private Gedcom gedcom;
  
  
  public CommitRequestedEvent(Gedcom gedcom, Component source) {
    super(source);
    this.gedcom = gedcom;
  }
  
  
  public Gedcom getGedcom() {
    return gedcom;
  }
  
} 
