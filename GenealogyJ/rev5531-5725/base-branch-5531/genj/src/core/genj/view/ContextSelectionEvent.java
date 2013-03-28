
package genj.view;

import genj.gedcom.Gedcom;
import genj.window.WindowBroadcastEvent;

import java.awt.Component;


public class ContextSelectionEvent extends WindowBroadcastEvent {
  
  private static ViewContext lastContext = null;
  private ViewContext context;
  private boolean isActionPerformed = false;
  
  
  public ContextSelectionEvent(ViewContext context, Component source) {
    super(source);
    this.context = context;
  }
  
  
  public ContextSelectionEvent(ViewContext context, Component source, boolean isActionPerformed) {
    this(context, source);
    this.isActionPerformed = isActionPerformed;
  }
  
  
  public static ViewContext getLastBroadcastedSelection() {
    return lastContext;
  }
  
  @Override
  protected void setBroadcasted() {
    lastContext = context;
  }
  
  
  
  public ViewContext getContext() {
    return context;
  }

  
  public boolean isActionPerformed() {
    return isActionPerformed;
  }
  
  
  public static ContextSelectionEvent narrow(WindowBroadcastEvent event, Gedcom gedcom) {
    ContextSelectionEvent cse = narrow(event);
    return cse==null || cse.getContext().getGedcom()!=gedcom ? null : cse;
  }

  
  public static ContextSelectionEvent narrow(WindowBroadcastEvent event) {
    return event instanceof ContextSelectionEvent ? (ContextSelectionEvent)event : null;
  }
}
