

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.plt.concurrent.ConcurrentUtil;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.jar.JarBuilder;
import edu.rice.cs.util.jar.ManifestWriter;
import edu.rice.cs.util.swing.FileChooser;
import edu.rice.cs.util.swing.FileSelectorStringComponent;
import edu.rice.cs.util.swing.FileSelectorComponent;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.SwingWorker;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.swing.ProcessingDialog;
import edu.rice.cs.util.swing.ScrollableListDialog;
import edu.rice.cs.util.StreamRedirectThread;
import edu.rice.cs.util.FileOps;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.jar.Manifest;

public class JarOptionsDialog extends SwingFrame {
  
  public static class FrameState {
    private Point _loc;
    public FrameState(Point l) { _loc = l; }
    public FrameState(String s) {
      StringTokenizer tok = new StringTokenizer(s);
      try {
        int x = Integer.valueOf(tok.nextToken());
        int y = Integer.valueOf(tok.nextToken());
        _loc = new Point(x, y);
      }
      catch(NoSuchElementException nsee) { throw new IllegalArgumentException("Wrong FrameState string: " + nsee); }
      catch(NumberFormatException nfe) { throw new IllegalArgumentException("Wrong FrameState string: " + nfe); }
    }
    public FrameState(JarOptionsDialog comp) { _loc = comp.getLocation(); }
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(_loc.x);
      sb.append(' ');
      sb.append(_loc.y);
      return sb.toString();
    }
    public Point getLocation() { return _loc; }
  }
  
  static edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("JarOptionsDialog.txt", false);
  
  
  public static final int JAR_CLASSES = 1;
  public static final int JAR_SOURCES = 2;
  public static final int MAKE_EXECUTABLE = 4;
  public static final int JAR_ALL = 8;
  public static final int CUSTOM_MANIFEST = 16;
  
  
  private JCheckBox _jarClasses; 
  
  private JCheckBox _jarSources;
  
  private JCheckBox _jarAll;
  
  private JCheckBox _makeExecutable;
  
  private JCheckBox _customManifest;
  
  private FileSelectorComponent _jarFileSelector;
  
  private FileSelectorStringComponent _mainClassField;
  
  private JLabel _mainClassLabel;
  
  private JButton _editManifest;
  
  private JButton _okButton;
  
  private JButton _cancelButton;
  
  private MainFrame _mainFrame;
  
  private GlobalModel _model;
  
  private JLabel _cantJarClassesLabel;
  
  private File _rootFile;
  
  private ProcessingDialog _processingDialog;  
  
  private FrameState _lastState = null;
  
  private String _customManifestText = "";
  
  
  public FrameState getFrameState() { return _lastState; }
  
  
  public void setFrameState(FrameState ds) {
    _lastState = ds;
    if (_lastState != null) {
      setLocation(_lastState.getLocation());
      validate();
    }
  }  
  
  
  public void setFrameState(String s) {
    try { _lastState = new FrameState(s); }
    catch(IllegalArgumentException e) { _lastState = null; }
    if (_lastState != null) setLocation(_lastState.getLocation());
    else Utilities.setPopupLoc(this, _mainFrame);
    validate();
  }  
  
  
  public JarOptionsDialog(MainFrame mf) {
    super("Create Jar File from Project");
    _mainFrame = mf;
    _model = mf.getModel();
    initComponents();
    
    initDone();  
    pack();
    
    Utilities.setPopupLoc(this, _mainFrame);   
  }
  
  
  private void _loadSettings() {
    int f = _model.getCreateJarFlags();
    _jarClasses.setSelected(((f & JAR_CLASSES) != 0));
    _jarSources.setSelected(((f & JAR_SOURCES) != 0));
    _jarAll.setSelected(((f & JAR_ALL) != 0));
    _makeExecutable.setSelected(((f & MAKE_EXECUTABLE) != 0));
    _customManifest.setSelected(((f & CUSTOM_MANIFEST) != 0));
    
    LOG.log("_customManifestText set off of " + _model);
    _customManifestText = _model.getCustomManifest();
    LOG.log("\tto: " + _customManifestText);
    if(_customManifestText == null)
      _customManifestText = "";
    
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
      LOG.log("_loadSettings, rootFile=" + _rootFile);
      try {
        _rootFile = _rootFile.getCanonicalFile();
      } catch(IOException e) { }
      
      final File mc = _model.getMainClassContainingFile();
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
    _mainClassField.getFileChooser().setCurrentDirectory(_rootFile);
    
    _okButton.setEnabled(_jarSources.isSelected() || _jarClasses.isSelected() || _jarAll.isSelected());
    _setEnableExecutable(_jarClasses.isSelected());
    _setEnableCustomManifest(_jarClasses.isSelected());
  }
  
  
  private void initComponents() {
    JPanel main = _makePanel();
    super.getContentPane().setLayout(new BorderLayout());
    super.getContentPane().add(main, BorderLayout.NORTH);
    
    Action okAction = new AbstractAction("OK") { public void actionPerformed(ActionEvent e) { _ok(); } };
    _okButton = new JButton(okAction);
    
    Action cancelAction = new AbstractAction("Cancel") { public void actionPerformed(ActionEvent e) { _cancel(); } };
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
    
    
    _jarAll = new JCheckBox(new AbstractAction("Jar All files") {
      public void actionPerformed(ActionEvent e){
        _toggleClassOptions();
        _jarClasses.setEnabled(!_jarAll.isSelected());
        _jarSources.setEnabled(!_jarAll.isSelected());
        _okButton.setEnabled(_jarSources.isSelected() || _jarClasses.isSelected() || _jarAll.isSelected());
      }
    });
    
    c.weightx = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = labelInsets;
    gridbag.setConstraints(_jarAll, c);
    panel.add(_jarAll);
    
    
    _jarSources = new JCheckBox(new AbstractAction("Jar source files") {
      public void actionPerformed(ActionEvent e) {
        _jarAll.setEnabled(!_jarSources.isSelected());
        _okButton.setEnabled(_jarSources.isSelected() || _jarClasses.isSelected() || _jarAll.isSelected());
      }
    });
    
    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    
    gridbag.setConstraints(_jarSources, c);
    panel.add(_jarSources);

    
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
        _jarAll.setEnabled(!_jarClasses.isSelected());
        _okButton.setEnabled(_jarSources.isSelected() || _jarClasses.isSelected() || _jarAll.isSelected());
      }
    });
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    panel.add(_jarClasses, gridBagConstraints);

    
    JLabel spacer = new JLabel("<html>&nbsp</html>",  SwingConstants.CENTER);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    panel.add(spacer, gridBagConstraints);
    
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
    
    
    _editManifest = new JButton(new AbstractAction("Edit Manifest") {
      public void actionPerformed(ActionEvent e){
        _editManifest();
      }
    });
    _customManifest = new JCheckBox(new AbstractAction("Custom Manifest") {
      public void actionPerformed(ActionEvent e){
        _toggleCustomManifest();
      }
    });
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.gridy = 2;
    addclasses.add(_customManifest, gridBagConstraints);
    
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new Insets(0, 20, 0, 0);
    addclasses.add(_editManifest, gridBagConstraints);
    
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new Insets(0, 25, 0, 0);
    panel.add(addclasses, gridBagConstraints);
    
    return panel;
  }
  
  
  private void _editManifest(){
    final JDialog editDialog = new JDialog(this, "Custom Manifest", true);
    editDialog.setSize(300,400);
    
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    
    editDialog.setLayout(new BorderLayout());
    
    JPanel bottom = new JPanel();
    bottom.setBorder(new EmptyBorder(5, 5, 5, 5));
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
    bottom.add(Box.createHorizontalGlue());
    bottom.add(okButton);
    bottom.add(cancelButton);
    bottom.add(Box.createHorizontalGlue());
    
    editDialog.add(bottom, BorderLayout.SOUTH);
    
    final JTextArea manifest = new JTextArea();
    JScrollPane pane = new JScrollPane(manifest);
    pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    pane.getHorizontalScrollBar().setUnitIncrement(10);
    pane.getVerticalScrollBar().setUnitIncrement(10);
    
    editDialog.add(pane, BorderLayout.CENTER);
    
    manifest.setText(_customManifestText);
    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        editDialog.setVisible(false);
      }
    });
      
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        manifest.setText(_customManifestText);
        editDialog.setVisible(false);
      }
    });
    
    editDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    editDialog.addWindowListener(new WindowAdapter(){
      public void WindowClosed(WindowEvent e){
        manifest.setText(_customManifestText);
        editDialog.setVisible(false);
      }
    });
    
    
    editDialog.setLocationRelativeTo(this);
    editDialog.setVisible(true);
    
    _customManifestText = manifest.getText();
  }
  
  
  private void _toggleCustomManifest(){
    _editManifest.setEnabled(_customManifest.isSelected() && (_jarClasses.isSelected() || _jarAll.isSelected()));
    _setEnableExecutable(!_customManifest.isSelected() && (_jarClasses.isSelected() || _jarAll.isSelected()));
  }
  
  
  private JPanel _makeMainClassSelectorPanel() {
    LOG.log("_makeMainClassSelectorPanel, _rootFile=" + _rootFile);
    FileChooser chooser = new FileChooser(_rootFile);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle("Select Main Class");

    chooser.setApproveButtonText("Select");
    FileFilter filter = new FileFilter() {
      public boolean accept(File f) {
        String name = f.getName();
        return  f.isDirectory() || name.endsWith(".class");
      }
      public String getDescription() { return "Class Files (*.class)"; }
    };
    chooser.addChoosableFileFilter(filter);
    
    _mainClassField = new FileSelectorStringComponent(this, chooser, 20, 12f) {
      protected void _chooseFile() {
        _mainFrame.removeModalWindowAdapter(JarOptionsDialog.this);
        if (getText().length() == 0) {
          LOG.log("getFileChooser().setCurrentDirectory(_rootFile);");
          getFileChooser().setRoot(_rootFile);
          getFileChooser().setCurrentDirectory(_rootFile);
        }
        super._chooseFile();
        if(!getFileChooser().getSelectedFile().getAbsolutePath().startsWith(_rootFile.getAbsolutePath())){
          JOptionPane.showMessageDialog(JarOptionsDialog.this,
                                        "Main Class must be in Build Directory or one of its sub-directories.", 
                                        "Unable to set Main Class", JOptionPane.ERROR_MESSAGE);
          setText("");
        }
        _mainFrame.installModalWindowAdapter(JarOptionsDialog.this, LambdaUtil.NO_OP, CANCEL);
      }
      public File convertStringToFile(String s) { 
        s = s.trim().replace('.', java.io.File.separatorChar) + ".class";
        if (s.equals("")) return null;
        else return new File(_rootFile, s);
      }
      
      public String convertFileToString(File f) {
        if (f == null)  return "";
        else {
          try {
            String s = edu.rice.cs.util.FileOps.stringMakeRelativeTo(f, _rootFile);
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
          catch(IOException e) { return ""; }
        }
      }
    };
    _mainClassField.getTextField().getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) { setEnabled(); }
      public void removeUpdate(DocumentEvent e) { setEnabled(); }
      public void changedUpdate(DocumentEvent e) { setEnabled(); }
      private void setEnabled() { 


            assert EventQueue.isDispatchThread();
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
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    
    _jarFileSelector = new FileSelectorComponent(this, fileChooser, 20, 12f, false) {
      
      protected void _chooseFile() {
        _mainFrame.removeModalWindowAdapter(JarOptionsDialog.this);
        super._chooseFile();
        _mainFrame.installModalWindowAdapter(JarOptionsDialog.this, LambdaUtil.NO_OP, CANCEL);
      }
    };
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
  
  
  private void _setEnableCustomManifest(boolean b) {
    _customManifest.setEnabled(b);
    _toggleCustomManifest();
  }
  
  
  private void _toggleClassOptions() {
    _setEnableExecutable(_jarClasses.isSelected() || _jarAll.isSelected());
    _setEnableCustomManifest(_jarClasses.isSelected() || _jarAll.isSelected());
  }
  
  
  private void _toggleMainClass() {
    _mainClassField.setEnabled(_makeExecutable.isSelected() && (_jarClasses.isSelected() || _jarAll.isSelected()));
    _mainClassLabel.setEnabled(_makeExecutable.isSelected() && (_jarClasses.isSelected() || _jarAll.isSelected()));
    
    _customManifest.setEnabled(!_makeExecutable.isSelected() && (_jarClasses.isSelected() || _jarAll.isSelected()));
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
    _processingDialog = new ProcessingDialog(this, "Creating Jar File", "Processing, please wait.");
    _processingDialog.setVisible(true);
    SwingWorker worker = new SwingWorker() {
      boolean _success = false;
      HashSet<String> _exceptions = new HashSet<String>();
      
      private boolean jarAll(File dir, JarBuilder jarFile, final File outputFile) throws IOException {
        LOG.log("jarOthers(" + dir + " , " + jarFile + ")");
        java.io.FileFilter allFilter = new java.io.FileFilter() {
          public boolean accept(File f) {
            return !outputFile.equals(f);
          }
        };
        
        File[] files = dir.listFiles(allFilter);
        
        if(files != null) {
          for(int i = 0; i < files.length; i++){
            try {
              if(files[i].isDirectory()){
                LOG.log("jarFile.addDirectoryRecursive(" + files[i] + ")");
                jarFile.addDirectoryRecursive(files[i], files[i].getName(), allFilter);
              }else{
                LOG.log("jarFile.addFile(" + files[i] + ")");
                jarFile.addFile(files[i], "", files[i].getName());
              }
            }
            catch(IOException ioe) { _exceptions.add(ioe.getMessage()); }
          }
        }
        
        return true;
      }
      
      
      private boolean jarBuildDirectory(File dir, JarBuilder jarFile) throws IOException {
      LOG.log("jarBuildDirectory(" + dir + " , " + jarFile + ")");
        
        java.io.FileFilter classFilter = new java.io.FileFilter() {
          public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".class");
          }
        };
        
        File[] files = dir.listFiles(classFilter);
        
        LOG.log("\tfiles = " + files);
        
        if (files != null) { 
          for (int i = 0; i < files.length; i++) {
            LOG.log("\t\tfiles[" + i + "] = " + files[i]);
            
            if(files[i] == null || !files[i].exists()) continue;
            
            try {
              if (files[i].isDirectory()) {
                LOG.log("jarFile.addDirectoryRecursive(" + files[i] + ")");
                jarFile.addDirectoryRecursive(files[i], files[i].getName(), classFilter);
              }
              else {
                LOG.log("jarFile.addFile(" + files[i] + ")");
                jarFile.addFile(files[i], "", files[i].getName());
              }
            }
            catch(IOException ioe) { _exceptions.add(ioe.getMessage()); }
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
            catch(IOException ioe) { _exceptions.add(ioe.getMessage()); }
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
          if (! jarOut.exists()) jarOut.createNewFile();  

          if ((_jarClasses.isSelected() && _jarSources.isSelected()) || _jarAll.isSelected()) {
            LOG.log("(_jarClasses.isSelected() && _jarSources.isSelected()) || _jarAll.isSelected()");
            JarBuilder mainJar = null;
            if (_makeExecutable.isSelected() || _customManifest.isSelected()) {
              ManifestWriter mw = new ManifestWriter();
              
              if(_makeExecutable.isSelected())
                mw.setMainClass(_mainClassField.getText());
              else
                mw.setManifestContents(_customManifestText);
              
              mainJar = new JarBuilder(jarOut, mw.getManifest());
            }
            else {
              mainJar = new JarBuilder(jarOut);
            }
            
            
            
            File binRoot = _model.getBuildDirectory();
            if(binRoot == null || binRoot == FileOps.NULL_FILE || binRoot.toString().trim().length() == 0)
              binRoot = _model.getProjectRoot();
            
            if(!_jarAll.isSelected())
              jarBuildDirectory(binRoot, mainJar);
            
            
            
            
            String prefix = _model.getBuildDirectory().getName();
            if(prefix.length() < 3)
              prefix = "drjava_tempSourceJar";
            
            File sourceJarFile = File.createTempFile(prefix, ".jar");
            
            if(!_jarAll.isSelected()){
              JarBuilder sourceJar = new JarBuilder(sourceJarFile);
              jarSources(_model, sourceJar);
              sourceJar.close();
              mainJar.addFile(sourceJarFile, "", "source.jar");
            }
            
            if(_jarAll.isSelected()){
              LOG.log("jarAll");
              LOG.log("binRoot=" + binRoot);
              LOG.log("root=" + _model.getProjectRoot());
              LOG.log("FileOps.isAncestorOf(_model.getProjectRoot(),binRoot)=" + FileOps.isAncestorOf(_model.getProjectRoot(),binRoot));
              LOG.log("mainJar=" + mainJar);
              LOG.log("jarOut=" + jarOut);
              jarAll(_model.getProjectRoot(), mainJar, jarOut);
              if(!_model.getProjectRoot().equals(binRoot))
                LOG.log("jarBuildDirectory");
                jarBuildDirectory(binRoot, mainJar);
            }
            
            mainJar.close();
            sourceJarFile.delete();  
          }
          else if (_jarClasses.isSelected()) {
            JarBuilder jb;
            if (_makeExecutable.isSelected() || _customManifest.isSelected()) {
              ManifestWriter mw = new ManifestWriter();
              if(_makeExecutable.isSelected())
                mw.setMainClass(_mainClassField.getText());
              else
                mw.setManifestContents(_customManifestText);
              
              Manifest m = mw.getManifest();
              
              if(m != null)
                jb = new JarBuilder(jarOut, m);
              else
                throw new IOException("Manifest is malformed");
            }
            else {
              jb = new JarBuilder(jarOut);
            }
            
            
            File binRoot = _model.getBuildDirectory();
            if(binRoot == null || binRoot == FileOps.NULL_FILE || binRoot.toString().trim().length() == 0)
              binRoot = _model.getProjectRoot();
            
            jarBuildDirectory(binRoot, jb);
            
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
          
          LOG.log("construct: " + e, e.getStackTrace());
        }
        return null;
      }
      public void finished() {
        _processingDialog.setVisible(false);
        _processingDialog.dispose();
        JarOptionsDialog.this.setEnabled(true);
        if (_success) {
          if (_exceptions.size() > 0) {
            ScrollableListDialog<String> dialog = new ScrollableListDialog.Builder<String>()
              .setOwner(JarOptionsDialog.this)
              .setTitle("Problems Creating Jar")
              .setText("There were problems creating this jar file, but DrJava was probably able to recover.")
              .setItems(new ArrayList<String>(_exceptions))
              .setMessageType(JOptionPane.ERROR_MESSAGE)
              .build();
            
            Utilities.setPopupLoc(dialog, JarOptionsDialog.this);
            dialog.showDialog();
          }
          if ((_jarAll.isSelected() || _jarClasses.isSelected()) && _makeExecutable.isSelected()) {
            Object[] options = { "OK", "Run" };
            int res = JOptionPane.showOptionDialog(JarOptionsDialog.this, "Jar file successfully written to '" + _jarFileSelector.getFileFromField().getName() + "'",
                                                   "Jar Creation Successful", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                                   null, options, options[0]);
            JarOptionsDialog.this.setVisible(false);
            if (1==res) {
              SwingWorker jarRunner = new SwingWorker() {
                public Object construct() {
                  try {
                    File cp = _jarFileSelector.getFileFromField();
                    File wd = cp.getParentFile();
                    Process p = JVMBuilder.DEFAULT.classPath(cp).directory(wd).start(_mainClassField.getText());
                    ConcurrentUtil.copyProcessErr(p, System.err);
                    ConcurrentUtil.copyProcessOut(p, System.out);
                    p.waitFor();
                    JOptionPane.showMessageDialog(JarOptionsDialog.this,"Execution of jar file terminated (exit value = " + 
                                                  p.exitValue() + ")", "Execution terminated.",
                                                  JOptionPane.INFORMATION_MESSAGE);
                  }
                  catch(Exception e) {
                    JOptionPane.showMessageDialog(JarOptionsDialog.this, "An error occured while running the jar file: \n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                  }
                  finally {
                    JarOptionsDialog.this.setVisible(false);
                  }
                  return null;
                }
              };
              jarRunner.start();
            }
          }
          else {
            JOptionPane.showMessageDialog(JarOptionsDialog.this, "Jar file successfully written to '" + _jarFileSelector.getFileFromField().getName() + "'", "Jar Creation Successful", JOptionPane.INFORMATION_MESSAGE);
            JarOptionsDialog.this.setVisible(false);
          }
        }
        else {
          ManifestWriter mw = new ManifestWriter();
          if(_makeExecutable.isSelected())
                mw.setMainClass(_mainClassField.getText());
              else
                mw.setManifestContents(_customManifestText);
              
          Manifest m = mw.getManifest();
          
          if(m != null){
            if (_exceptions.size() > 0) {
              ScrollableListDialog<String> dialog = new ScrollableListDialog.Builder<String>()
                .setOwner(JarOptionsDialog.this)
                .setTitle("Error Creating Jar")
                .setText("<html>An error occured while creating the jar file. This could be because the file<br>" + 
                         "that you are writing to or the file you are reading from could not be opened.</html>")
                .setItems(new ArrayList<String>(_exceptions))
                .setMessageType(JOptionPane.ERROR_MESSAGE)
                .build();
              
              Utilities.setPopupLoc(dialog, JarOptionsDialog.this);
              dialog.showDialog();
            }
            else {
              JOptionPane.showMessageDialog(JarOptionsDialog.this, 
                                            "An error occured while creating the jar file. This could be because the file that you " + 
                                            "are writing to or the file you are reading from could not be opened.", 
                                            "Error Creating Jar",
                                            JOptionPane.ERROR_MESSAGE);
            }
          }
          else {
            if (_exceptions.size() > 0) {
              ScrollableListDialog<String> dialog = new ScrollableListDialog.Builder<String>()
                .setOwner(JarOptionsDialog.this)
                .setTitle("Error Creating Jar")
                .setText("The supplied manifest does not conform to the 1.0 Manifest format specification")
                .setItems(new ArrayList<String>(_exceptions))
                .setMessageType(JOptionPane.ERROR_MESSAGE)
                .build();
              
              Utilities.setPopupLoc(dialog, JarOptionsDialog.this);
              dialog.showDialog();
            }
            else {
              JOptionPane.showMessageDialog(JarOptionsDialog.this, "The supplied manifest does not conform to the 1.0 Manifest format specification.",
                                            "Error Creating Jar",
                                            JOptionPane.ERROR_MESSAGE);
            }
          }
          JarOptionsDialog.this.setVisible(false);  
        }
        _model.refreshActiveDocument();
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
    if (_jarAll.isSelected()) f |= JAR_ALL;
    if (_makeExecutable.isSelected()) f |= MAKE_EXECUTABLE;
    if (_customManifest.isSelected()) f |= CUSTOM_MANIFEST;
    
    if (f != _model.getCreateJarFlags()) {
      _model.setCreateJarFlags(f);
    }
    
    String currentManifest = _model.getCustomManifest();
    
    if(currentManifest == null || !(currentManifest.equals(_customManifestText))){
      LOG.log("Updated Manifest on: " + _model);
      _model.setCustomManifest(_customManifestText);
    }
    
    return true;
  }
  
  
  protected final Runnable1<WindowEvent> CANCEL = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { _cancel(); }
  };
  
  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
      ProcessingDialog pf = new ProcessingDialog(this, "Checking class files", "Processing, please wait.");
      pf.setVisible(true);
      _loadSettings();
      pf.setVisible(false);
      pf.dispose();
      toFront();
    }
    else {
      _mainFrame.removeModalWindowAdapter(this);
      _mainFrame.hourglassOff();
      _mainFrame.toFront();
    }
    super.setVisible(vis);
  }  
}
