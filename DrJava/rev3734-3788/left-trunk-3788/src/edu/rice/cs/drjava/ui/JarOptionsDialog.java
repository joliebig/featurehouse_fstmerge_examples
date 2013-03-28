

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.jar.JarBuilder;
import edu.rice.cs.util.jar.ManifestWriter;
import edu.rice.cs.util.swing.FileChooser;
import edu.rice.cs.util.swing.FileSelectorStringComponent;
import edu.rice.cs.util.swing.FileSelectorComponent;
import edu.rice.cs.util.swing.SwingWorker;
import edu.rice.cs.util.newjvm.ExecJVM;
import edu.rice.cs.util.StreamRedirectThread;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

public class JarOptionsDialog extends JFrame {
  
  public static class FrameState {
    private Point _loc;
    public FrameState(Point l) {
      _loc = l;
    }
    public FrameState(String s) {
      StringTokenizer tok = new StringTokenizer(s);
      try {
        int x = Integer.valueOf(tok.nextToken());
        int y = Integer.valueOf(tok.nextToken());
        _loc = new Point(x, y);
      }
      catch(NoSuchElementException nsee) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nsee);
      }
      catch(NumberFormatException nfe) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nfe);
      }
    }
    public FrameState(JarOptionsDialog comp) {
      _loc = comp.getLocation();
    }
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(_loc.x);
      sb.append(' ');
      sb.append(_loc.y);
      return sb.toString();
    }
    public Point getLocation() { return _loc; }
  }
  
  
  public static final int JAR_CLASSES = 1;
  public static final int JAR_SOURCES = 2;
  public static final int MAKE_EXECUTABLE = 4;
  
  
  private JCheckBox _jarClasses; 
  
  private JCheckBox _jarSources;
  
  private JCheckBox _makeExecutable;
  
  private FileSelectorComponent _jarFileSelector;
  
  private FileSelectorStringComponent _mainClassField;
  
  private JLabel _mainClassLabel;
  
  private JButton _okButton;
  
  private JButton _cancelButton;
  
  private MainFrame _mainFrame;
  
  private GlobalModel _model;
  
  private JLabel _cantJarClassesLabel;
  
  private File _rootFile;
  
  private ProcessingFrame _processingFrame;  
  
  private FrameState _lastState = null;

  
  
  public FrameState getFrameState() { return _lastState; }
  
  
  public void setFrameState(FrameState ds) {
    _lastState = ds;
    if (_lastState!=null) {
      setLocation(_lastState.getLocation());
      validate();
    }
  }  
  
  
  public void setFrameState(String s) {
    try { _lastState = new FrameState(s); }
    catch(IllegalArgumentException e) { _lastState = null; }
    if (_lastState!=null) {
      setLocation(_lastState.getLocation());
    }
    else {
      setLocationRelativeTo(_mainFrame);
    }
    validate();
  }
  
  
  private static class ProcessingFrame extends JFrame {
    private Component _parent;
    public ProcessingFrame(Component parent, String title, String label) {
      super(title);
      _parent = parent;
      setSize(350, 150);
      setLocationRelativeTo(parent);
      JLabel waitLabel = new JLabel(label, SwingConstants.CENTER);
      getRootPane().setLayout(new BorderLayout());
      getRootPane().add(waitLabel, BorderLayout.CENTER);
    }
    public void setVisible(boolean vis) {
      setLocation((int)(_parent.getLocation().getX() + (_parent.getSize().width - getSize().width)/2),
                  (int)(_parent.getLocation().getY() + (_parent.getSize().height - getSize().height)/2));
      super.setVisible(vis);
    }
  }

  
  public JarOptionsDialog(MainFrame mf) {
    super("Create Jar File from Project");
    _mainFrame = mf;
    _model = mf.getModel();
    initComponents();
  }

  
  private void _loadSettings() {
    int f = _model.getCreateJarFlags();
    _jarClasses.setSelected(((f & JAR_CLASSES) != 0));
    _jarSources.setSelected(((f & JAR_SOURCES) != 0));
    _makeExecutable.setSelected(((f & MAKE_EXECUTABLE) != 0));
    
    boolean outOfSync = true;
    if (_model.getBuildDirectory() != null) {
      outOfSync = _model.hasOutOfSyncDocuments();
    }
    if ((_model.getBuildDirectory() == null) || (outOfSync)) {
      _jarClasses.setSelected(false);
      _jarClasses.setEnabled(false);
      String s;
      if ((_model.getBuildDirectory() == null) && (outOfSync)) {
        s = "<html><center>A build directory must be specified in order to jar class files,<br>and the project needs to be compiled.</center></html>";
      }
      else
      if (_model.getBuildDirectory() == null) {
        s = "<html>A build directory must be specified in order to jar class files.</html>";
      }
      else {
        s = "<html>The project needs to be compiled.</html>";
      }
      _cantJarClassesLabel.setText(s);
    }
    else {
      _jarClasses.setEnabled(true);
      _cantJarClassesLabel.setText(" ");

      
      _rootFile = _model.getBuildDirectory();
      try {
        _rootFile = _rootFile.getCanonicalFile();
      } catch(IOException e) { }
    
      FileChooser chooser = new FileChooser(_rootFile);
      chooser.setDialogTitle("Select Main Class");

      chooser.setApproveButtonText("Select");
      FileFilter filter = new FileFilter() {
        public boolean accept(File f) {
          String name = f.getName();
          return  !f.isDirectory() && name.endsWith(".class");
        }
        public String getDescription() { return "Class Files (*.class)"; }
      };
      chooser.addChoosableFileFilter(filter);



      _mainClassField.setFileChooser(chooser);
      
      final File mc = _model.getMainClass();
      if (mc == null)  _mainClassField.setText("");
      else {
        try {
          OpenDefinitionsDocument mcDoc = _model.getDocumentForFile(mc);
          _mainClassField.setText(mcDoc.getQualifiedClassName());
        }
        catch(IOException ioe) { _mainClassField.setText(""); }
        catch(edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException e) { _mainClassField.setText(""); }
      }
    }
    
    _jarFileSelector.setFileField(_model.getCreateJarFile());
    
    _okButton.setEnabled(_jarSources.isSelected() || _jarClasses.isSelected());
    _setEnableExecutable(_jarClasses.isSelected());
  }

  
  private void initComponents() {
    JPanel main = _makePanel();
    super.getContentPane().setLayout(new BorderLayout());
    super.getContentPane().add(main, BorderLayout.NORTH);

    Action okAction = new AbstractAction("OK") {
      public void actionPerformed(ActionEvent e) {
        _ok();
      }
    };
    _okButton = new JButton(okAction);

    Action cancelAction = new AbstractAction("Cancel") {
      public void actionPerformed(ActionEvent e) {
        _cancel();
      }
    };
    _cancelButton = new JButton(cancelAction);

    
    JPanel bottom = new JPanel();
    bottom.setBorder(new EmptyBorder(5, 5, 5, 5));
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
    bottom.add(Box.createHorizontalGlue());
    bottom.add(_okButton);
    bottom.add(_cancelButton);
    bottom.add(Box.createHorizontalGlue());

    super.getContentPane().add(bottom, BorderLayout.SOUTH);
    super.setResizable(false);
    pack();
    
    setLocationRelativeTo(_mainFrame);

    _processingFrame = new ProcessingFrame(this, "Creating Jar File", "Processing, please wait.");
  }

  
  private JPanel _makePanel() {
    JPanel panel = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    panel.setLayout(gridbag);
    c.fill = GridBagConstraints.HORIZONTAL;
    Insets labelInsets = new Insets(5, 10, 0, 10);
    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = labelInsets;
    c.fill = GridBagConstraints.HORIZONTAL;

    JPanel jarClassesPanel = _makeClassesPanel();
    gridbag.setConstraints(jarClassesPanel, c);
    panel.add(jarClassesPanel);

    _cantJarClassesLabel = new JLabel("<html><center>A build directory must be specified in order to jar class files,<br>and the project needs to be compiled.</center></html>",  SwingConstants.CENTER);
    c.gridx = 0;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    gridbag.setConstraints(jarClassesPanel, c);
    panel.add(_cantJarClassesLabel);
    
    
    _jarSources = new JCheckBox(new AbstractAction("Jar source files") {
      public void actionPerformed(ActionEvent e) {
        _okButton.setEnabled(_jarSources.isSelected() || _jarClasses.isSelected());
      }
    });

    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    gridbag.setConstraints(_jarSources, c);
    panel.add(_jarSources);

    
    c.gridx = 0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    JLabel label = new JLabel("Jar File");
    label.setToolTipText("The file that the jar should be written to.");
    gridbag.setConstraints(label, c);
    panel.add(label);

    c.weightx = 1.0;
    c.gridx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = labelInsets;

    JPanel jarFilePanel = _makeJarFileSelector();
    gridbag.setConstraints(jarFilePanel, c);
    panel.add(jarFilePanel);

    return panel;
  }

  
  private JPanel _makeClassesPanel() {
    JPanel panel = new JPanel();
    GridBagConstraints gridBagConstraints;
    panel.setLayout(new GridBagLayout());
    
    _jarClasses = new JCheckBox(new AbstractAction("Jar classes") {
      public void actionPerformed(ActionEvent e) {
        _toggleClassOptions();
        _okButton.setEnabled(_jarSources.isSelected() || _jarClasses.isSelected());
      }
    });
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    panel.add(_jarClasses, gridBagConstraints);

    JPanel addclasses = new JPanel();
    addclasses.setLayout(new GridBagLayout());
    _makeExecutable = new JCheckBox(new AbstractAction("Make executable") {
      public void actionPerformed(ActionEvent e) {
        _toggleMainClass();        
      }
    });
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    addclasses.add(_makeExecutable, gridBagConstraints);
    
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new Insets(0, 20, 0, 0);
    addclasses.add(_makeMainClassSelectorPanel(), gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new Insets(0, 25, 0, 0);
    panel.add(addclasses, gridBagConstraints);

    return panel;
  }
 
  
  private JPanel _makeMainClassSelectorPanel() {
    _mainClassField = new FileSelectorStringComponent(this, null, 20, 12f) {
        public File convertStringToFile(String s) { 
          s = s.trim().replace('.', java.io.File.separatorChar) + ".class";
          if (s.equals("")) return null;
          else return new File(_rootFile, s);
        }
        
        public String convertFileToString(File f) {
          if (f == null)  return "";
          else {
            try {
              String s = edu.rice.cs.util.FileOps.makeRelativeTo(f, _rootFile).toString();
              s = s.substring(0, s.lastIndexOf(".class"));
              s = s.replace(java.io.File.separatorChar, '.').replace('$', '.');
              int pos = 0;
              boolean ok = true;
              while((pos = s.indexOf('.', pos)) >= 0) {
                if ((s.length() <= pos + 1) || (Character.isDigit(s.charAt(pos + 1)))) {
                  ok = false;
                  break;
                }
                ++pos;
              }
              if (ok) return s;
              return "";
            }
            catch(Exception e) { return ""; }
          }
        }
    };
    _mainClassField.getTextField().getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        _okButton.setEnabled(true);
      }
      public void removeUpdate(DocumentEvent e) {
        _okButton.setEnabled(true);
      }
      public void changedUpdate(DocumentEvent e) {
        _okButton.setEnabled(true);
      }
    });
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    _mainClassLabel = new JLabel("Main class:  ");
    _mainClassLabel.setLabelFor(_mainClassField);
    p.add(_mainClassLabel, BorderLayout.WEST);
    p.add(_mainClassField, BorderLayout.CENTER);
    return p;
  }


  
  private JPanel _makeJarFileSelector() {
    JFileChooser fileChooser = new JFileChooser(_model.getBuildDirectory());
    fileChooser.setDialogTitle("Select Jar Output File");
    fileChooser.setApproveButtonText("Select");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);

    _jarFileSelector = new FileSelectorComponent(this, fileChooser, 20, 12f, false);
    _jarFileSelector.setFileFilter(new FileFilter() {
      public boolean accept(File f) { return f.getName().endsWith(".jar") || f.isDirectory(); }
      public String getDescription() { return "Java Archive Files (*.jar)"; }
    });

    return _jarFileSelector;
  }

  
  private void _setEnableExecutable(boolean b) {
    _makeExecutable.setEnabled(b);
    _toggleMainClass();
  }
 
  
  private void _toggleClassOptions() {
    _setEnableExecutable(_jarClasses.isSelected());
  }

  
  private void _toggleMainClass() {
    _mainClassField.setEnabled(_makeExecutable.isSelected() && _jarClasses.isSelected());
    _mainClassLabel.setEnabled(_makeExecutable.isSelected() && _jarClasses.isSelected());
  }

  
  private void _cancel() {
    _lastState = new FrameState(this);
    this.setVisible(false);
  }

  
  private void _ok() {
    
    _saveSettings();

    File jarOut = _jarFileSelector.getFileFromField();
    if (jarOut == null) {
      JOptionPane.showMessageDialog(JarOptionsDialog.this,
                                    "You must specify an output file",
                                    "Error: No File Specified",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }
    else if (jarOut.exists()) {
      if (JOptionPane.showConfirmDialog(JarOptionsDialog.this,
                                        "Are you sure you want to overwrite the file '" + jarOut.getPath() + "'?",
                                        "Overwrite file?",
                                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        
        return;
      }
    }

    setEnabled(false);
    _processingFrame.setVisible(true);
    SwingWorker worker = new SwingWorker() {
      boolean _success = false;

      
      private boolean jarBuildDirectory(File dir, JarBuilder jarFile) throws IOException {
        java.io.FileFilter classFilter = new java.io.FileFilter() {
          public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".class");
          }
        };

        File[] files = dir.listFiles(classFilter);
        if (files!=null) { 
          for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
              jarFile.addDirectoryRecursive(files[i], files[i].getName(), classFilter);
            }
            else {
              jarFile.addFile(files[i], "", files[i].getName());
            }
          }
        }

        return true;
      }

      
      private boolean jarSources(GlobalModel model, JarBuilder jar) {
        List<OpenDefinitionsDocument> srcs = model.getProjectDocuments();

        Iterator<OpenDefinitionsDocument> iter = srcs.iterator();
        while (iter.hasNext()) {
          OpenDefinitionsDocument doc = iter.next();
          if (doc.inProject() && ! doc.isAuxiliaryFile()) {
            try {
              
              jar.addFile(doc.getFile(), packageNameToPath(doc.getPackageName()), doc.getFileName());
            }
            catch (IOException e) {
              e.printStackTrace();
              throw new UnexpectedException(e);
            }
          }
        }
        return true;
      }

      
      private String packageNameToPath(String packageName) {
        return packageName.replaceAll("\\.", System.getProperty("file.separator").replaceAll("\\\\", "\\\\\\\\"));
      }
      
      public Object construct() {
        try {
          File jarOut = _jarFileSelector.getFileFromField();
          if (!jarOut.exists()) {
            jarOut.createNewFile();
          }
          
          if (_jarClasses.isSelected() && _jarSources.isSelected()) {
            JarBuilder mainJar = null;
            if (_makeExecutable.isSelected()) {
              ManifestWriter mw = new ManifestWriter();
              mw.setMainClass(_mainClassField.getText());
              mainJar = new JarBuilder(jarOut, mw.getManifest());
            }
            else {
              mainJar = new JarBuilder(jarOut);
            }
            
            jarBuildDirectory(_model.getBuildDirectory(), mainJar);
            
            File sourceJarFile = File.createTempFile(_model.getBuildDirectory().getName(), ".jar");
            JarBuilder sourceJar = new JarBuilder(sourceJarFile);
            jarSources(_model, sourceJar);
            sourceJar.close();
            mainJar.addFile(sourceJarFile, "", "source.jar");
            
            mainJar.close();
            sourceJarFile.delete();
          }
          else if (_jarClasses.isSelected()) {
            JarBuilder jb;
            if (_makeExecutable.isSelected()) {
              ManifestWriter mw = new ManifestWriter();
              mw.setMainClass(_mainClassField.getText());
              jb = new JarBuilder(jarOut, mw.getManifest());
            }
            else {
              jb = new JarBuilder(jarOut);
            }
            jarBuildDirectory(_model.getBuildDirectory(), jb);
            jb.close();
          }
          else {
            JarBuilder jb = new JarBuilder(jarOut);
            jarSources(_model, jb);
            jb.close();
          }
          _success = true;
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
      public void finished() {
        _processingFrame.setVisible(false);
        JarOptionsDialog.this.setEnabled(true);
        if (_success) {
          if (_makeExecutable.isSelected()) {
             Object[] options = { "OK", "Run" };
             int res = JOptionPane.showOptionDialog(JarOptionsDialog.this, "Jar file successfully written to '"+_jarFileSelector.getFileFromField().getName()+"'",
                                                    "Jar Creation Successful", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                                    null, options, options[0]);
             JarOptionsDialog.this.setVisible(false);
             if (1==res) {
               SwingWorker jarRunner = new SwingWorker() {
                 public Object construct() {
                   try {
                     Process jarFileProcess = ExecJVM.runJVM(_mainClassField.getText(), 
                                                             new String[] {}, 
                                                             new String[] { _jarFileSelector.getFileFromField().getAbsolutePath() }, 
                                                             new String[] {}, 
                                                             _jarFileSelector.getFileFromField().getParentFile());
                                                             
                     StreamRedirectThread errThread = new StreamRedirectThread("error reader", jarFileProcess.getErrorStream(), System.err);
                     StreamRedirectThread outThread = new StreamRedirectThread("output reader", jarFileProcess.getInputStream(), System.out);
                     errThread.start();
                     outThread.start();
                     boolean notDead = true;
                     while(notDead) {
                       try {
                         errThread.join();
                         outThread.join();
                         notDead = false;
                       }
                       catch (InterruptedException exc) {
                         
                       }
                     }
                     jarFileProcess.waitFor();
                     JOptionPane.showMessageDialog(JarOptionsDialog.this,"Execution of jar file terminated (exit value = "+
                                                   jarFileProcess.exitValue()+")", "Execution terminated.",
                                                   JOptionPane.INFORMATION_MESSAGE);
                   }
                   catch(Exception e) {
                     JOptionPane.showMessageDialog(JarOptionsDialog.this, "An error occured while running the jar file: \n"+e, "Error", JOptionPane.ERROR_MESSAGE);
                   }
                   return null;
                 }
               };
               jarRunner.start();
             }
          }
          else {
            JOptionPane.showMessageDialog(JarOptionsDialog.this,"Jar file successfully written to '"+_jarFileSelector.getFileFromField().getName()+"'", "Jar Creation Successful", JOptionPane.INFORMATION_MESSAGE);
            JarOptionsDialog.this.setVisible(false);
          }
        }
        else {
          JOptionPane.showMessageDialog(JarOptionsDialog.this, "An error occured while creating the jar file. This could be because the file that you are writing to or the file you are reading from could not be opened.", "Error: File Access", JOptionPane.ERROR_MESSAGE);
          JarOptionsDialog.this.setVisible(false);
        }
      }
    };
    worker.start();
  }

  
  private boolean _saveSettings() {
    _lastState = new FrameState(this);
    if ((_model.getCreateJarFile() == null) ||
        (!_model.getCreateJarFile().getName().equals(_jarFileSelector.getFileFromField().getName()))) {
      _model.setCreateJarFile(_jarFileSelector.getFileFromField());
    }
    int f = 0;
    if (_jarClasses.isSelected()) f |= JAR_CLASSES;
    if (_jarSources.isSelected()) f |= JAR_SOURCES;
    if (_makeExecutable.isSelected()) f |= MAKE_EXECUTABLE;
    if (f!=_model.getCreateJarFlags()) {
      _model.setCreateJarFlags(f);
    }
    return true;
  }

  
  public void setVisible(boolean vis) {
    validate();
    _mainFrame.setEnabled(!vis);
    if (vis) {
      ProcessingFrame pf = new ProcessingFrame(this, "Checking class files", "Processing, please wait.");
      pf.setVisible(true);
      _loadSettings();
      pf.setVisible(false);
    }
    super.setVisible(vis);
  }  
}
