
package genj.fo;

import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.swing.FileChooserWidget;
import genj.util.swing.NestedBlockLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class FormatOptionsWidget extends JPanel {
  
  private Action validAction;
  private Document doc;
  private FileChooserWidget chooseFile;
  private JComboBox chooseFormat;
  
  
  public FormatOptionsWidget(Document document, Registry registry) {
    
    setLayout(new NestedBlockLayout("<col><row><label/><file wx=\"1\"/></row><row><label/><format wx=\"1\"/></row></col>"));

    doc = document;
    
    
    chooseFile  = new FileChooserWidget();
    String file = registry.get("file", (String)null);
    if (file!=null) {
      File f = new File(file);
      chooseFile.setFile(f);
      chooseFile.setDirectory(f.getParent());
    }
    add(new JLabel("File"));
    add(chooseFile);
    
    
    chooseFormat = new JComboBox(Format.getFormats());
    chooseFormat.setSelectedItem(Format.getFormat(registry.get("format", (String)null)));
    chooseFormat.setEditable(false);
    add(new JLabel("Format"));
    add(chooseFormat);

    
    chooseFile.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        validateOptions(false);
      }
    });
    chooseFormat.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        validateOptions(true);
      }
    });
    
    
  }
  
  
  public void remember(Registry registry) {
    registry.put("format", getFormat().getFormat());
    registry.put("file", getFile().getAbsolutePath());
  }
  
  
  public Format getFormat() {
    return (Format)chooseFormat.getSelectedItem();
  }

  
  public File getFile() {
    File result = chooseFile.getFile();
    if (result.getPath().length()==0)
      return null;
    
    if (result.getParentFile()==null)
      result = new File(EnvironmentChecker.getProperty("user.home", ".", "home directory for report output"), result.getPath());
    
    Format format = getFormat();
    if (format.getFileExtension()==null)
      return  result;
    String path = result.getPath();
    Format[] formats = Format.getFormats();
    for (int f=0;f<formats.length;f++) {
      String suffix = "."+formats[f].getFileExtension();
      if (path.endsWith(suffix)) {
        path = path.substring(0, path.length()-suffix.length());
      }
    }
    
    return new File(path+"."+format.getFileExtension());
  }
  
  
  public void connect(Action validAction) {
    this.validAction = validAction;
    validateOptions(true);
  }
  
  private void validateOptions(boolean updateFilename) {
    
    Format format = getFormat();
    boolean valid = true;
    
    
    if (format.getFileExtension()!=null&&chooseFile.isEmpty())
      valid = false;
    
    
    chooseFile.setEnabled(format.getFileExtension()!=null);
    
    
    if (!format.supports(doc))
      valid = false;
    
    
    if (updateFilename)
      chooseFile.setFile(getFile());
    
    
    validAction.setEnabled(valid);
  }

} 
