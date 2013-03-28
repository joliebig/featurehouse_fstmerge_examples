
package genj.edit.beans;

import genj.common.SelectEntityWidget;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.gedcom.UnitOfWork;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.view.SelectionSink;
import genj.view.ViewContext;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;


public class XRefBean extends PropertyBean {

  private Preview preview;
  private PropertyXRef xref;
  
  @Override
  public ViewContext getContext() {
    
    ViewContext result = super.getContext();
    result.addAction(new Swivel());
    return result;
  }

  
  private class Swivel extends Action2 {
    public Swivel() {
      setText(RESOURCES.getString("xref.swivel"));
      setImage(MetaProperty.IMG_LINK);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      
      if (xref==null)
        return;
      
      SelectEntityWidget select = new SelectEntityWidget(xref.getGedcom(), xref.getTargetType(), null);
      if (0!=WindowManager.getInstance().openDialog(null, getText(), WindowManager.QUESTION_MESSAGE, select, Action2.okCancel(), e))
        return;

      final Entity newTarget = select.getSelection();
      
      if (xref.getTarget()!=null)
        LOG.fine("Swiveling "+xref.getEntity().getId()+"."+xref.getPath()+" from "+xref.getTarget().getEntity().getId()+" to "+newTarget.getId());
      else
        LOG.fine("Swiveling "+xref.getEntity().getId()+"."+xref.getPath()+" to "+newTarget.getId());
        
      try {
        xref.getGedcom().doUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) throws GedcomException {
            Property backpointer = xref.getTarget();
            if (backpointer!=null) {
              xref.unlink();
              backpointer.getParent().delProperty(backpointer);
            }
            xref.setValue("@"+newTarget.getId()+"@");
            xref.link();
          }
        });
      } catch (GedcomException ge) {
        WindowManager.getInstance().openDialog(null, getText(), WindowManager.WARNING_MESSAGE, ge.getMessage(), Action2.okOnly(), e);
        LOG.log(Level.FINER, ge.getMessage(), ge);
      }
      
      
    }
  }
  
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
        
        SelectionSink.Dispatcher.fireSelection(e, new ViewContext(xref));
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
