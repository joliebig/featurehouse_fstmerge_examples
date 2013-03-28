

package edu.rice.cs.drjava.ui;

import java.awt.event.*;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.ui.config.*;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.AbsRelFile;
import edu.rice.cs.util.swing.FileSelectorComponent;
import edu.rice.cs.util.swing.DirectorySelectorComponent;
import edu.rice.cs.util.swing.DirectoryChooser;
import edu.rice.cs.util.swing.FileChooser;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.filechooser.FileFilter;


public class ProjectPropertiesFrame extends SwingFrame {

  private static final int FRAME_WIDTH = 503;
  private static final int FRAME_HEIGHT = 500;

  private MainFrame _mainFrame;      
  private SingleDisplayModel _model; 

  private final JButton _okButton;
  private final JButton _applyButton;
  private final JButton _cancelButton;
  
  private JPanel _mainPanel;

  private DirectorySelectorComponent _projRootSelector;
  private DirectorySelectorComponent _buildDirSelector;
  private DirectorySelectorComponent _workDirSelector;
  private JTextField                 _mainDocumentSelector;
  
  private JCheckBox _autoRefreshComponent;

  private VectorAbsRelFileOptionComponent _extraClassPathList;
  private VectorFileOptionComponent _excludedFilesList;
  
  
  public ProjectPropertiesFrame(MainFrame mf) {
    super("Project Properties");

    

    _mainFrame = mf;
    _model = _mainFrame.getModel();
    _mainPanel= new JPanel();
    
    Action okAction = new AbstractAction("OK") {
      public void actionPerformed(ActionEvent e) {
        
        boolean successful = true;
        successful = saveSettings();
        if (successful) ProjectPropertiesFrame.this.setVisible(false);
        reset();
      }
    };
    _okButton = new JButton(okAction);

    Action applyAction = new AbstractAction("Apply") {
      public void actionPerformed(ActionEvent e) {
        
        saveSettings();
        reset();
      }
    };
    _applyButton = new JButton(applyAction);

    Action cancelAction = new AbstractAction("Cancel") {
      public void actionPerformed(ActionEvent e) { cancel(); }
    };
    _cancelButton = new JButton(cancelAction);
    
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
    bottom.add(_applyButton);
    bottom.add(_okButton);
    bottom.add(_cancelButton);
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

  
  public void cancel() {
    reset();
    _applyButton.setEnabled(false);
    ProjectPropertiesFrame.this.setVisible(false);
  }

  public void reset() { reset(_model.getProjectRoot()); }

  private void reset(File projRoot) {

    _projRootSelector.setFileField(projRoot);

    final File bd = _model.getBuildDirectory();
    final JTextField bdTextField = _buildDirSelector.getFileField();
    if (bd == FileOps.NULL_FILE) bdTextField.setText("");
    else _buildDirSelector.setFileField(bd);

    final File wd = _model.getWorkingDirectory();
    final JTextField wdTextField = _workDirSelector.getFileField();
    if (wd == FileOps.NULL_FILE) wdTextField.setText("");
    else _workDirSelector.setFileField(wd);

    final String mc = _model.getMainClass();
    final JTextField mcTextField = _mainDocumentSelector;
    if (mc == null) mcTextField.setText("");
    else mcTextField.setText(mc);
    
    _autoRefreshComponent.setSelected(_getAutoRefreshStatus());

    ArrayList<AbsRelFile> cp = new ArrayList<AbsRelFile>(CollectUtil.makeList(_model.getExtraClassPath()));
    _extraClassPathList.setValue(cp);

    ArrayList<File> ef = new ArrayList<File>();
    for(File f: _model.getExclFiles()) { ef.add(f); }
    _excludedFilesList.setValue(ef);
    _applyButton.setEnabled(false);
  }

  
  public boolean saveSettings() {
    boolean projRootChanged = false;

    File pr = _projRootSelector.getFileFromField();

    if (!pr.equals(_model.getProjectRoot())) {
      _model.setProjectRoot(pr);
      projRootChanged = true;
    }


    File bd = _buildDirSelector.getFileFromField();
    if (_buildDirSelector.getFileField().getText().equals("")) bd = FileOps.NULL_FILE;
    _model.setBuildDirectory(bd);

    File wd = _workDirSelector.getFileFromField();
    if (_workDirSelector.getFileField().getText().equals("")) wd = FileOps.NULL_FILE;
    _model.setWorkingDirectory(wd);

    String mc = _mainDocumentSelector.getText();
    if(mc == null) mc = "";
    _model.setMainClass(mc);

    Vector<AbsRelFile> extras = _extraClassPathList.getValue();  
    _model.setExtraClassPath(IterUtil.snapshot(extras));

    _model.setAutoRefreshStatus(_autoRefreshComponent.isSelected());

    _model.setExcludedFiles(_excludedFilesList.getValue().toArray(new File[0]));
    
    
    if (projRootChanged) {
      try {
        _model.reloadProject(_mainFrame.getCurrentProject(), _mainFrame.gatherProjectDocInfo());
      } catch(IOException e) { throw new edu.rice.cs.util.UnexpectedException(e, "I/O error while reloading project"); }
    }
    
    return true;
  }

  
  private File _getProjRoot() {
    File projRoot = _model.getProjectRoot();
    if (projRoot != null) return projRoot;
    return FileOps.NULL_FILE;
  }

  
  private File _getBuildDir() {
    File buildDir = _model.getBuildDirectory();
    if (buildDir != null) return buildDir;
    return FileOps.NULL_FILE;
  }

  
  private File _getWorkDir() {
    File workDir = _model.getWorkingDirectory();
    if (workDir != null) return workDir;
    return FileOps.NULL_FILE;
  }

  
  private File _getMainFile() {
    File mainFile = _model.getMainClassContainingFile();
    if (mainFile != null) return mainFile;
    return FileOps.NULL_FILE;
  }
  
  
  private String _getMainClass(){
    String mainClass = _model.getMainClass();
    if(mainClass == null) return "";
    
    return mainClass;
  }
  
  
  private boolean _getAutoRefreshStatus() {
    return _model.getAutoRefreshStatus();
  }

  private void _setupPanel(JPanel panel) {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    panel.setLayout(gridbag);
    c.fill = GridBagConstraints.HORIZONTAL;
    Insets labelInsets = new Insets(5, 10, 0, 0);
    Insets compInsets  = new Insets(5, 5, 0, 10);

    

    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    JLabel prLabel = new JLabel("Project Root");
    prLabel.setToolTipText("<html>The root directory for the project source files .<br>" + 
    "If not specified, the parent directory of the project file.</html>");
    gridbag.setConstraints(prLabel, c);

    panel.add(prLabel);
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;

    JPanel prPanel = _projRootPanel();
    gridbag.setConstraints(prPanel, c);
    panel.add(prPanel);

    

    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    JLabel bdLabel = new JLabel("Build Directory");
    bdLabel.setToolTipText("<html>The directory the class files will be compiled into.<br>" + 
        "If not specified, the class files will be compiled into<br>" + 
    "the same directory as their corresponding source files</html>");
    gridbag.setConstraints(bdLabel, c);

    panel.add(bdLabel);
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;

    JPanel bdPanel = _buildDirectoryPanel();
    gridbag.setConstraints(bdPanel, c);
    panel.add(bdPanel);

    

    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    JLabel wdLabel = new JLabel("Working Directory");
    wdLabel.setToolTipText("<html>The root directory for relative path names.</html>");
    gridbag.setConstraints(wdLabel, c);

    panel.add(wdLabel);
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;

    JPanel wdPanel = _workDirectoryPanel();
    gridbag.setConstraints(wdPanel, c);
    panel.add(wdPanel);

    

    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    JLabel classLabel = new JLabel("Main Class");
    classLabel.setToolTipText("<html>The class containing the <code>main</code><br>" + 
                              "method for the entire project</html>");
    gridbag.setConstraints(classLabel, c);
    panel.add(classLabel);

    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;

    JPanel mainClassPanel = _mainDocumentSelector();
    gridbag.setConstraints(mainClassPanel, c);
    panel.add(mainClassPanel);

    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    
    JLabel extrasLabel = new JLabel("Extra Classpath");
    extrasLabel.setToolTipText("<html>The list of extra classpaths to load with the project.<br>" +   
                               "This may include either JAR files or directories. Any<br>" + 
                               "classes defined in these classpath locations will be <br>" + 
                               "visible in the interactions pane and also accessible <br>" + 
                               "by the compiler when compiling the project.<br>" + 
                               "The entries are relative to the project file unless<br>" + 
                               "the 'Absolute' checkbox is marked.</html>");
    gridbag.setConstraints(extrasLabel, c);
    panel.add(extrasLabel);

    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;

    Component extrasComponent = _extraClassPathComponent();
    gridbag.setConstraints(extrasComponent, c);
    panel.add(extrasComponent);
    
    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;

    JLabel refreshLabel = new JLabel("Auto Refresh");
    refreshLabel.setToolTipText("<html>Whether the project will automatically open new files found within the source tree</html>");
    gridbag.setConstraints(refreshLabel, c);
    panel.add(refreshLabel);

    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;

    _autoRefreshComponent = new JCheckBox();
    gridbag.setConstraints(_autoRefreshComponent, c);
    panel.add(_autoRefreshComponent);    

    c.weightx = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    
    
    JLabel excludedLabel = new JLabel("<html>Files Excluded from<br>Auto-Refresh</html>");
    excludedLabel.setToolTipText("<html>The list of source files excluded from project auto-refresh.<br>" + 
                                 "These files will not be added to the project.</html>");
    gridbag.setConstraints(excludedLabel, c);
    panel.add(excludedLabel);
    
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    Component excludedComponent = _excludedFilesComponent();
    gridbag.setConstraints(excludedComponent, c);
    panel.add(excludedComponent);
  }
  
   private DocumentListener _applyListener = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) { setEnabled(); }
      public void removeUpdate(DocumentEvent e) { setEnabled(); }
      public void changedUpdate(DocumentEvent e) { setEnabled(); }
      private void setEnabled() { 
        assert EventQueue.isDispatchThread();


            _applyButton.setEnabled(true); 


      }
   };

  public JPanel _projRootPanel() {
    DirectoryChooser dirChooser = new DirectoryChooser(this);
    dirChooser.setSelectedFile(_getProjRoot());
    dirChooser.setDialogTitle("Select Project Root Folder");
    dirChooser.setApproveButtonText("Select");

    _projRootSelector = new DirectorySelectorComponent(this, dirChooser, 20, 12f) {
      protected void _chooseFile() {
        _mainFrame.removeModalWindowAdapter(ProjectPropertiesFrame.this);
        super._chooseFile();
        _mainFrame.installModalWindowAdapter(ProjectPropertiesFrame.this, LambdaUtil.NO_OP, CANCEL);
      }
    };
    
    
    _projRootSelector.getFileField().getDocument().addDocumentListener(_applyListener);

    return _projRootSelector;
  }

  public JPanel _buildDirectoryPanel() {
    DirectoryChooser dirChooser = new DirectoryChooser(this);
    File bd = _getBuildDir();
    if (bd == null || bd == FileOps.NULL_FILE) bd = _getProjRoot();
    dirChooser.setSelectedFile(bd);
    dirChooser.setDialogTitle("Select Build Directory");
    dirChooser.setApproveButtonText("Select");

    
    _buildDirSelector = new DirectorySelectorComponent(this, dirChooser, 20, 12f, false) {
      protected void _chooseFile() {
        _mainFrame.removeModalWindowAdapter(ProjectPropertiesFrame.this);
        super._chooseFile();
        _mainFrame.installModalWindowAdapter(ProjectPropertiesFrame.this, LambdaUtil.NO_OP, CANCEL);
      }
    };
    _buildDirSelector.setFileField(bd);  
    

    _buildDirSelector.getFileField().getDocument().addDocumentListener(_applyListener);

    return _buildDirSelector;
  }

  public JPanel _workDirectoryPanel() {
    DirectoryChooser dirChooser = new DirectoryChooser(this);
    dirChooser.setSelectedFile(_getWorkDir());
    dirChooser.setDialogTitle("Select Working Directory");
    dirChooser.setApproveButtonText("Select");

    _workDirSelector = new DirectorySelectorComponent(this, dirChooser, 20, 12f) {
      protected void _chooseFile() {
        _mainFrame.removeModalWindowAdapter(ProjectPropertiesFrame.this);
        super._chooseFile();
        _mainFrame.installModalWindowAdapter(ProjectPropertiesFrame.this, LambdaUtil.NO_OP, CANCEL);
      }
    };
    

    _workDirSelector.getFileField().getDocument().addDocumentListener(_applyListener);
    return _workDirSelector;
  }

  public Component _extraClassPathComponent() {
    _extraClassPathList = new VectorAbsRelFileOptionComponent(null, "Extra Project Classpaths", this, null, true) {
      protected Action _getAddAction() {
        final Action a = super._getAddAction();
        return new AbstractAction("Add") {
          public void actionPerformed(ActionEvent ae) {
            _mainFrame.removeModalWindowAdapter(ProjectPropertiesFrame.this);
            a.actionPerformed(ae);
            _mainFrame.installModalWindowAdapter(ProjectPropertiesFrame.this, LambdaUtil.NO_OP, CANCEL);
          }
        };
      }
    };
    _extraClassPathList.setRows(5,5);
    _extraClassPathList.addChangeListener(new OptionComponent.ChangeListener() {
      public Object value(Object oc) {
        _applyButton.setEnabled(true);
        return null;
      }
    });
    return _extraClassPathList.getComponent();
  }

  public Component _excludedFilesComponent() {
    _excludedFilesList = new VectorFileOptionComponent(null, "Files Excluded from Auto-Refresh", this, null, false) {
      protected Action _getAddAction() {
        final Action a = super._getAddAction();
        return new AbstractAction("Add") {
          public void actionPerformed(ActionEvent ae) {
            _mainFrame.removeModalWindowAdapter(ProjectPropertiesFrame.this);
            a.actionPerformed(ae);
            _mainFrame.installModalWindowAdapter(ProjectPropertiesFrame.this, LambdaUtil.NO_OP, CANCEL);
          }
        };
      }
    };
    _excludedFilesList.setRows(5,5);
    _excludedFilesList.getFileChooser().resetChoosableFileFilters();
    _excludedFilesList.getFileChooser().addChoosableFileFilter(new JavaSourceFilter());
    _excludedFilesList.getFileChooser().setFileFilter(new SmartSourceFilter());
    _excludedFilesList.addChangeListener(new OptionComponent.ChangeListener() {
      public Object value(Object oc) {
        _applyButton.setEnabled(true);
        return null;
      }
    });
    if (_model.getProjectRoot() != null) {
      _excludedFilesList.setBaseDir(_model.getProjectRoot());
    }
    return _excludedFilesList.getComponent();
  }

  public JPanel _mainDocumentSelector() {
    final File projRoot = _getProjRoot();

    final FileChooser chooser = new FileChooser(projRoot);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);

    chooser.setDialogTitle("Select Main Class");
    chooser.setCurrentDirectory(projRoot);
    File   mainFile  = _getMainFile();
    if (mainFile != FileOps.NULL_FILE){
      chooser.setSelectedFile(mainFile);
    }

    chooser.setApproveButtonText("Select");

    chooser.resetChoosableFileFilters();
    chooser.addChoosableFileFilter(new SmartSourceFilter());
    chooser.addChoosableFileFilter(new JavaSourceFilter());
    _mainDocumentSelector = new JTextField(20){
      public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
      }
    };

    _mainDocumentSelector.setFont(_mainDocumentSelector.getFont().deriveFont(12f));
    _mainDocumentSelector.setPreferredSize(new Dimension(22, 22));
    
    _mainDocumentSelector.getDocument().addDocumentListener(_applyListener);
    
    JButton selectFile = new JButton("...");
    selectFile.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        int ret = chooser.showOpenDialog(ProjectPropertiesFrame.this);
        
        if(ret != JFileChooser.APPROVE_OPTION)
          return;
        
        File mainClass = chooser.getSelectedFile();
        
        File sourceRoot = new File(_projRootSelector.getFileField().getText());
        
        if(sourceRoot == null || mainClass == null)
          return;
        
        if(!mainClass.getAbsolutePath().startsWith(sourceRoot.getAbsolutePath())){
          JOptionPane.showMessageDialog(ProjectPropertiesFrame.this,
                                        "Main Class must be in either Project Root or one of its sub-directories.", 
                                        "Unable to set Main Class", JOptionPane.ERROR_MESSAGE);
          
          _mainDocumentSelector.setText("");
          return;
        }
        
        
        String qualifiedName = mainClass.getAbsolutePath().substring(sourceRoot.getAbsolutePath().length());
        
        
        if(qualifiedName.startsWith("" + File.separatorChar))
          qualifiedName = qualifiedName.substring(1);
        
        
        if(qualifiedName.toLowerCase().endsWith(OptionConstants.JAVA_FILE_EXTENSION))
          qualifiedName = qualifiedName.substring(0, qualifiedName.length() - 5);
          
        
        _mainDocumentSelector.setText(qualifiedName.replace(File.separatorChar, '.'));
      }
    });
    
    
    selectFile.setMaximumSize(new Dimension(22, 22));
    selectFile.setMargin(new Insets(0, 5 ,0, 5));
    
    JPanel toRet = new JPanel();
    javax.swing.BoxLayout layout = new javax.swing.BoxLayout(toRet, javax.swing.BoxLayout.X_AXIS);
    toRet.setLayout(layout);
    toRet.add(_mainDocumentSelector);
    toRet.add(selectFile);
    
    return toRet;
  }

  
  protected final Runnable1<WindowEvent> CANCEL = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { cancel(); }
  };
  
  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
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
