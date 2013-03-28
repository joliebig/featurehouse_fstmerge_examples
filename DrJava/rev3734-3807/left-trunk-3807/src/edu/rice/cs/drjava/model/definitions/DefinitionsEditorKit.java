

package edu.rice.cs.drjava.model.definitions;

import javax.swing.text.*;
import edu.rice.cs.drjava.model.GlobalEventNotifier;


public class DefinitionsEditorKit extends StyledEditorKit {

  private GlobalEventNotifier _notifier;

  
  public DefinitionsEditorKit(GlobalEventNotifier notifier) { _notifier = notifier; }

  private static ViewFactory _factory = new ViewFactory() {
    public View create(Element elem) {
      
      
      return new ColoringView(elem);
    }
  };

  
  public Document createNewDocument() { return  _createDefaultTypedDocument(); }

  
  private DefinitionsDocument _createDefaultTypedDocument() { return new DefinitionsDocument(_notifier); }

  
  public String getContentType() { return "text/java"; }

  
  public final ViewFactory getViewFactory() { return _factory; }
}




