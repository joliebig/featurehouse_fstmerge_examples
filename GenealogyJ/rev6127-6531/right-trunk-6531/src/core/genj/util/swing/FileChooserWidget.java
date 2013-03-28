
package genj.util.swing;

import genj.io.InputSource;
import genj.util.EnvironmentChecker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;


public class FileChooserWidget extends JPanel {

  
  private TextFieldWidget text = new TextFieldWidget("", 12);
  
  
  private Choose choose = new Choose();
  
  
  private String extensions;
  
  
  public final static String EXECUTABLES = "exe, bin, sh, cmd, bat";
  
  
  private String directory = EnvironmentChecker.getProperty("user.home", ".", "file chooser directory");
  
  
  private JComponent accessory;
  
  
  private List<ActionListener> listeners = new CopyOnWriteArrayList<ActionListener>();
  
  
  private ActionListener actionProxy = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      fireActionEvent();
    }
  };
 
  
  public FileChooserWidget() {
    this(null);
  }
  
  
  public void setEnabled(boolean set) {
    super.setEnabled(set);
    choose.setEnabled(set);
    text.setEnabled(set);
  }
  
  
  public FileChooserWidget(String extensions) {
    super(new BorderLayout());
    
    add(BorderLayout.CENTER, text );
    add(BorderLayout.EAST  , new ButtonHelper().setInsets(0).create(choose));
    this.extensions = extensions;
  }
  
  
  private void fireActionEvent() {
    ActionEvent e = new ActionEvent(this, 0, "");
    ActionListener[] ls = (ActionListener[])listeners.toArray(new ActionListener[listeners.size()]);
    for (int i = 0; i < ls.length; i++)
      ls[i].actionPerformed(e);
  }
  
  
  public void addChangeListener(ChangeListener l) {
    text.addChangeListener(l);
  }
  
  
  public void removeChangeListener(ChangeListener l) {
    text.removeChangeListener(l);
  }
  
  
  public void addActionListener(ActionListener l) {
    
    if (listeners.isEmpty())
      text.addActionListener(actionProxy);
    listeners.add(l);
  }
  
  
  public void removeActionListener(ActionListener l) {
    listeners.remove(l);
    
    if (listeners.isEmpty())
      text.removeActionListener(actionProxy);
  }
  
  
  public void setDirectory(String set) {
    directory = set;
  }
  
  
  public String getDirectory() {
    return directory;
  }
  
  
  public boolean isEmpty() {
    return text.isEmpty();
  }
  
  
  public void setTemplate(boolean set) {
    text.setTemplate(set);
  }
  
  
  public void setFile(String file) {
    text.setText(file!=null ? file : "");
  }
  
  
  public void setFile(File file) {
    
    
    
    text.setText(file!=null ? file.getPath() : "");
    if (file!=null&&file.getParentFile().isDirectory())
      setDirectory(file.getParentFile().toString());
  }
  
  
  public File getFile() {
    
    File file = new File(text.getText().trim());
    String name = file.getName();
    
    if (extensions!=null&&name.indexOf(".")<0&&extensions.indexOf(',')<0) {
      String ext = extensions.trim();
      if (name.length()>0&&!name.endsWith("."+ext))
        file = new File(file.getParentFile(), name+"."+ext);
    }
    
    return file;
  }
  
  
  public void setAccessory(JComponent set) {
    accessory = set;
  }

    
  public boolean requestFocusInWindow() {
    return text.requestFocusInWindow();
  }
  
  @Override
  public void requestFocus() {
    text.requestFocus();
  }

  
  private class Choose extends Action2 implements PropertyChangeListener {
    
    
    private Choose() {
      setText("...");
    }

        
    public void actionPerformed(ActionEvent event) {

      
      FileChooser fc = new FileChooser(FileChooserWidget.this, getName(), Action2.TXT_OK, extensions, directory);
      
      fc.setAccessory(accessory);
      fc.addPropertyChangeListener(this);
      
      File file = getFile();
      if (file.isFile())
        fc.setSelectedFile(file);
      
      fc.showDialog();
      
      
      file = fc.getSelectedFile();
      if (file!=null)  {
        
        if (extensions!=null && extensions.indexOf(',')<0) {
          String ext = extensions.trim();
          if (!file.getName().endsWith("."+ext))
            file = new File(file.getParentFile(), file.getName()+"."+ext);
        }
        
        setFile(file);
        directory = file.getParent();
        
        
        fireActionEvent();
      }
      
      
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      
      if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
        File file = (File)evt.getNewValue();
        if (accessory instanceof ThumbnailWidget)
          ((ThumbnailWidget)accessory).setSource(file!=null ? InputSource.get(file) : null);
      }
    }
    
  } 
 
} 
