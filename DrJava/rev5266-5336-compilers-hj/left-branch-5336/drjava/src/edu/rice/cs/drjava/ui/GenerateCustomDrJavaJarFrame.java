

package edu.rice.cs.drjava.ui;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

import edu.rice.cs.util.*;
import edu.rice.cs.util.swing.FileSelectorComponent;
import edu.rice.cs.drjava.ui.config.VectorFileOptionComponent;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.config.OptionConstants;

import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.io.IOUtil;
  
import javax.swing.filechooser.FileFilter;


public class GenerateCustomDrJavaJarFrame extends SwingFrame {

  private static final int FRAME_WIDTH = 503;
  private static final int FRAME_HEIGHT = 500;
  
  
  private static final int INFO_DIALOG_WIDTH = 850;
  private static final int INFO_DIALOG_HEIGHT = 550;

  private MainFrame _mainFrame;

  private final JButton _generateButton;
  private final JButton _checkButton;
  private final JButton _closeButton;
  private JPanel _mainPanel;
  
  
  private final File _drjavaFile = FileOps.getDrJavaFile();

  
  private FileSelectorComponent _jarFileSelector;
  
  
  private VectorFileOptionComponent _sourcesList;
  
  
  public GenerateCustomDrJavaJarFrame(MainFrame mf) {
    super("Generate Custom drjava.jar File");

    _mainFrame = mf;
    _mainPanel= new JPanel();
    
    Action generateAction = new AbstractAction("Generate") {
      public void actionPerformed(ActionEvent e) { generate(); } 
    };
    _generateButton = new JButton(generateAction);

    Action checkAction = new AbstractAction("Check Conflicts") {
      public void actionPerformed(ActionEvent e) { checkConflicts(); } 
    };
    _checkButton = new JButton(checkAction);

    Action closeAction = new AbstractAction("Close") {
      public void actionPerformed(ActionEvent e) { close(); }
    };
    _closeButton = new JButton(closeAction);
    
    init();
    initDone(); 
  }

  
  private void init() {
    _setupPanel(_mainPanel);
    JScrollPane scrollPane = new JScrollPane(_mainPanel);
    Container cp = getContentPane();
    
    GridBagLayout cpLayout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    cp.setLayout(cpLayout);
    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.NORTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridheight = GridBagConstraints.RELATIVE;
    c.weightx = 1.0;
    c.weighty = 1.0;
    cpLayout.setConstraints(scrollPane, c);
    cp.add(scrollPane);
    
    
    JPanel bottom = new JPanel();
    bottom.setBorder(new EmptyBorder(5,5,5,5));
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
    bottom.add(Box.createHorizontalGlue());
    bottom.add(_checkButton);
    bottom.add(_generateButton);
    bottom.add(_closeButton);
    bottom.add(Box.createHorizontalGlue());

    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTH;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.weighty = 0.0;
    cpLayout.setConstraints(bottom, c);
    cp.add(bottom);

    
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    if (dim.width>FRAME_WIDTH) { dim.width = FRAME_WIDTH; }
    else { dim.width -= 80; }
    if (dim.height>FRAME_HEIGHT) { dim.height = FRAME_HEIGHT; }
    else { dim.height -= 80; }
    setSize(dim);
    Utilities.setPopupLoc(this, _mainFrame);

    reset();
  }
  
  
  private JPanel _makeJarFileSelector() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Jar Output File");
    fileChooser.setApproveButtonText("Select");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    
    _jarFileSelector = new FileSelectorComponent(this, fileChooser, 20, 12f, false) {
      
      protected void _chooseFile() {
        _mainFrame.removeModalWindowAdapter(GenerateCustomDrJavaJarFrame.this);
        super._chooseFile();
        _mainFrame.installModalWindowAdapter(GenerateCustomDrJavaJarFrame.this, LambdaUtil.NO_OP, CLOSE);
      }
    };
    _jarFileSelector.setFileFilter(new FileFilter() {
      public boolean accept(File f) { return f.getName().endsWith(".jar") || f.isDirectory(); }
      public String getDescription() { return "Java Archive Files (*.jar)"; }
    });
    
    return _jarFileSelector;
  }

  
  public void close() {
    setVisible(false);
    reset();
  }

  
  public void reset() {
    ArrayList<File> jars = new ArrayList<File>();
    _sourcesList.setValue(jars);
  }
  
  
  public void generate() {
    final File jarOut = _jarFileSelector.getFileFromField();
    if ((jarOut == null) || (jarOut.equals(FileOps.NULL_FILE))) {
      JOptionPane.showMessageDialog(GenerateCustomDrJavaJarFrame.this,
                                    "You must specify an output file",
                                    "Error: No File Specified",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }
    else if (jarOut.exists()) {
      if (jarOut.equals(_drjavaFile)) {
        JOptionPane.showMessageDialog(GenerateCustomDrJavaJarFrame.this,
                                      "You cannot specify this DrJava executable as output file.\n"+
                                      "Please choose a different file.",
                                      "Error: Cannot Overwrite",
                                      JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (JOptionPane.showConfirmDialog(GenerateCustomDrJavaJarFrame.this,
                                        "Are you sure you want to overwrite the file '" + jarOut.getPath() + "'?",
                                        "Overwrite file?",
                                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        
        return;
      }
    }

    final Container prevContentPane = getContentPane();
    
    JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    validate();
    
    new Thread() {
      public void run() {
        final StringBuilder sb = new StringBuilder();
        final Runnable yesRunnable = new Runnable() {
          public void run() {
            
            sb.setLength(0);
            new Thread() {
              public void run() {
                boolean result = true;
                try {
                  final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jarOut));
                  final Runnable noRunnable = new Runnable() {
                    public void run() {
                      try { zos.close(); }
                      catch(IOException ioe) {  }
                      setContentPane(prevContentPane);
                      validate();
                      new DrJavaScrollableDialog(GenerateCustomDrJavaJarFrame.this,
                                                 "Generation Failed",
                                                 "Custom drjava.jar file generation failed.",
                                                 sb.toString(),
                                                 INFO_DIALOG_WIDTH,
                                                 INFO_DIALOG_HEIGHT,
                                                 true).show();
                    }
                  };
                  
                  checkConflictFree(sb, zos, "Writing file ...", new Runnable() {
                    public void run() {
                      try {
                        
                        addOptionsPropertiesFile(zos);
                        
                        zos.close();
                        setContentPane(prevContentPane);
                        validate();
                        JOptionPane.showMessageDialog(GenerateCustomDrJavaJarFrame.this,
                                                      "Custom drjava.jar file generated successfully.",
                                                      "Generation Successful",
                                                      JOptionPane.INFORMATION_MESSAGE);
                      }
                      catch(IOException ioe) { noRunnable.run(); }
                    }
                  }, noRunnable);
                }
                catch(IOException ioe) {
                  setContentPane(prevContentPane);
                  validate();
                  new DrJavaScrollableDialog(GenerateCustomDrJavaJarFrame.this,
                                             "Generation Failed",
                                             "Custom drjava.jar file generation failed.",
                                             sb.toString(),
                                             INFO_DIALOG_WIDTH,
                                             INFO_DIALOG_HEIGHT,
                                             true).show();
                }
              }
            }.start();
          }
        };
        Runnable noRunnable = new Runnable() {
          public void run() {
            if (askGenerateAnyway(sb.toString())) {
              yesRunnable.run();
            }
            else {
              setContentPane(prevContentPane);
              validate();
            }
          }
        };
        checkConflictFree(sb, null, "Checking for conflicts ...", yesRunnable, noRunnable);
      }
    }.start();
  }

  
  public boolean askGenerateAnyway(String text) {
    final boolean[] result = new boolean[] { false };
    new DrJavaScrollableDialog(this,
                               "Additional Files Conflict",
                               "The files you want to add create conflicts. As a result,\n"+
                               "the generated file may not work.",
                               text,
                               INFO_DIALOG_WIDTH,
                               INFO_DIALOG_HEIGHT,
                               true) {
      protected void _addButtons() {
        _buttonPanel.add(new JButton(new AbstractAction("Generate anyway") {
          public void actionPerformed(ActionEvent e) {
            result[0] = true;
            _dialog.dispose();
          }
        }));
        _buttonPanel.add(new JButton(new AbstractAction("Go back") {
          public void actionPerformed(ActionEvent e) {
            result[0] = false;
            _dialog.dispose();
          }
        }));
      }
    }.show();
    return result[0];
  }
  
  
  public void checkConflictFree(StringBuilder sb, ZipOutputStream zos,
                                String message,
                                Runnable yesRunnable, Runnable noRunnable) {
    final Container prevContentPane = getContentPane();
    
    JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    cp.add(new JOptionPane(message,JOptionPane.INFORMATION_MESSAGE,
                           JOptionPane.DEFAULT_OPTION,null,
                           new Object[0]), BorderLayout.CENTER);
    JProgressBar pb = new JProgressBar(0,100);
    pb.setIndeterminate(true);
    cp.add(pb, BorderLayout.SOUTH);
    validate();
    
    MD5ChecksumProperties p = new MD5ChecksumProperties();
    
    sb.append("Conflict summary:\n");
    boolean result = true;
    
    try {
      if (!addZipFile(_drjavaFile, p, sb, zos, NOT_OPTIONS_PROPERTIES)) {
        result = false;
      }
    }
    catch(IOException ioe) {
      sb.append("Error: "+_drjavaFile.getPath()+" could not be processed.");
      result = false;
    }
    
    for(File f: _sourcesList.getValue()) {
      try {
        if (!f.exists()) {
          sb.append("Error: "+f.getPath()+" not found.");
          result = false;
          continue;
        }
        if (f.isDirectory()) {
          if (!addDirectory(f, p, sb, zos, NOT_MANIFEST)) {
            result = false;
          }
        }
        else {
          if (!addZipFile(f, p, sb, zos, NOT_MANIFEST)) {
            result = false;
          }
        }
      }
      catch(IOException ioe) {
        sb.append("Error: "+f.getPath()+" could not be processed.");
        result = false;
      }
    }
    
    setContentPane(prevContentPane);
    validate();

    if (result) {
      Utilities.invokeLater(yesRunnable);
    }
    else {
      Utilities.invokeLater(noRunnable);
    }
  }
  
  
  public boolean addDirectory(File f,
                              MD5ChecksumProperties p,
                              StringBuilder sb,
                              ZipOutputStream zos,
                              Predicate<String> processFile) throws IOException {
    sb.append("Adding "+f+":\n");
    boolean result = true;
    for(File de: IOUtil.listFilesRecursively(f)) {
      if (!de.isDirectory()) {
        String key = FileOps.stringMakeRelativeTo(de, f).replace('\\','/');
        if (processFile.contains(key)) {
          if (zos!=null) {
            
            if (p.containsKey(key)) {
              
              sb.append("Warning: skipped "+key+", already exists\n");
            }
            else {
              
              zos.putNextEntry(new ZipEntry(key));
              if (!p.addMD5(key, de, zos)) {
                
                result = false;
                sb.append("Warning: a different "+key+" already exists\n");
              }
            }
          }
          else {
            
            if (!p.addMD5(key, de, zos)) {
              
              result = false;
              sb.append("Warning: a different "+key+" already exists\n");
            }
          }
        }
      }
    }
    return result;
  }
  
  
  public boolean addZipFile(File f,
                            MD5ChecksumProperties p,
                            StringBuilder sb,
                            ZipOutputStream zos,
                            Predicate<String> processFile) throws IOException {
    sb.append("Adding "+f+":\n");
    ZipFile zf = new ZipFile(f);
    Enumeration<? extends ZipEntry> entries = zf.entries();
    boolean result = true;
    while(entries.hasMoreElements()) {
      ZipEntry ze = entries.nextElement();
      if (!ze.isDirectory()) {
        String key = ze.getName().replace('\\','/');
        if (processFile.contains(key)) {
          if (zos!=null) {
            
            if (p.containsKey(key)) {
              
              sb.append("Warning: skipped "+key+", already exists\n");
            }
            else {
              
              zos.putNextEntry(new ZipEntry(key));
              if (!p.addMD5(key, zf.getInputStream(ze), zos)) {
                
                result = false;
                sb.append("Warning: a different "+key+" already exists\n");
              }
            }
          }
          else {
            
            if (!p.addMD5(key, zf.getInputStream(ze), zos)) {
              
              result = false;
              sb.append("Warning: a different "+key+" already exists\n");
            }
          }
        }
      }
    }
    return result;
  }
  
  
  public void checkConflicts() {
    new Thread() {
      public void run() {
        final StringBuilder sb = new StringBuilder();
        checkConflictFree(sb, null, "Checking for conflicts ...", new Runnable() {
          public void run() {
            JOptionPane.showMessageDialog(GenerateCustomDrJavaJarFrame.this,
                                          "There were no conflicts.",
                                          "Additional Files",
                                          JOptionPane.INFORMATION_MESSAGE);
          }
        }, new Runnable() {
          public void run() {
            new DrJavaScrollableDialog(GenerateCustomDrJavaJarFrame.this,
                                       "Additional Files Conflict",
                                       "The files you want to add create conflicts. As a result,\n"+
                                       "the generated file may not work.",
                                       sb.toString(),
                                       INFO_DIALOG_WIDTH,
                                       INFO_DIALOG_HEIGHT,
                                       true).show();
          }
        });
      }
    }.start();
  }
  
  
  private void _setupPanel(JPanel panel) {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    panel.setLayout(gridbag);
    c.fill = GridBagConstraints.HORIZONTAL;
    Insets labelInsets = new Insets(5, 10, 0, 0);
    Insets compInsets  = new Insets(5, 5, 0, 10);

    c.weightx = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = labelInsets;
    
    JTextArea helpText = new JTextArea();
    helpText.setEditable(false);
    helpText.setText("This dialog lets you generate a custom drjava.jar file based on "+
                     "the currently running version of DrJava, and you can include "+
                     "additional jar files, zip files or directories. These additional "+
                     "files are added to the drjava.jar and are immediately available "+
                     "in the generated DrJava application without having to set up "+
                     "extra classpaths.\n"+
                     "\n"+
                     "If a file is contained in more than one source, the file " +
                     "contained in the first source will be included; conflicting " +
                     "files from sources further down the list will be skipped. " +
                     "Files belonging to DrJava always take precedence.\n" +
                     "Note: This implies that DrJava's manifest file will be used.\n" +
                     "\n"+
                     "Please note that the added files may produce a copy of DrJava "+
                     "does not work as intended, and that it will be more difficult "+
                     "for us to help you with these problems. YOU ARE USING THE "+
                     "CUSTOM DRJAVA.JAR FILE AT YOUR OWN RISK.");
    helpText.setLineWrap(true);
    helpText.setWrapStyleWord(true);
    
    gridbag.setConstraints(helpText, c);
    panel.add(helpText);

    
    c.weightx = 0.0;
    c.gridwidth = 1;
    c.weighty = 0.0;
    c.gridheight = 1;
    c.insets = labelInsets;
    JLabel label = new JLabel("Output Jar File");
    label.setToolTipText("The file that the custom drjava.jar should be written to.");
    gridbag.setConstraints(label, c);
    panel.add(label);
    
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    JPanel jarFilePanel = _makeJarFileSelector();
    gridbag.setConstraints(jarFilePanel, c);
    panel.add(jarFilePanel);
    
    
    c.weightx = 0.0;
    c.gridwidth = 1;
    c.weighty = 1.0;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.fill = GridBagConstraints.BOTH;
    c.insets = labelInsets;
    
    JLabel jarLabel = new JLabel("<html>Additional Sources</html>");
    jarLabel.setToolTipText("<html>The list of additional jar or zip files or<br>" +   
                            "directories that should be added to the custom drjava.jar<br>" + 
                            "file. If a file is contained in more than one source,<br>" +
                            "the file contained in the first source will be included;<br>" +
                            "conflicting files from sources further down the list<br>" +
                            "will be skipped. Files belonging to DrJava always<br>" +
                            "take precedence.</html>");
    gridbag.setConstraints(jarLabel, c);
    panel.add(jarLabel);

    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;

    Component sourcesComponent = _sourcesComponent();
    gridbag.setConstraints(sourcesComponent, c);
    panel.add(sourcesComponent);    
  }

  
  public Component _sourcesComponent() {
    _sourcesList = new VectorFileOptionComponent(null, "Additional Sources", this, null, true) {
      protected Action _getAddAction() {
        final Action a = super._getAddAction();
        return new AbstractAction("Add") {
          public void actionPerformed(ActionEvent ae) {
            _mainFrame.removeModalWindowAdapter(GenerateCustomDrJavaJarFrame.this);
            a.actionPerformed(ae);
            _mainFrame.installModalWindowAdapter(GenerateCustomDrJavaJarFrame.this, LambdaUtil.NO_OP, CLOSE);
          }
        };
      }
    };
    _sourcesList.setRows(5,5);
    return _sourcesList.getComponent();
  }

  
  protected final Runnable1<WindowEvent> CLOSE = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { close(); }
  };
  
  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CLOSE);
      toFront();
    }
    else {
      _mainFrame.removeModalWindowAdapter(this);
      _mainFrame.hourglassOff();
      _mainFrame.toFront();
    }
    super.setVisible(vis);
  }
  
  
  public void addOptionsPropertiesFile(ZipOutputStream zos) throws IOException {
    Properties optionsProperties = new Properties();
    ResourceBundle bundle = ResourceBundle .getBundle(edu.rice.cs.drjava.DrJava.RESOURCE_BUNDLE_NAME);
    String customDrJavaJarVersionSuffix = "";

    Enumeration<String> keyEn = bundle.getKeys();    
    while(keyEn.hasMoreElements()) {
      String key = keyEn.nextElement();
      String value = bundle.getString(key);
      if (key.equals(OptionConstants.CUSTOM_DRJAVA_JAR_VERSION_SUFFIX.getName())) {
        
        customDrJavaJarVersionSuffix = value;
      }
      else if (key.equals(OptionConstants.NEW_VERSION_NOTIFICATION.getName())) {
        
      }
      else if (key.equals(OptionConstants.NEW_VERSION_ALLOWED.getName())) {
        
      }
      else {
        optionsProperties.setProperty(key, value);
      }
    }
    
    
    StringBuilder sb = new StringBuilder(customDrJavaJarVersionSuffix);
    for(File f: _sourcesList.getValue()) {
      if (sb.length()>0) { sb.append(", "); }
      sb.append(f.getName());
    }
    optionsProperties.setProperty(OptionConstants.CUSTOM_DRJAVA_JAR_VERSION_SUFFIX.getName(), sb.toString());
    
    
    optionsProperties.setProperty(OptionConstants.NEW_VERSION_ALLOWED.getName(), "false");
    optionsProperties.setProperty(OptionConstants.NEW_VERSION_NOTIFICATION.getName(),
                                  OptionConstants.VersionNotificationChoices.DISABLED);
    
    
    zos.putNextEntry(new ZipEntry(OPTIONS_PROPERTIES_FILENAME));
    optionsProperties.store(zos, "Custom drjava.jar file generated "+new Date());
  }
  
  
  public static final String OPTIONS_PROPERTIES_FILENAME = 
    edu.rice.cs.drjava.DrJava.RESOURCE_BUNDLE_NAME.replace('.','/')+".properties";
  
  
  public static final Predicate<String> NOT_OPTIONS_PROPERTIES = new Predicate<String>() {
    public boolean contains(String key) {
      return !key.equals(OPTIONS_PROPERTIES_FILENAME);
    }
  };
  
  
  public static final Predicate<String> NOT_MANIFEST = new Predicate<String>() {
    public boolean contains(String key) {
      return !key.equals("META-INF/MANIFEST.MF");
    }
  };
}
