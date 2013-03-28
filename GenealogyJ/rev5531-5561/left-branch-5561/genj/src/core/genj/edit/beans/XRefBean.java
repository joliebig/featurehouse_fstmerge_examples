
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.util.Registry;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class XRefBean extends PropertyBean {

  private Preview preview;
  private PropertyXRef xref;
  
  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    preview = new Preview();
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, preview);
    
    preview.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        
        if (e.getClickCount()<2)
          return;
        
        if (xref==null)
          return;
        
        View.fireSelection(preview, new ViewContext(xref), true);
      }
    });
  }
  
  
  
    
  public boolean isEditable() {
    return false;
  }
  
  
  boolean accepts(Property prop) {
    return prop instanceof PropertyXRef;
  }
  public void setPropertyImpl(Property prop) {
    
    PropertyXRef xref = (PropertyXRef)prop;
    this.xref = xref;
    
    
    if (xref!=null&&xref.getTargetEntity()!=null) 
      preview.setEntity(xref.getTargetEntity());
    else
      preview.setEntity(null);
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(64,48);
  }

    
} 
