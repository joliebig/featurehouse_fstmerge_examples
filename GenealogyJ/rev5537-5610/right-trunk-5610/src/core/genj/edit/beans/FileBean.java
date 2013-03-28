
package genj.edit.beans;

import genj.gedcom.Property;
import genj.gedcom.PropertyBlob;
import genj.gedcom.PropertyFile;
import genj.util.Origin;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.FileChooserWidget;
import genj.util.swing.ImageWidget;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilePermission;
import java.util.List;

import javax.swing.JCheckBox;


public class FileBean extends PropertyBean {
  
  
  private ImageWidget preview = new ImageWidget();
  
  
  private JCheckBox updateMeta = new JCheckBox(resources.getString("file.update"), true);
  
  
  private FileChooserWidget chooser = new FileChooserWidget();
  
  private ActionListener doPreview = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      
      
      registry.put("bean.file.dir", chooser.getDirectory());
      
      
      File file = getProperty().getGedcom().getOrigin().getFile(chooser.getFile().toString());
      if (file==null) {
        preview.setSource(null);
        return;
      }
      preview.setSource(new ImageWidget.FileSource(file));
      
      
      String relative = getProperty().getGedcom().getOrigin().calcRelativeLocation(file.getAbsolutePath());
      if (relative!=null)
        chooser.setFile(relative);
      
      
    }
  };
  
  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    setLayout(new BorderLayout());
    
    
    chooser.setAccessory(updateMeta);
    chooser.addChangeListener(changeSupport);
    chooser.addActionListener(doPreview);

    add(chooser, BorderLayout.NORTH);      
    
    
    add(preview, BorderLayout.CENTER);
    
    
    setPreferredSize(new Dimension(128,128));
    
    
    new DropTarget(this, new DropHandler());
    
    
  }
  
  
  boolean accepts(Property prop) {
    return prop instanceof PropertyFile || prop instanceof PropertyBlob;
  }
  public void setPropertyImpl(Property property) {

    if (property==null)
      return;
    
    
    Origin origin = property.getGedcom().getOrigin();
    String dir = origin.getFile()!=null ? origin.getFile().getParent() : null;
    
    
    if (dir!=null) try {
      
      SecurityManager sm = System.getSecurityManager();
      if (sm!=null) 
        sm.checkPermission( new FilePermission(dir, "read"));      

      chooser.setDirectory(registry.get("bean.file.dir", dir));
      chooser.setVisible(true);
      defaultFocus = chooser;

    } catch (SecurityException se) {
      chooser.setVisible(false);
      defaultFocus = null;
    }

    
    if (property instanceof PropertyFile) {

      PropertyFile file = (PropertyFile)property;
      
      
      chooser.setTemplate(false);
      chooser.setFile(file.getValue());

      if (property.getValue().length()>0)
        preview.setSource(new ImageWidget.RelativeSource(property.getGedcom().getOrigin(), property.getValue()));
      else
        preview.setSource(null);
      
      
    }

    
    if (property instanceof PropertyBlob) {

      PropertyBlob blob = (PropertyBlob)property;

      
      chooser.setFile(blob.getValue());
      chooser.setTemplate(true);

      
      preview.setSource(new ImageWidget.ByteArraySource( ((PropertyBlob)property).getBlobData() ));

    }
      
    preview.setZoom(registry.get("file.zoom", 0)/100F);
    
    
  }

  
  public void commit(Property property) {
    
    super.commit(property);
    
    
    String value = chooser.getFile().toString();
    
    if (property instanceof PropertyFile)
      ((PropertyFile)property).setValue(value, updateMeta.isSelected());
    
    if (property instanceof PropertyBlob) 
      ((PropertyBlob)property).load(value, updateMeta.isSelected());

    
    File file = getProperty().getGedcom().getOrigin().getFile(value);
    preview.setSource(file!=null?new ImageWidget.FileSource(file):null);
    
    
  }

  
  public ViewContext getContext() {
    ViewContext result = super.getContext();
    if (result!=null) {
      result.addAction(new ActionZoom( 10));
      result.addAction(new ActionZoom( 25));
      result.addAction(new ActionZoom( 50));
      result.addAction(new ActionZoom(100));
      result.addAction(new ActionZoom(150));
      result.addAction(new ActionZoom(200));
      result.addAction(new ActionZoom(  0));
    }
    
    return result;
  }
  
  
  private class ActionZoom extends Action2 {
    
    private int zoom;
    
    protected ActionZoom(int zOOm) {
      zoom = zOOm;
      setText(zoom==0?resources.getString("file.zoom.fit"):zoom+"%");
      setEnabled(zoom != (int)(preview.getZoom()*100));
    }
    
    protected void execute() {
      preview.setZoom(zoom/100F);
      registry.put("file.zoom", zoom);
    }
  } 

  
  private class DropHandler extends DropTargetAdapter {
    
    
    public void dragEnter(DropTargetDragEvent dtde) {
      if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        dtde.acceptDrag(dtde.getDropAction());
      else
        dtde.rejectDrag();
    }
     
    
    public void drop(DropTargetDropEvent dtde) {
      try {
        dtde.acceptDrop(dtde.getDropAction());
        
        List files = (List)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        File file = (File)files.get(0);
        chooser.setFile(file);
        
        preview.setSource(new ImageWidget.FileSource(file));
        
        dtde.dropComplete(true);
        
      } catch (Throwable t) {
        dtde.dropComplete(false);
      }
    }
    
  }

} 
