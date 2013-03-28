

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.model.definitions.ColoringGlyphPainter;
import javax.swing.text.*;



public class InteractionsEditorKit extends StyledEditorKit {
  
  
  public InteractionsEditorKit() {
  }
  
  
  private static ViewFactory _factory = new ViewFactory() {
    
    public View create(Element elem) {
      String kind = elem.getName();
      
      if (kind != null) {
        if (kind.equals(AbstractDocument.ContentElementName)) {
          return _createColoringView(elem);
        } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
          return new ParagraphView(elem);
        } else if (kind.equals(AbstractDocument.SectionElementName)) {
          return new BoxView(elem, View.Y_AXIS);
        } else if (kind.equals(StyleConstants.ComponentElementName)) {
          return new ComponentView(elem);
        } else if (kind.equals(StyleConstants.IconElementName)) {
          return new IconView(elem);
        }
      }
      
      
      return _createColoringView(elem);
    }
    
  };
  
  
  public String getContentType() { return "text/java"; }
  
  
  public final ViewFactory getViewFactory() { return _factory; }
  
  public InteractionsDJDocument createDefaultDocument() {
    return new InteractionsDJDocument();
  }
  
  
  private static GlyphView _createColoringView(Element elem) {    
    final GlyphView view = new GlyphView(elem);
    view.setGlyphPainter(new ColoringGlyphPainter(new Runnable() {
      public void run() {
        if (view.getContainer() != null) view.getContainer().repaint();
      }
    }));
    return view;
  }
}


