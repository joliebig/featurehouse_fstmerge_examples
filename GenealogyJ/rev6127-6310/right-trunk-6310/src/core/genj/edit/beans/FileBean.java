
package genj.edit.beans;

import genj.edit.actions.RunExternal;
import genj.gedcom.Property;
import genj.gedcom.PropertyBlob;
import genj.gedcom.PropertyFile;
import genj.io.InputSource;
import genj.util.Origin;
import genj.util.swing.FileChooserWidget;
import genj.util.swing.ThumbnailWidget;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilePermission;
import java.util.List;

import javax.swing.JCheckBox;


public class FileBean extends PropertyBean {
  
  
  private JCheckBox updateMeta = new JCheckBox(RESOURCES.getString("file.update"), true);
  
  
  private FileChooserWidget chooser = new FileChooserWidget();
  
  
  private ThumbnailWidget preview = new ThumbnailWidget() {
    @Override
    protected void handleDrop(List<File> files) {
      if (files.size()==1) {
        File file = files.get(0);
        chooser.setFile(file);
        setSource(InputSource.get(file));
      }
    }
  };
  
  private transient ActionListener doPreview = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      
      
      REGISTRY.put("bean.file.dir", chooser.getDirectory());
      
      
      File file = getProperty().getGedcom().getOrigin().getFile(chooser.getFile().toString());
      if (file==null) {
        preview.setSource(null);
        return;
      }
      preview.setSource(InputSource.get(file));
      
      
      String relative = getProperty().getGedcom().getOrigin().calcRelativeLocation(file.getAbsolutePath());
      if (relative!=null)
        chooser.setFile(relative);
      
      
    }
  };
  
  public FileBean() {
    
    setLayout(new BorderLayout());
    
    
    chooser.setAccessory(updateMeta);
    chooser.addChangeListener(changeSupport);
    chooser.addActionListener(doPreview);

    add(chooser, BorderLayout.NORTH);      
    
    
    add(preview, BorderLayout.CENTER);
    
    
    defaultFocus = chooser;
  }
  
  
  public void setPropertyImpl(Property property) {

    
    Origin origin = getRoot().getGedcom().getOrigin();
    String dir = origin.getFile()!=null ? origin.getFile().getParent() : null;
    
    
    if (dir!=null) try {
      
      SecurityManager sm = System.getSecurityManager();
      if (sm!=null) 
        sm.checkPermission( new FilePermission(dir, "read"));      

      chooser.setDirectory(REGISTRY.get("bean.file.dir", dir));
      chooser.setVisible(true);
      defaultFocus = chooser;

    } catch (SecurityException se) {
      chooser.setVisible(false);
      defaultFocus = null;
    }

    preview.setSource(null);
    
    
    if (property instanceof PropertyFile) {

      PropertyFile file = (PropertyFile)property;
      
      
      chooser.setTemplate(false);
      chooser.setFile(file.getValue());

      if (property.getValue().length()>0)
        preview.setSource(InputSource.get(property.getGedcom().getOrigin().getFile(file.getValue())));
      
      
    }

    
    if (property instanceof PropertyBlob) {

      PropertyBlob blob = (PropertyBlob)property;

      
      chooser.setFile(blob.getValue());
      chooser.setTemplate(true);

      
      preview.setSource(InputSource.get(blob.getPropertyName(), ((PropertyBlob)property).getBlobData() ));

    }
      
    
  }

  
  protected void commitImpl(Property property) {
    
    
    String value = chooser.getFile().toString();
    
    if (property instanceof PropertyFile)
      ((PropertyFile)property).setValue(value, updateMeta.isSelected());
    
    if (property instanceof PropertyBlob) 
      ((PropertyBlob)property).load(value, updateMeta.isSelected());

    
    File file = getProperty().getGedcom().getOrigin().getFile(value);
    preview.setSource(file!=null?InputSource.get(file):null);
    
    
  }

  
  public ViewContext getContext() {
    ViewContext result = super.getContext();
    if (result!=null) {
      PropertyFile file = (PropertyFile)getProperty();
      if (file!=null) 
        result.addAction(new RunExternal(file.getFile()));
    }
    
    return result;
  }
  

} 
