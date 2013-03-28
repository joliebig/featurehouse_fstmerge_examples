
package genj.util.swing;

import genj.io.FileAssociation;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


public class EditorHyperlinkSupport implements HyperlinkListener {

  private final static Logger LOG = Logger.getLogger("genj.log");
  
  private JEditorPane editor;
  
  
  public EditorHyperlinkSupport(JEditorPane editor) {
    this.editor = editor;
  }

  
  public void hyperlinkUpdate(HyperlinkEvent e) {
    
    if (e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED)
      return;
    
    try {
      if (e.getDescription().startsWith("#")) 
          editor.scrollToReference(e.getDescription().substring(1));
      else {
        FileAssociation.open(new URL(e.getDescription()), editor);
      }          
        
    } catch (Throwable t) {
      LOG.log(Level.FINE, "Can't handle URL for "+e.getDescription(), t);
    }
    
  }

}