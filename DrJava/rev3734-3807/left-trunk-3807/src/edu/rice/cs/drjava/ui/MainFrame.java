

package edu.rice.cs.drjava.ui;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.print.*;
import java.beans.*;

import java.io.*;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaRoot;
import edu.rice.cs.drjava.platform.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.definitions.NoSuchDocumentException;
import edu.rice.cs.drjava.model.definitions.DocumentUIListener;
import edu.rice.cs.drjava.model.definitions.CompoundUndoManager;
import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.drjava.model.debug.*;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.ui.config.ConfigFrame;
import edu.rice.cs.drjava.ui.predictive.PredictiveInputFrame;
import edu.rice.cs.drjava.ui.predictive.PredictiveInputModel;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.ExitingNotAllowedException;
import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.swing.DelegatingAction;
import edu.rice.cs.util.swing.DirectoryChooser;
import edu.rice.cs.util.swing.HighlightManager;
import edu.rice.cs.util.swing.SwingWorker;
import edu.rice.cs.util.swing.ConfirmCheckBoxDialog;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.util.swing.BorderlessSplitPane;
import edu.rice.cs.util.swing.FileDisplayManager;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.docnavigation.*;
import edu.rice.cs.drjava.project.*;
import edu.rice.cs.util.swing.*;
import edu.rice.cs.util.*;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;

import static edu.rice.cs.drjava.config.OptionConstants.*;


public class MainFrame extends JFrame {
  
  private static final int INTERACTIONS_TAB = 0;
  private static final String ICON_PATH = "/edu/rice/cs/drjava/ui/icons/";
  private static final String DEBUGGER_OUT_OF_SYNC =
    " Current document is out of sync with the debugger and should be recompiled!";
  
  
  private static final int DEBUG_STEP_TIMER_VALUE = 2000;
  
  
  private final SingleDisplayModel _model;
  
  
  private final ModelListener _mainListener; 
  
  
  private Hashtable<OpenDefinitionsDocument, JScrollPane> _defScrollPanes;
  
  
  private DefinitionsPane _currentDefPane;
  
  
  private String _fileTitle = "";
  
  
  
  
  
  private JTabbedPane _tabbedPane;
  private CompilerErrorPanel _compilerErrorPanel;
  private InteractionsPane _consolePane;
  private JScrollPane _consoleScroll;
  private ConsoleController _consoleController;  
  private InteractionsPane _interactionsPane;
  private JPanel _interactionsContainer;
  private InteractionsController _interactionsController;  
  private InteractionsScriptController _interactionsScriptController;
  private InteractionsScriptPane _interactionsScriptPane;
  
  private DebugPanel _debugPanel;
  private JUnitPanel _junitErrorPanel;
  private JavadocErrorPanel _javadocErrorPanel;
  private FindReplaceDialog _findReplace;
  private BreakpointsPanel _breakpointsPanel;
  private LinkedList<TabbedPanel> _tabs;
  
  private Component _lastFocusOwner;
  

  
  

  
  
  private JPanel _statusBar;
  private JLabel _fileNameField;
  private JLabel _sbMessage;
  private JLabel _currLocationField;
  private PositionListener _posListener;
  
  
  private JSplitPane _docSplitPane;
  private JSplitPane _debugSplitPane;
  private JSplitPane _mainSplit;
  
  
  private JButton _compileButton;
  private JButton _closeButton;
  private JButton _undoButton;
  private JButton _redoButton;
  private JToolBar _toolBar;
  private JFileChooser _interactionsHistoryChooser;
  
  
  private JMenuBar _menuBar;
  private JMenu _fileMenu;
  private JMenu _editMenu;
  private JMenu _toolsMenu;
  private JMenu _projectMenu;
  private JMenu _debugMenu;
  private JMenu _languageLevelMenu;
  private JMenu _helpMenu;
  private JMenuItem _debuggerEnabledMenuItem;









  
  
  private JPopupMenu _navPanePopupMenu;
  private JPopupMenu _navPanePopupMenuForExternal;
  private JPopupMenu _navPanePopupMenuForAuxiliary;
  private JPopupMenu _navPanePopupMenuForRoot;
  private JPopupMenu _navPaneFolderPopupMenu;
  private JPopupMenu _interactionsPanePopupMenu;
  private JPopupMenu _consolePanePopupMenu;
  
  
  private ConfigFrame _configFrame;
  private HelpFrame _helpFrame;
  private QuickStartFrame _quickStartFrame;
  private AboutDialog _aboutDialog;

  
  
  private RecentFileManager _recentFileManager;
  
  
  private RecentFileManager _recentProjectManager;
  
  private File _currentProjFile;
  
  
  private final Timer _debugStepTimer;
  
  
  private HighlightManager.HighlightInfo _currentThreadLocationHighlight = null;
  
  
  private java.util.Hashtable<Breakpoint, HighlightManager.HighlightInfo> _breakpointHighlights;
  
  
  private boolean _promptBeforeQuit;
  
  
  private JFileChooser _openChooser;
  
  
  private JFileChooser _openProjectChooser;
  
  
  private JFileChooser _saveChooser;
  
  
  private javax.swing.filechooser.FileFilter _javaSourceFilter = new JavaSourceFilter();
  
  
  private javax.swing.filechooser.FileFilter _projectFilter = new javax.swing.filechooser.FileFilter() {
    public boolean accept(File f) {
      return f.isDirectory() || f.getPath().endsWith(PROJECT_FILE_EXTENSION);
    }
    public String getDescription() { return "DrJava Project Files (*.pjt)"; }
  };
  
  
  
  private FileOpenSelector _openSelector = new FileOpenSelector() {
    public File[] getFiles() throws OperationCanceledException {
      
      _openChooser.resetChoosableFileFilters();
      
      _openChooser.setFileFilter(_javaSourceFilter);
      return getOpenFiles(_openChooser);
    }
  };
  
  
  private FileOpenSelector _openFileOrProjectSelector = new FileOpenSelector() {
    public File[] getFiles() throws OperationCanceledException {
      
      _openChooser.resetChoosableFileFilters();
      
      _openChooser.addChoosableFileFilter(_projectFilter);
      _openChooser.setFileFilter(_javaSourceFilter);
      return getOpenFiles(_openChooser);
    }
  };
 
  
  private FileOpenSelector _openProjectSelector = new FileOpenSelector() {
    public File[] getFiles() throws OperationCanceledException {
      File[] retFiles = getOpenFiles(_openProjectChooser);
      return retFiles;
    }
  };
  
  
  private FileSaveSelector _saveSelector = new FileSaveSelector() {
    public File getFile() throws OperationCanceledException { return getSaveFile(_saveChooser); }
    public boolean warnFileOpen(File f) { return _warnFileOpen(f); }
    public boolean verifyOverwrite() { return _verifyOverwrite(); }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) {
      _model.setActiveDocument(doc);
      String text = "File " + oldFile.getAbsolutePath() +
        "\ncould not be found on disk!  It was probably moved\n" +
        "or deleted.  Would you like to save it in a new file?";
      int rc = JOptionPane.showConfirmDialog(MainFrame.this,
                                             text,
                                             "File Moved or Deleted",
                                             JOptionPane.YES_NO_OPTION);
      return (rc == JOptionPane.YES_OPTION);
    }
  };
  
  
  private FileSaveSelector _saveAsSelector = new FileSaveSelector() {
    public File getFile() throws OperationCanceledException { return getSaveFile(_saveChooser); }
    public boolean warnFileOpen(File f) { return _warnFileOpen(f); }
    public boolean verifyOverwrite() { return _verifyOverwrite(); }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return true; }
  };
  
  
  private JavadocDialog _javadocSelector = new JavadocDialog(this);
  
    

  DirectoryChooser _folderChooser;
  private JCheckBox _openRecursiveCheckBox;
  
  private Action _moveToAuxiliaryAction = new AbstractAction("Include With Project") {
    {
      String msg = 
      "<html>Open this document each time this project is opened.<br>"+
      "This file would then be compiled and tested with the<br>"+
      "rest of the project.</html>";
      putValue(Action.SHORT_DESCRIPTION, msg);
    }
    public void actionPerformed(ActionEvent ae) { _moveToAuxiliary(); }
  };
  private Action _removeAuxiliaryAction = new AbstractAction("Do Not Include With Project") {
    {
      putValue(Action.SHORT_DESCRIPTION, "Do not open this document next time this project is opened.");
    }
    public void actionPerformed(ActionEvent ae) { _removeAuxiliary(); }
  };
  
  private Action _newAction = new AbstractAction("New") {
    public void actionPerformed(ActionEvent ae) {

      _new();
    }
  };
  
  private Action _newProjectAction = new AbstractAction("New") {
    public void actionPerformed(ActionEvent ae) { _newProject(); }
  };
  
  private AbstractAction _runProjectAction = new AbstractAction("Run Main Document") {
    public void actionPerformed(ActionEvent ae) { _runProject(); }
  };
  
  
  private JarOptionsDialog _jarOptionsDialog;
  
  
  private void initJarOptionsDialog() {
    if (_jarOptionsDialog==null) {
      _jarOptionsDialog = new JarOptionsDialog(MainFrame.this);
      if (DrJava.getConfig().getSetting(DIALOG_JAROPTIONS_STORE_POSITION).booleanValue()) {
        _jarOptionsDialog.setFrameState(DrJava.getConfig().getSetting(DIALOG_JAROPTIONS_STATE));
      }      
    }
  }
  
  
  public void resetJarOptionsDialogPosition() {
    initJarOptionsDialog();
    _jarOptionsDialog.setFrameState("default");
    if (DrJava.getConfig().getSetting(DIALOG_JAROPTIONS_STORE_POSITION).booleanValue()) {
      DrJava.getConfig().setSetting(DIALOG_JAROPTIONS_STATE, "default");
    }
  }
  
  private Action _jarProjectAction = new AbstractAction("Create Jar File from Project...") {
    public void actionPerformed(ActionEvent ae) {
      new SwingWorker() {
        public Object construct() {
          initJarOptionsDialog();
          _jarOptionsDialog.setVisible(true);
          return null;
        }
      }.start();
    }
  };
  
  
  
  private Action _newJUnitTestAction = new AbstractAction("New JUnit Test Case...") {
    public void actionPerformed(ActionEvent ae) {
      String testName = JOptionPane.showInputDialog(MainFrame.this,
                                                    "Please enter a name for the test class:",
                                                    "New JUnit Test Case",
                                                    JOptionPane.QUESTION_MESSAGE);
      if (testName != null) {
        String ext;
        for(int i=0; i < DrJavaRoot.LANGUAGE_LEVEL_EXTENSIONS.length; i++) {
          ext = DrJavaRoot.LANGUAGE_LEVEL_EXTENSIONS[i];
          if (testName.endsWith(ext)) testName = testName.substring(0, testName.length() - ext.length());
        }
        
        _model.newTestCase(testName, false, false);
      }
    }
  };
  
  
  private Action _openAction = new AbstractAction("Open...") {
    public void actionPerformed(ActionEvent ae) {
      _open();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private Action _openFolderAction  = new AbstractAction("Open Folder...") {
    public void actionPerformed(ActionEvent ae) { 
      _openFolder();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private Action _openFileOrProjectAction = new AbstractAction("Open...") {
    public void actionPerformed(ActionEvent ae) { 
      _openFileOrProject(); 
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private Action _openProjectAction = new AbstractAction("Open...") {
    public void actionPerformed(ActionEvent ae) { _openProject(); }
  };
  
  private Action _closeProjectAction = new AbstractAction("Close") {
    public void actionPerformed(ActionEvent ae) { 
      _closeProject();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  
  private Action _closeAction = new AbstractAction("Close") {
    public void actionPerformed(ActionEvent ae) { 
      _close();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private Action _closeAllAction = new AbstractAction("Close All") {
    public void actionPerformed(ActionEvent ae) { 
      _closeAll();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private Action _closeFolderAction = new AbstractAction("Close Folder") {
    public void actionPerformed(ActionEvent ae) { 
      _closeFolder();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  
  
  private Action _openAllFolderAction = new AbstractAction("Open All Files") {
    public void actionPerformed(ActionEvent ae) {
      
      
      
      
      File dir = _openChooser.getCurrentDirectory();
      _openFolder(dir, false);  
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private Action _openOneFolderAction = new AbstractAction("Open File in Folder") {
    public void actionPerformed(ActionEvent ae)  { 
      _open();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  public Action _newFileFolderAction = new AbstractAction("Create New File in Folder") {
    public void actionPerformed(ActionEvent ae)  {
      
      _new();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private AbstractAction _junitFolderAction = new AbstractAction("Test Folder") {
    public void actionPerformed(ActionEvent ae) { _junitFolder(); }
  };
  
  
  private Action _saveAction = new AbstractAction("Save") {
    public void actionPerformed(ActionEvent ae) { _save(); }
  };
  
  
  public void pack() {
    Utilities.invokeAndWait(new Runnable() { public void run() { packHelp(); } });
  }
  
  
  private void packHelp() { super.pack(); }
  
  
  public boolean saveEnabledHuh() { return _saveAction.isEnabled(); }
  
  
  private Action _saveAsAction = new AbstractAction("Save As...") {
    public void actionPerformed(ActionEvent ae) { _saveAs(); }
  };
  
  private Action _saveProjectAction = new AbstractAction("Save") {
    public void actionPerformed(ActionEvent ae) {
      _saveAll();  
    }
  };
  
  private Action _saveProjectAsAction = new AbstractAction("Save As...") {
    public void actionPerformed(ActionEvent ae) {
      if (_saveProjectAs()) {  
        _saveAll();  
      }
    }
  };
  
  
  private Action _revertAction = new AbstractAction("Revert to Saved") {
    public void actionPerformed(ActionEvent ae) {
      String title = "Revert to Saved?";
      
      String message = "Are you sure you want to revert the current " +
        "file to the version on disk?";
      
      int rc = JOptionPane.showConfirmDialog(MainFrame.this,
                                             message,
                                             title,
                                             JOptionPane.YES_NO_OPTION);
      if (rc == JOptionPane.YES_OPTION) {
        _revert();
      }
    }
  };
  
  
  
  
  private Action _saveAllAction = new AbstractAction("Save All") {
    public void actionPerformed(ActionEvent ae) { _saveAll(); }
  };
  
  
  private Action _printDefDocAction = new AbstractAction("Print...") {
    public void actionPerformed(ActionEvent ae) { _printDefDoc(); }
  };
  
  
  private Action _printConsoleAction = new AbstractAction("Print Console...") {
    public void actionPerformed(ActionEvent ae) { _printConsole(); }
  };
  
  
  private Action _printInteractionsAction = new AbstractAction("Print Interactions...") {
    public void actionPerformed(ActionEvent ae) { _printInteractions(); }
  };
  
  
  private Action _printDefDocPreviewAction = new AbstractAction("Print Preview...") {
    public void actionPerformed(ActionEvent ae) { _printDefDocPreview(); }
  };
  
  
  private Action _printConsolePreviewAction = new AbstractAction("Print Preview...") {
    public void actionPerformed(ActionEvent ae) { _printConsolePreview(); }
  };
  
  
  private Action _printInteractionsPreviewAction = new AbstractAction("Print Preview...") {
    public void actionPerformed(ActionEvent ae) { _printInteractionsPreview(); }
  };
  
  
  private Action _pageSetupAction = new AbstractAction("Page Setup...") {
    public void actionPerformed(ActionEvent ae) { _pageSetup(); }
  };
  




  
 
  private Action _compileAction = new AbstractAction("Compile Current Document") {
    public void actionPerformed(ActionEvent ae) { 
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes(); 
      _compile(); 
    }
  };
  
  
  private AbstractAction _compileProjectAction = new AbstractAction("Compile Project") {
    public void actionPerformed(ActionEvent ae) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes();
      _compileProject(); 
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private AbstractAction _compileFolderAction = new AbstractAction("Compile Folder") {
    public void actionPerformed(ActionEvent ae) { 
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes();
      _compileFolder();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private AbstractAction _compileAllAction = new AbstractAction("Compile All Documents") {
    public void actionPerformed(ActionEvent ae) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes();
      _compileAll();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  
  private AbstractAction _cleanAction = new AbstractAction("Clean Build Directory") {
    public void actionPerformed(ActionEvent ae) { _clean(); }
  };
  
  
  private AbstractAction _runAction = new AbstractAction("Run Document's Main Method") {
    public void actionPerformed(ActionEvent ae) { _runMain(); }
  };
  
  
  private AbstractAction _junitAction = new AbstractAction("Test Current Document") {
    public void actionPerformed(ActionEvent ae) { 
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) _mainSplit.resetToPreferredSizes();
      _junit(); 
    }
  };
  
  
  private AbstractAction _junitAllAction = new AbstractAction("Test All Documents") {
    public void actionPerformed(ActionEvent e) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) _mainSplit.resetToPreferredSizes();
      _junitAll();
      _findReplace.updateFirstDocInSearch();
    }
    
  };
  
  
  private AbstractAction _junitOpenProjectFilesAction = new AbstractAction("Test Project") {
    public void actionPerformed(ActionEvent e) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) _mainSplit.resetToPreferredSizes();
      _junitProject();
      _findReplace.updateFirstDocInSearch();
    }
  };
  
  


















  
  
  private Action _javadocAllAction = new AbstractAction("Javadoc All Documents") {
    public void actionPerformed(ActionEvent ae) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes();
      try {
        
        JavadocModel jm = _model.getJavadocModel();
        File suggestedDir = jm.suggestJavadocDestination(_model.getActiveDocument());
        _javadocSelector.setSuggestedDir(suggestedDir);
        String cps = _model.getClassPath().toString();
        jm.javadocAll(_javadocSelector, _saveSelector, cps);
      }
      catch (IOException ioe) { _showIOError(ioe); }
      finally {
        
      }
    }
  };
  
  
  private Action _javadocCurrentAction = new AbstractAction("Preview Javadoc for Current Document") {
    public void actionPerformed(ActionEvent ae) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes();
      try { _model.getActiveDocument().generateJavadoc(_saveSelector); }
      catch (IOException ioe) { _showIOError(ioe); }
    }
  };
  
  
  Action cutAction = new DefaultEditorKit.CutAction() {
    public void actionPerformed(ActionEvent e) {
      Component c = MainFrame.this.getFocusOwner();
      super.actionPerformed(e);
      if (c != null) c.requestFocusInWindow();
    }
  };
  
  
  Action copyAction = new DefaultEditorKit.CopyAction() {
    public void actionPerformed(ActionEvent e) {
      Component c = MainFrame.this.getFocusOwner();
      super.actionPerformed(e);
      if (c != null) c.requestFocusInWindow();
    }
  };
  
  
  Action pasteAction = new DefaultEditorKit.PasteAction() {
    public void actionPerformed(ActionEvent e) {
      Component c = MainFrame.this.getFocusOwner();
      if (_currentDefPane.hasFocus()) {
        _currentDefPane.endCompoundEdit();


        super.actionPerformed(e);
        _currentDefPane.endCompoundEdit(); 

      }
      else super.actionPerformed(e);
      
      if (c != null) c.requestFocusInWindow();      
    }
  };
  
  
  private Action _copyInteractionToDefinitionsAction =
    new AbstractAction("Lift Current Interaction to Definitions") {
    public void actionPerformed(ActionEvent a) {
      String text = _interactionsController.getDocument().getCurrentInput();
      if (! text.equals("")) {
        _putTextIntoDefinitions(text + "\n");
        return;
      }
      try { text = _interactionsController.getDocument().lastEntry(); }
      catch(Exception e) { return; } 
      
      
      _putTextIntoDefinitions(text + "\n");
      return;
    }
  };
  
  
  
  
  private DelegatingAction _undoAction = new DelegatingAction() {
    public void actionPerformed(ActionEvent e) {
      _currentDefPane.endCompoundEdit();
      super.actionPerformed(e);
      _currentDefPane.requestFocusInWindow();
      OpenDefinitionsDocument doc = _model.getActiveDocument();

      _saveAction.setEnabled(doc.isModifiedSinceSave() || doc.isUntitled());

    }
  };
  
  
  private DelegatingAction _redoAction = new DelegatingAction() {
    public void actionPerformed(ActionEvent e) {
      super.actionPerformed(e);
      _currentDefPane.requestFocusInWindow();
      OpenDefinitionsDocument doc = _model.getActiveDocument();
      _saveAction.setEnabled(doc.isModifiedSinceSave() || doc.isUntitled());
    }
  };
  
  
  private Action _quitAction = new AbstractAction("Quit") {
    public void actionPerformed(ActionEvent ae) { _quit(); }
  };
  
  
  private Action _selectAllAction = new AbstractAction("Select All") {
    public void actionPerformed(ActionEvent ae) { _selectAll(); }
  };
  
  private void _showFindReplaceTab() {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes(); 
      if (! _findReplace.isDisplayed()) {
        showTab(_findReplace);
        _findReplace.beginListeningTo(_currentDefPane);
      }
      _findReplace.setVisible(true);
      _tabbedPane.setSelectedComponent(_findReplace);
  }
  
  
  private Action _findReplaceAction = new AbstractAction("Find/Replace") {
    public void actionPerformed(ActionEvent ae) {
      _showFindReplaceTab();
      
      SwingUtilities.invokeLater(new Runnable() { public void run() { _findReplace.requestFocusInWindow(); } });
    }
  };
  
  
  private Action _findNextAction = new AbstractAction("Find Next") {
    public void actionPerformed(ActionEvent ae) {
      _showFindReplaceTab();
      if (!DrJava.getConfig().getSetting(FIND_REPLACE_FOCUS_IN_DEFPANE).booleanValue()) {
        
        SwingUtilities.invokeLater(new Runnable() { public void run() { _findReplace.requestFocusInWindow(); } });
      }
      _findReplace.findNext();
      _currentDefPane.requestFocusInWindow();  
      
    }
  };
  
  
  private Action _findPrevAction = new AbstractAction("Find Previous") {
    public void actionPerformed(ActionEvent ae) {
      _showFindReplaceTab();
      if (!DrJava.getConfig().getSetting(FIND_REPLACE_FOCUS_IN_DEFPANE).booleanValue()) {
        
        SwingUtilities.invokeLater(new Runnable() { public void run() { _findReplace.requestFocusInWindow(); } });
      }
      _findReplace.findPrevious();
      _currentDefPane.requestFocusInWindow();
    }
  };
  
  
  private Action _gotoLineAction = new AbstractAction("Go to Line...") {
    public void actionPerformed(ActionEvent ae) {
      int pos = _gotoLine();
      _currentDefPane.requestFocusInWindow();
      if (pos != -1) _currentDefPane.setCaretPosition(pos);  
      
    }
  };

  
  private static class GoToFileListEntry implements Comparable<GoToFileListEntry> {
    public final OpenDefinitionsDocument doc;
    private final String str;
    public GoToFileListEntry(OpenDefinitionsDocument d, String s) {
      doc = d;
      str = s;
    }
    public String toString() {
      return str;
    }
    public int compareTo(GoToFileListEntry other) {
      return str.toLowerCase().compareTo(other.str.toLowerCase());
    }
    public boolean equals(Object other) {
      if (!(other instanceof GoToFileListEntry)) return false;
      return str.equals(((GoToFileListEntry)other).str);
    }
    public int hashCode() {
      return str.hashCode();
    }
  }

  
  public void resetGotoFileDialogPosition() {
    initGotoFileDialog();
    _gotoFileDialog.setFrameState("default");
    if (DrJava.getConfig().getSetting(DIALOG_GOTOFILE_STORE_POSITION).booleanValue()) {
      DrJava.getConfig().setSetting(DIALOG_GOTOFILE_STATE, "default");
    }
  }
  
  
  void initGotoFileDialog() {
    if (_gotoFileDialog==null) {
      PredictiveInputFrame.InfoSupplier<GoToFileListEntry> info = 
        new PredictiveInputFrame.InfoSupplier<GoToFileListEntry>() {
        public String apply(GoToFileListEntry entry) {
          StringBuilder sb = new StringBuilder();
          
          if (entry.doc != null) {
            try {
              try {
                sb.append(FileOps.makeRelativeTo(entry.doc.getRawFile(), entry.doc.getSourceRoot()));
              }
              catch(IOException e) {
                sb.append(entry.doc.getFile());
              }
            }
            catch(edu.rice.cs.drjava.model.FileMovedException e) {
              sb.append(entry + " was moved");
            }
            catch(java.lang.IllegalStateException e) {
              sb.append(entry);
            }
            catch(InvalidPackageException e) { 
              sb.append(entry);
            }
          } else {
            sb.append(entry);
          }
          return sb.toString();
        }
      };
      PredictiveInputFrame.CloseAction<GoToFileListEntry> okAction = 
        new PredictiveInputFrame.CloseAction<GoToFileListEntry>() {
        public Object apply(PredictiveInputFrame<GoToFileListEntry> p) {
          if (p.getItem()!=null) {
            _model.setActiveDocument(p.getItem().doc);
          }
          hourglassOff();
          return null;
        }
      };
      PredictiveInputFrame.CloseAction<GoToFileListEntry> cancelAction = 
        new PredictiveInputFrame.CloseAction<GoToFileListEntry>() {
        public Object apply(PredictiveInputFrame<GoToFileListEntry> p) {
          hourglassOff();
          return null;
        }
      };
      java.util.ArrayList<PredictiveInputModel.MatchingStrategy<GoToFileListEntry>> strategies =
        new java.util.ArrayList<PredictiveInputModel.MatchingStrategy<GoToFileListEntry>>();
      strategies.add(new PredictiveInputModel.FragmentStrategy<GoToFileListEntry>());
      strategies.add(new PredictiveInputModel.PrefixStrategy<GoToFileListEntry>());
      strategies.add(new PredictiveInputModel.RegExStrategy<GoToFileListEntry>());
      _gotoFileDialog = 
        new PredictiveInputFrame<GoToFileListEntry>(MainFrame.this,
                                                    "Go to File",
                                                    true, 
                                                    true, 
                                                    info,
                                                    strategies,
                                                    okAction,
                                                    cancelAction,
                                                    new GoToFileListEntry(null, "dummy")); 
      
      
      if (DrJava.getConfig().getSetting(DIALOG_GOTOFILE_STORE_POSITION).booleanValue()) {
        _gotoFileDialog.setFrameState(DrJava.getConfig().getSetting(DIALOG_GOTOFILE_STATE));
      }      
    }
  }

  
  PredictiveInputFrame<GoToFileListEntry> _gotoFileDialog = null;
 
  
  private Action _gotoFileAction = new AbstractAction("Go to File...") {
    public void actionPerformed(ActionEvent ae) {
      initGotoFileDialog();
      List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
      if ((docs==null) || (docs.size() == 0)) {
        return; 
      }
      GoToFileListEntry currentEntry = null;
      ArrayList<GoToFileListEntry> list;
      if (DrJava.getConfig().getSetting(DIALOG_GOTOFILE_FULLY_QUALIFIED).booleanValue()) {
        list = new ArrayList<GoToFileListEntry>(2*docs.size());
      }
      else {
        list = new ArrayList<GoToFileListEntry>(docs.size());
      }
      for(OpenDefinitionsDocument d: docs) {
        GoToFileListEntry entry = new GoToFileListEntry(d, d.toString());
        if (d.equals(_model.getActiveDocument())) {
          currentEntry = entry;
        }
        list.add(entry);
        if (DrJava.getConfig().getSetting(DIALOG_GOTOFILE_FULLY_QUALIFIED).booleanValue()) {
          try {
            try {
              File relative = FileOps.makeRelativeTo(d.getFile(), d.getSourceRoot());
              if (!relative.toString().equals(d.toString())) {
                list.add(new GoToFileListEntry(d, d.getPackageName() + "." + d.toString()));
              }
            }
            catch(IOException e) {
              
            }
            catch(edu.rice.cs.drjava.model.definitions.InvalidPackageException e) { 
              
            }
          }
          catch(IllegalStateException e) {
            
          }
        }
      }
      _gotoFileDialog.setItems(true, list); 
      if (currentEntry!=null) {
        _gotoFileDialog.setCurrentItem(currentEntry);
      }
      hourglassOn();
      _gotoFileDialog.setVisible(true);
    }
  };
   
  
  void _gotoFileUnderCursor() {

    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    if ((docs==null) || (docs.size() == 0)) return; 
    
    GoToFileListEntry currentEntry = null;
    ArrayList<GoToFileListEntry> list;
    list = new ArrayList<GoToFileListEntry>(docs.size());
    for(OpenDefinitionsDocument d: docs) {
      GoToFileListEntry entry = new GoToFileListEntry(d, d.toString());
      if (d.equals(_model.getActiveDocument())) currentEntry = entry;
      list.add(entry);
    }
    
    PredictiveInputModel<GoToFileListEntry> pim =
      new PredictiveInputModel<GoToFileListEntry>(true,
                                                  new PredictiveInputModel.PrefixStrategy<GoToFileListEntry>(),
                                                  list);
    OpenDefinitionsDocument odd = getCurrentDefPane().getOpenDefDocument();
    odd.readLock();
    String mask = "";
    try {
      int loc = getCurrentDefPane().getCaretPosition();
      String s = odd.getText();
      
      int start = loc;
      while(start>0) {
        if (!Character.isJavaIdentifierPart(s.charAt(start-1))) { break; }
        --start;
      }
      while((start<s.length()) && (!Character.isJavaIdentifierStart(s.charAt(start))) && (start<loc)) {
        ++start;
      }
      
      int end = loc-1;
      while(end<s.length()-1) {
        if (!Character.isJavaIdentifierPart(s.charAt(end+1))) { break; }
        ++end;
      }
      if ((start>=0) && (end<s.length())) {
        mask = s.substring(start, end+1);
        pim.setMask(mask);
      }
    }
    finally { odd.readUnlock(); }
    

    
    if (pim.getMatchingItems().size() == 1) {
      
      if (pim.getCurrentItem() != null) _model.setActiveDocument(pim.getCurrentItem().doc);
    }
    else {
      
      pim.extendMask(".java");
      if (pim.getMatchingItems().size() == 1) {
        
        if (pim.getCurrentItem() != null) _model.setActiveDocument(pim.getCurrentItem().doc);
      }
      else {
        
        pim.setMask(mask);
        if (pim.getMatchingItems().size() == 0) {
          
          mask = pim.getMask();
          while(mask.length()>0) {
            mask = mask.substring(0, mask.length()-1);
            pim.setMask(mask);
            if (pim.getMatchingItems().size()>0) { break; }
          }
        }       
        initGotoFileDialog();
        _gotoFileDialog.setModel(true, pim); 
        if (currentEntry != null) _gotoFileDialog.setCurrentItem(currentEntry);
        hourglassOn();
        _gotoFileDialog.setVisible(true);
      }
    }
  }
  
  
  final Action gotoFileUnderCursorAction = new AbstractAction("Go to File Under Cursor") {
    public void actionPerformed(ActionEvent ae) {
      _gotoFileUnderCursor();
    }
  };

  
  public void resetCompleteFileDialogPosition() {
    initCompleteFileDialog();
    _completeFileDialog.setFrameState("default");
    if (DrJava.getConfig().getSetting(DIALOG_COMPLETE_FILE_STORE_POSITION).booleanValue()) {
      DrJava.getConfig().setSetting(DIALOG_COMPLETE_FILE_STATE, "default");
    }
  }
  
  
  void initCompleteFileDialog() {
    if (_completeFileDialog==null) {
      PredictiveInputFrame.CloseAction<GoToFileListEntry> okAction = new PredictiveInputFrame.CloseAction<GoToFileListEntry>() {
        public Object apply(PredictiveInputFrame<GoToFileListEntry> p) {
          if (p.getItem()!=null) {
            OpenDefinitionsDocument odd = getCurrentDefPane().getOpenDefDocument();
            try {
              String mask = "";
              int loc = getCurrentDefPane().getCaretPosition();
              String s = odd.getText(AbstractDJDocument.DOCSTART, loc);
              
              
              if ((loc<s.length()) && (!Character.isWhitespace(s.charAt(loc))) &&
                  ("()[]{}<>.,:;/*+-!~&|%".indexOf(s.charAt(loc))==-1)) return null;
              
              
              int start = loc;
              while(start>0) {
                if (!Character.isJavaIdentifierPart(s.charAt(start-1))) { break; }
                --start;
              }
              while((start<s.length()) && (!Character.isJavaIdentifierStart(s.charAt(start))) && (start<loc)) {
                ++start;
              }
              
              if (!s.substring(start, loc).equals(p.getItem().toString())) {
                odd.remove(start, loc-start);
                odd.insertString(start, p.getItem().toString(), null);
              }
            }
            catch(BadLocationException ble) {  }
            finally { odd.modifyUnlock(); }
          }
          hourglassOff();
          return null;
        }
      };
      PredictiveInputFrame.CloseAction<GoToFileListEntry> cancelAction = 
        new PredictiveInputFrame.CloseAction<GoToFileListEntry>() {
        public Object apply(PredictiveInputFrame<GoToFileListEntry> p) {
          hourglassOff();
          return null;
        }
      };
      java.util.ArrayList<PredictiveInputModel.MatchingStrategy<GoToFileListEntry>> strategies =
        new java.util.ArrayList<PredictiveInputModel.MatchingStrategy<GoToFileListEntry>>();
      strategies.add(new PredictiveInputModel.FragmentStrategy<GoToFileListEntry>());
      strategies.add(new PredictiveInputModel.PrefixStrategy<GoToFileListEntry>());
      strategies.add(new PredictiveInputModel.RegExStrategy<GoToFileListEntry>());
      _completeFileDialog = 
        new PredictiveInputFrame<GoToFileListEntry>(MainFrame.this,
                                                    "Auto-Complete File",
                                                    true, 
                                                    true, 
                                                    null,
                                                    strategies,
                                                    okAction,
                                                    cancelAction,
                                                    new GoToFileListEntry(null, "dummy")); 
      
      
      if (DrJava.getConfig().getSetting(DIALOG_COMPLETE_FILE_STORE_POSITION).booleanValue()) {
        _completeFileDialog.setFrameState(DrJava.getConfig().getSetting(DIALOG_COMPLETE_FILE_STATE));
      }      
    }
  }

  
  PredictiveInputFrame<GoToFileListEntry> _completeFileDialog = null;
   
  
  void _completeFileUnderCursor() {
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    if ((docs==null) || (docs.size() == 0)) return; 
    
    GoToFileListEntry currentEntry = null;
    ArrayList<GoToFileListEntry> list;
    if ((DrJava.getConfig().getSetting(DIALOG_COMPLETE_SCAN_CLASS_FILES).booleanValue()) &&
        (_completeClassList.size()>0)) {
      list = _completeClassList;
    }
    else {
      list = new ArrayList<GoToFileListEntry>(docs.size());
      for(OpenDefinitionsDocument d: docs) {
        if (d.isUntitled()) continue;
        String str = d.toString();
        if (str.lastIndexOf('.')>=0) {
          str = str.substring(0, str.lastIndexOf('.'));
        }
        GoToFileListEntry entry = new GoToFileListEntry(d, str);
        if (d.equals(_model.getActiveDocument())) currentEntry = entry;
        list.add(entry);
      }
    }
    
    PredictiveInputModel<GoToFileListEntry> pim =
      new PredictiveInputModel<GoToFileListEntry>(true,
                                                  new PredictiveInputModel.PrefixStrategy<GoToFileListEntry>(),
                                                  list);
    OpenDefinitionsDocument odd = getCurrentDefPane().getOpenDefDocument();
    odd.modifyLock();
    boolean uniqueMatch = true;
    try {
      String mask = "";
      int loc = getCurrentDefPane().getCaretPosition();
      String s = odd.getText(AbstractDJDocument.DOCSTART, loc);
      
      
      if ((loc<s.length()) && (!Character.isWhitespace(s.charAt(loc))) &&
          ("()[]{}<>.,:;/*+-!~&|%".indexOf(s.charAt(loc))==-1)) return;
      
      
      int start = loc;
      while(start>0) {
        if (!Character.isJavaIdentifierPart(s.charAt(start-1))) { break; }
        --start;
      }
      while((start<s.length()) && (!Character.isJavaIdentifierStart(s.charAt(start))) && (start<loc)) {
        ++start;
      }
      
      int end = loc-1;
      
      if ((start>=0) && (end<s.length())) {
        mask = s.substring(start, end+1);
        pim.setMask(mask);
      }
      
      if (pim.getMatchingItems().size() == 1) {
        if (pim.getCurrentItem() != null) {
          
          if (!s.substring(start, loc).equals(pim.getCurrentItem().toString())) {
            odd.remove(start, loc-start);
            odd.insertString(start, pim.getCurrentItem().toString(), null);
          }
          return;
        }
      }
      else {
        
        uniqueMatch = false;
        pim.setMask(mask);
        if (pim.getMatchingItems().size() == 0) {
          
          mask = pim.getMask();
          while(mask.length()>0) {
            mask = mask.substring(0, mask.length()-1);
            pim.setMask(mask);
            if (pim.getMatchingItems().size()>0) { break; }
          }
        }       
        initCompleteFileDialog();
        _completeFileDialog.setModel(true, pim); 
        if (currentEntry != null) _completeFileDialog.setCurrentItem(currentEntry);
        hourglassOn();
        _completeFileDialog.setVisible(true);
      }
    }
    catch(BadLocationException ble) {  }
    finally { 
      if (uniqueMatch) { odd.modifyUnlock(); }
    }
  }
  
  
  final Action completeFileUnderCursorAction = new AbstractAction("Auto-Complete File Under Cursor...") {
    public void actionPerformed(ActionEvent ae) {
      _completeFileUnderCursor();
    }
  };

  
  private Action _indentLinesAction = new AbstractAction("Indent Line(s)") {
    public void actionPerformed(ActionEvent ae) {
      _currentDefPane.endCompoundEdit();
      _currentDefPane.indent();
    }
  };
  
  
  private Action _commentLinesAction = new AbstractAction("Comment Line(s)") {
    public void actionPerformed(ActionEvent ae) {
      hourglassOn();
      try{ commentLines(); }
      finally{ hourglassOff(); }
    }
  };
  
  
  private Action _uncommentLinesAction = new AbstractAction("Uncomment Line(s)") {
    public void actionPerformed(ActionEvent ae){
      hourglassOn();
      try{ uncommentLines(); }
      finally{ hourglassOff(); }
    }
  };
  
  
  private Action _clearConsoleAction = new AbstractAction("Clear Console") {
    public void actionPerformed(ActionEvent ae) { _model.resetConsole(); }
  };
  
  
  private Action _showDebugConsoleAction = new AbstractAction("Show DrJava Debug Console") {
    public void actionPerformed(ActionEvent e) {
      DrJavaRoot.showDrJavaDebugConsole(MainFrame.this);
    }
  };
  
  
  public void enableResetInteractions() { _resetInteractionsAction.setEnabled(true); }
  
  
  private Action _resetInteractionsAction = new AbstractAction("Reset Interactions") {
    public void actionPerformed(ActionEvent ae) {
      if (! DrJava.getConfig().getSetting(INTERACTIONS_RESET_PROMPT).booleanValue()) {
        _doResetInteractions();
        return;
      }
      
      String title = "Confirm Reset Interactions";
      String message = "Are you sure you want to reset the Interactions Pane?";
      ConfirmCheckBoxDialog dialog =
        new ConfirmCheckBoxDialog(MainFrame.this, title, message);
      int rc = dialog.show();
      if (rc == JOptionPane.YES_OPTION) {
        _doResetInteractions();
        
        if (dialog.getCheckBoxValue()) {
          DrJava.getConfig().setSetting(INTERACTIONS_RESET_PROMPT, Boolean.FALSE);
        }
      }
    }
  };
  
  private void _doResetInteractions() {
    _tabbedPane.setSelectedIndex(INTERACTIONS_TAB);
    
    
    new Thread(new Runnable() { 
      public void run() {_model.resetInteractions(_model.getWorkingDirectory(), true);
      }
    }).start();
  }
  
  
  private Action _viewInteractionsClassPathAction = new AbstractAction("View Interactions Classpath...") {
    public void actionPerformed(ActionEvent e) { viewInteractionsClassPath(); }
  };
  
    
  public void viewInteractionsClassPath() {
    StringBuffer cpBuf = new StringBuffer();
    Vector<URL> classPathElements = _model.getClassPath();
    for(int i = 0; i < classPathElements.size(); i++) {
      cpBuf.append(classPathElements.get(i).getPath());
      if (i + 1 < classPathElements.size()) cpBuf.append("\n");
    }
    String classPath = cpBuf.toString();
    
    new DrJavaScrollableDialog(MainFrame.this, "Interactions Classpath", "Current Interpreter Classpath", classPath).
      show();
  }
  
  
  private Action _helpAction = new AbstractAction("Help") {
    public void actionPerformed(ActionEvent ae) {
      
      if (_helpFrame == null) {
        _helpFrame = new HelpFrame();
      }
      _helpFrame.setVisible(true);
    }
  };
  
  
  private Action _quickStartAction = new AbstractAction("QuickStart") {
    public void actionPerformed(ActionEvent ae) {
      
      if (_quickStartFrame == null) {
        _quickStartFrame = new QuickStartFrame();
      }
      _quickStartFrame.setVisible(true);
    }
  };
  
  
  private Action _aboutAction = new AbstractAction("About") {
    public void actionPerformed(ActionEvent ae) {
      
      if (_aboutDialog == null) _aboutDialog = new AboutDialog(MainFrame.this);
      _aboutDialog.setVisible(true);
    }
  };
  
  
  private Action _errorsAction = new AbstractAction("DrJava Errors") {
    public void actionPerformed(ActionEvent ae) {
      DrJavaErrorWindow.singleton().setVisible(true);
    }
  };
  
  
  private Action _switchToNextAction = new AbstractAction("Next Document") {
    public void actionPerformed(ActionEvent ae) {
      this.setEnabled(false);
      if (_docSplitPane.getDividerLocation() < _docSplitPane.getMinimumDividerLocation())
        _docSplitPane.setDividerLocation(DrJava.getConfig().getSetting(DOC_LIST_WIDTH).intValue());
      
      _model.setActiveNextDocument();
      _findReplace.updateFirstDocInSearch();
      this.setEnabled(true);
    }
  };
  
  
  private Action _switchToPrevAction = new AbstractAction("Previous Document") {
    public void actionPerformed(ActionEvent ae) {
      this.setEnabled(false);
      if (_docSplitPane.getDividerLocation() < _docSplitPane.getMinimumDividerLocation())
        _docSplitPane.setDividerLocation(DrJava.getConfig().getSetting(DOC_LIST_WIDTH).intValue());
      _model.setActivePreviousDocument();
      _findReplace.updateFirstDocInSearch();
      this.setEnabled(true);
    }
  };
  
  
  private Action _switchToNextPaneAction =  new AbstractAction("Next Pane") {
    public void actionPerformed(ActionEvent ae) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes(); 
      this.setEnabled(false);
      _switchPaneFocus(true);
      this.setEnabled(true);
    }
  };
  
  
  private Action _switchToPreviousPaneAction =  new AbstractAction("Previous Pane") {
    public void actionPerformed(ActionEvent ae) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes(); 
      this.setEnabled(false);
      _switchPaneFocus(false);
      this.setEnabled(true);
    }
  };
  
  
  private Action _gotoClosingBraceAction =  new AbstractAction("Go to Closing Brace") {
    public void actionPerformed(ActionEvent ae) {
        OpenDefinitionsDocument odd = getCurrentDefPane().getOpenDefDocument();
        odd.readLock();
        try {
          int pos = odd.findNextEnclosingBrace(getCurrentDefPane().getCaretPosition(), '{', '}');
          if (pos!=AbstractDJDocument.ERROR_INDEX) { getCurrentDefPane().setCaretPosition(pos); }
        }
        catch(BadLocationException ble) {  }
        finally { odd.readUnlock(); }
    }
  };
  
  
  private Action _gotoOpeningBraceAction =  new AbstractAction("Go to Opening Brace") {
    public void actionPerformed(ActionEvent ae) {
        OpenDefinitionsDocument odd = getCurrentDefPane().getOpenDefDocument();
        if (true) throw new RuntimeException("booh");
        odd.readLock();
        try {
          int pos = odd.findPrevEnclosingBrace(getCurrentDefPane().getCaretPosition(), '{', '}');
          if (pos!=AbstractDJDocument.ERROR_INDEX) { getCurrentDefPane().setCaretPosition(pos); }
        }
        catch(BadLocationException ble) {  }
        finally { odd.readUnlock(); }
    }
  };
  
  
  private void _switchToPane(Component c) {
    Component newC = c;
    if (c == _interactionsContainer) newC = _interactionsPane;
    
    if (c == _consoleScroll) newC = _consolePane;
    
    showTab(newC);
  }
  
  
  private void _switchPaneFocus(boolean next) {
    int numTabs = _tabbedPane.getTabCount();

    
    if (next) _switchToPane(_tabbedPane.getComponentAt((numTabs+_tabbedPane.getSelectedIndex()+1)%numTabs));
    else _switchToPane(_tabbedPane.getComponentAt((numTabs+_tabbedPane.getSelectedIndex()-1)%numTabs));
  }
  
  
  private Action _editPreferencesAction = new AbstractAction("Preferences ...") {
    public void actionPerformed(ActionEvent ae) {
      
      if (_configFrame == null) {
        _configFrame = new ConfigFrame(MainFrame.this);
      }
      _configFrame.setVisible(true);
      _configFrame.toFront();
    }
  };
  
  private AbstractAction _projectPropertiesAction = new AbstractAction("Project Properties") {
    public void actionPerformed(ActionEvent ae) { _editProject(); }
  };
    
  
  private Action _toggleDebuggerAction = new AbstractAction("Debug Mode") {
    public void actionPerformed(ActionEvent ae) { 
      this.setEnabled(false);
      debuggerToggle();
      this.setEnabled(true);
    }
  };
  
  
  private Action _resumeDebugAction = new AbstractAction("Resume Debugger") {
    public void actionPerformed(ActionEvent ae) {
      try { debuggerResume(); }
      catch (DebugException de) { _showDebugError(de); }
    }
  };
  
  
  private Action _stepIntoDebugAction = new AbstractAction("Step Into") {
    public void actionPerformed(ActionEvent ae) { debuggerStep(Debugger.STEP_INTO); }
  };
  
  
  private Action _stepOverDebugAction = new AbstractAction("Step Over") {
    public void actionPerformed(ActionEvent ae) { debuggerStep(Debugger.STEP_OVER); }
  };
  
  
  private Action _stepOutDebugAction = new AbstractAction("Step Out") {
    public void actionPerformed(ActionEvent ae) {
      debuggerStep(Debugger.STEP_OUT);
    }
  };
  
  
  
  
  
  Action _toggleBreakpointAction = new AbstractAction("Toggle Breakpoint on Current Line") {
    public void actionPerformed(ActionEvent ae) { debuggerToggleBreakpoint(); }
  };
  
  
  private Action _clearAllBreakpointsAction = new AbstractAction("Clear All Breakpoints") {
    public void actionPerformed(ActionEvent ae) { debuggerClearAllBreakpoints(); }
  };
  
  
  private Action _breakpointsPanelAction = new AbstractAction("Breakpoints") {
    public void actionPerformed(ActionEvent ae) {
      if (_mainSplit.getDividerLocation() > _mainSplit.getMaximumDividerLocation()) 
        _mainSplit.resetToPreferredSizes(); 
      if (! _breakpointsPanel.isDisplayed()) {
        showTab(_breakpointsPanel);
        _breakpointsPanel.beginListeningTo(_currentDefPane);
      }
      _breakpointsPanel.setVisible(true);
      _tabbedPane.setSelectedComponent(_breakpointsPanel);
      
      SwingUtilities.invokeLater(new Runnable() { public void run() { _breakpointsPanel.requestFocusInWindow(); } });
    }
  };
  
  
  protected Action _cutLineAction = new AbstractAction("Cut Line") {
    public void actionPerformed(ActionEvent ae) {
      ActionMap _actionMap = _currentDefPane.getActionMap();
      int oldCol = _model.getActiveDocument().getCurrentCol();
      _actionMap.get(DefaultEditorKit.selectionEndLineAction).actionPerformed(ae);
      
      
      
      if (oldCol == _model.getActiveDocument().getCurrentCol()) {
        
        _actionMap.get(DefaultEditorKit.selectionForwardAction).actionPerformed(ae);
        cutAction.actionPerformed(ae);
      }
      else cutAction.actionPerformed(ae);
    }
  };
  
  
  protected Action _clearLineAction = new AbstractAction("Clear Line") {
    public void actionPerformed(ActionEvent ae) {
      ActionMap _actionMap = _currentDefPane.getActionMap();
      _actionMap.get(DefaultEditorKit.selectionEndLineAction).actionPerformed(ae);
      _actionMap.get(DefaultEditorKit.deleteNextCharAction).actionPerformed(ae);
    }
  };
  
  
  private Action _beginLineAction = new AbstractAction("Begin Line") {
    public void actionPerformed(ActionEvent ae) {
      int beginLinePos = _getBeginLinePos();
      _currentDefPane.setCaretPosition(beginLinePos);
    }
  };

  
  private Action _selectionBeginLineAction = new AbstractAction("Select to Beginning of Line") {
    public void actionPerformed(ActionEvent ae) {
      int beginLinePos = _getBeginLinePos();
      _currentDefPane.moveCaretPosition(beginLinePos);
    }
  };
  
  
  private int _getBeginLinePos() {
    try {
      int currPos = _currentDefPane.getCaretPosition();
      OpenDefinitionsDocument openDoc = _model.getActiveDocument();
      openDoc.setCurrentLocation(currPos);
      return openDoc.getIntelligentBeginLinePos(currPos);
    }
    catch (BadLocationException ble) {
      
      throw new UnexpectedException(ble);
    }
  }
  
  private FileOpenSelector _interactionsHistoryFileSelector = new FileOpenSelector() {
    public File[] getFiles() throws OperationCanceledException {
      return getOpenFiles(_interactionsHistoryChooser);
    }
  };
  
  
  private Action _executeHistoryAction = new AbstractAction("Execute Interactions History...") {
    public void actionPerformed(ActionEvent ae) {
      
      _tabbedPane.setSelectedIndex(INTERACTIONS_TAB);
      
      _interactionsHistoryChooser.setDialogTitle("Execute Interactions History");
      try { _model.loadHistory(_interactionsHistoryFileSelector); }
      catch (FileNotFoundException fnf) { _showFileNotFoundError(fnf); }
      catch (IOException ioe) { _showIOError(ioe); }
      _interactionsPane.requestFocusInWindow();
    }
  };
  
  
  private void _closeInteractionsScript() {
    if (_interactionsScriptController != null) {
      _interactionsContainer.remove(_interactionsScriptPane);
      _interactionsScriptController = null;
      _interactionsScriptPane = null;
      _tabbedPane.invalidate();
      _tabbedPane.repaint();
    }
  }
  
  
  private Action _loadHistoryScriptAction = new AbstractAction("Load Interactions History as Script...") {
    public void actionPerformed(ActionEvent e) {
      try {
        _interactionsHistoryChooser.setDialogTitle("Load Interactions History");
        InteractionsScriptModel ism = _model.loadHistoryAsScript(_interactionsHistoryFileSelector);
        _interactionsScriptController = new InteractionsScriptController(ism, new AbstractAction("Close") {
          public void actionPerformed(ActionEvent e) {
            _closeInteractionsScript();
            _interactionsPane.requestFocusInWindow();
          }
        }, _interactionsPane);
        _interactionsScriptPane = _interactionsScriptController.getPane();
        _interactionsContainer.add(_interactionsScriptPane, BorderLayout.EAST);
        _tabbedPane.invalidate();
        _tabbedPane.repaint();
      }
      catch (FileNotFoundException fnf) { _showFileNotFoundError(fnf); }
      catch (IOException ioe) { _showIOError(ioe); }
      catch (OperationCanceledException oce) {
      }
    }
  };
  
  
  private Action _saveHistoryAction = new AbstractAction("Save Interactions History...") {
    public void actionPerformed(ActionEvent ae) {
      String[] options = {"Yes","No","Cancel"};
      int resp = JOptionPane.showOptionDialog(MainFrame.this,
                                              "Edit interactions history before saving?",
                                              "Edit History?",
                                              JOptionPane.YES_NO_CANCEL_OPTION,
                                              JOptionPane.QUESTION_MESSAGE,
                                              null,options,
                                              options[1]);
      
      if (resp == 2 || resp == JOptionPane.CLOSED_OPTION) return;
      
      String history = _model.getHistoryAsStringWithSemicolons();
      
      
      if (resp == 0)
        history = (new HistorySaveDialog(MainFrame.this)).editHistory(history);
      if (history == null) return; 
      
      _interactionsHistoryChooser.setDialogTitle("Save Interactions History");
      FileSaveSelector selector = new FileSaveSelector() {
        public File getFile() throws OperationCanceledException {
          
          
          
          
          
          File selection = _interactionsHistoryChooser.getSelectedFile();
          if (selection != null) {
            _interactionsHistoryChooser.setSelectedFile(selection.getParentFile());
            _interactionsHistoryChooser.setSelectedFile(selection);
            _interactionsHistoryChooser.setSelectedFile(null);
          }

          int rc = _interactionsHistoryChooser.showSaveDialog(MainFrame.this);
          File c = getChosenFile(_interactionsHistoryChooser, rc);
          
          
          if (c.getName().indexOf('.') == -1)
            c = new File(c.getAbsolutePath() + "." + InteractionsHistoryFilter.HIST_EXTENSION);
          _interactionsHistoryChooser.setSelectedFile(c);
          return c;
        }
        public boolean warnFileOpen(File f) { return true; }
        public boolean verifyOverwrite() { return _verifyOverwrite(); }
        public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) {
          return true;
        }
      };
      
      try { _model.saveHistory(selector, history);}
      catch (IOException ioe) {
        _showIOError(new IOException("An error occured writing the history to a file"));
      }
      _interactionsPane.requestFocusInWindow();
    }
  };
  
  
  private Action _clearHistoryAction = new AbstractAction("Clear Interactions History") {
    public void actionPerformed(ActionEvent ae) {
      _model.clearHistory();
      _interactionsPane.requestFocusInWindow();
    }
  };
  
  
  private WindowListener _windowCloseListener = new WindowAdapter() {
    public void windowActivated(WindowEvent ev) { }
    public void windowClosed(WindowEvent ev) { }
    public void windowClosing(WindowEvent ev) { _quit(); }
    public void windowDeactivated(WindowEvent ev) { }
    public void windowDeiconified(WindowEvent ev) {
      try { _model.getActiveDocument().revertIfModifiedOnDisk(); }
      catch (FileMovedException fme) { _showFileMovedError(fme); }
      catch (IOException e) { _showIOError(e);}
    }
    public void windowIconified(WindowEvent ev) { }
    public void windowOpened(WindowEvent ev) { _currentDefPane.requestFocusInWindow(); }
  };
  
  private MouseListener _resetFindReplaceListener = new MouseListener() {
    public void mouseClicked (MouseEvent e) { }
    public void mousePressed (MouseEvent e) { }
    
    public void mouseReleased (MouseEvent e) {_findReplace.updateFirstDocInSearch();}
    public void mouseEntered (MouseEvent e) { }
    public void mouseExited (MouseEvent e) { }
  };
  
  
  
  private static DJFileDisplayManager _djFileDisplayManager20;
  private static DJFileDisplayManager _djFileDisplayManager30;
  private static OddDisplayManager _oddDisplayManager20;
  private static OddDisplayManager _oddDisplayManager30;
  private static Icon _djProjectIcon;
  
  static {
    Icon java, dj0, dj1, dj2, other, star, jup, juf;
    
    java = MainFrame.getIcon("JavaIcon20.gif");
    dj0 = MainFrame.getIcon("ElementaryIcon20.gif");
    dj1 = MainFrame.getIcon("IntermediateIcon20.gif");
    dj2 = MainFrame.getIcon("AdvancedIcon20.gif");
    other = MainFrame.getIcon("OtherIcon20.gif");
    _djFileDisplayManager20 = new DJFileDisplayManager(java,dj0,dj1,dj2,other);
    
    java = MainFrame.getIcon("JavaIcon30.gif");
    dj0 = MainFrame.getIcon("ElementaryIcon30.gif");
    dj1 = MainFrame.getIcon("IntermediateIcon30.gif");
    dj2 = MainFrame.getIcon("AdvancedIcon30.gif");
    other = MainFrame.getIcon("OtherIcon30.gif");
    _djFileDisplayManager30 = new DJFileDisplayManager(java,dj0,dj1,dj2,other);
    
    star = MainFrame.getIcon("ModStar20.gif");
    jup = MainFrame.getIcon("JUnitPass20.gif");
    juf = MainFrame.getIcon("JUnitFail20.gif");
    _oddDisplayManager20 = new OddDisplayManager(_djFileDisplayManager20,star,jup,juf);
    
    star = MainFrame.getIcon("ModStar30.gif");
    jup = MainFrame.getIcon("JUnitPass30.gif");
    juf = MainFrame.getIcon("JUnitFail30.gif");
    _oddDisplayManager30 = new OddDisplayManager(_djFileDisplayManager30,star,jup,juf);
    
    _djProjectIcon = MainFrame.getIcon("ProjectIcon.gif");
  }
  
  
  
  private static class DJFileDisplayManager extends DefaultFileDisplayManager {
    private Icon _java;
    private Icon _dj0;
    private Icon _dj1;
    private Icon _dj2;
    private Icon _other;
    
    public DJFileDisplayManager(Icon java, Icon dj0, Icon dj1, Icon dj2, Icon other) {
      _java = java;
      _dj0 = dj0;
      _dj1 = dj1;
      _dj2 = dj2;
      _other = other;
    }
    
    public Icon getIcon(File f) {
      if (f == null) return _other;
      Icon ret = null;
      if (!f.isDirectory()) {
        String name = f.getName().toLowerCase();
        if (name.endsWith(".java")) ret = _java;
        if (name.endsWith(".dj0")) ret = _dj0;
        if (name.endsWith(".dj1")) ret = _dj1;
        if (name.endsWith(".dj2")) ret = _dj2;
      }
      if (ret == null) {
        ret = super.getIcon(f);
        if (ret.getIconHeight() < _java.getIconHeight()) {
          ret = new CenteredIcon(ret, _java.getIconWidth(), _java.getIconHeight());
        }
      }
      return ret;
    }
  }
  
  
  private static class OddDisplayManager implements DisplayManager<OpenDefinitionsDocument> {
    private Icon _star;


    private FileDisplayManager _default;
    
    
    public OddDisplayManager(FileDisplayManager fdm, Icon star, Icon junitPass, Icon junitFail) {
      _star = star;


      _default = fdm;
    }
    public Icon getIcon(OpenDefinitionsDocument odd) {
      File f = null;
      try { f = odd.getFile(); }
      catch(FileMovedException fme) {  }
      
      if (odd.isModifiedSinceSave()) return makeLayeredIcon(_default.getIcon(f), _star);
      return _default.getIcon(f);
    }
    public String getName(OpenDefinitionsDocument doc) { return doc.getFileName(); }
    private LayeredIcon makeLayeredIcon(Icon base, Icon star) {
      return new LayeredIcon(new Icon[]{base, star}, new int[]{0, 0}, 
                             new int[]{0, (base.getIconHeight() / 4)});
    }






  };
  
  
  private DisplayManager<INavigatorItem> _navPaneDisplayManager = new DisplayManager<INavigatorItem>() {
    public Icon getIcon(INavigatorItem item) {
      OpenDefinitionsDocument odd = (OpenDefinitionsDocument) item;  
      return _oddDisplayManager20.getIcon(odd);
    }
    public String getName(INavigatorItem name) { return name.getName(); }
  };
  
  
  public static DJFileDisplayManager getFileDisplayManager20() {
    return _djFileDisplayManager20;
  }
  public static DJFileDisplayManager getFileDisplayManager30() {
    return _djFileDisplayManager30;
  }
  public static OddDisplayManager getOddDisplayManager20() {
    return _oddDisplayManager20;
  }
  public static OddDisplayManager getOddDisplayManager30() {
    return _oddDisplayManager30;
  }
  
  public DisplayManager<INavigatorItem> getNavPaneDisplayManager() {
    return _navPaneDisplayManager;
  }
  
  
  
  
  public MainFrame() {
    
    final Configuration config = DrJava.getConfig();
   
    
    PlatformFactory.ONLY.beforeUISetup();
    
    
    _posListener = new PositionListener();
    _setUpStatusBar();
    

    
    
    _model = new DefaultGlobalModel();




















    

    
    _model.getDocumentNavigator().asContainer().addKeyListener(_historyListener);
    _model.getDocumentNavigator().asContainer().addFocusListener(_focusListenerForRecentDocs);
    
    
    _model.getDocumentNavigator().asContainer().addMouseListener(_resetFindReplaceListener);
      
    
    
    
    
    
    DefinitionsPane.setEditorKit(_model.getEditorKit());
    if (_model.getDebugger().isAvailable()) {
      
      _model.getDebugger().addListener(new UIDebugListener());
    }
    
    _debugStepTimer = new Timer(DEBUG_STEP_TIMER_VALUE, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        _model.printDebugMessage("Stepping...");
      }
    });
    _debugStepTimer.setRepeats(false);
    
    
    
    File workDir = _model.getMasterWorkingDirectory();

    
    _openChooser = new JFileChooser() {
      public void setCurrentDirectory(File dir) {
        
        super.setCurrentDirectory(dir);
        setDialogTitle("Open:  " + getCurrentDirectory());
      }
    };
    _openChooser.setPreferredSize(new Dimension(650, 410));
    _openChooser.setCurrentDirectory(workDir);
    _openChooser.setFileFilter(_javaSourceFilter);
    _openChooser.setMultiSelectionEnabled(true);
    
    _openRecursiveCheckBox = new JCheckBox("Open folders recursively");
    _openRecursiveCheckBox.setSelected(config.getSetting(OptionConstants.OPEN_FOLDER_RECURSIVE).booleanValue());
    
    _folderChooser = makeFolderChooser(workDir);
    
    
    Vector<File> recentProjects = config.getSetting(RECENT_PROJECTS);
    _openProjectChooser = new JFileChooser();
    _openProjectChooser.setPreferredSize(new Dimension(650, 410));
    
    if (recentProjects.size()>0 && recentProjects.elementAt(0).getParentFile() != null)
      _openProjectChooser.setCurrentDirectory(recentProjects.elementAt(0).getParentFile());
    else
      _openProjectChooser.setCurrentDirectory(workDir);
    
    _openProjectChooser.setFileFilter(_projectFilter);
    _openProjectChooser.setMultiSelectionEnabled(false);
    _saveChooser = new JFileChooser() {
      public void setCurrentDirectory(File dir) {
        
        super.setCurrentDirectory(dir);
        setDialogTitle("Save:  " + getCurrentDirectory());
      }
    };
    _saveChooser.setPreferredSize(new Dimension(650, 410));
    _saveChooser.setCurrentDirectory(workDir);
    _saveChooser.setFileFilter(_javaSourceFilter);
    
    _interactionsHistoryChooser = new JFileChooser();
    _interactionsHistoryChooser.setPreferredSize(new Dimension(650, 410));
    _interactionsHistoryChooser.setCurrentDirectory(workDir);
    _interactionsHistoryChooser.setFileFilter(new InteractionsHistoryFilter());
    _interactionsHistoryChooser.setMultiSelectionEnabled(true);
    
    
    setGlassPane(new GlassPane());
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    
    
    this.addWindowListener(_windowCloseListener);
    
    
    _mainListener = new ModelListener();
    _model.addListener(_mainListener);
    
    _defScrollPanes = new Hashtable<OpenDefinitionsDocument, JScrollPane>();
    
    
    _setUpTabs();
    
    
    JScrollPane defScroll = _createDefScrollPane(_model.getActiveDocument());
    _recentDocFrame  = new RecentDocFrame(this);
    _recentDocFrame.pokeDocument(_model.getActiveDocument());
    _currentDefPane = (DefinitionsPane) defScroll.getViewport().getView();
    _currentDefPane.notifyActive();
    
    
    KeyBindingManager.Singleton.setMainFrame(this);
    KeyBindingManager.Singleton.setActionMap(_currentDefPane.getActionMap());
    _setUpKeyBindingMaps();
    
    _posListener.updateLocation();
    
    
    
    _undoAction.setDelegatee(_currentDefPane.getUndoAction());
    _redoAction.setDelegatee(_currentDefPane.getRedoAction());
    
    _compilerErrorPanel.reset();
    _junitErrorPanel.reset();
    _javadocErrorPanel.reset();
    
    
    _setUpActions();
    _setUpMenuBar();
    _setUpToolBar();
    
    _setUpContextMenus();
    
    
    _recentFileManager = new RecentFileManager(_fileMenu.getItemCount() - 2,
                                               _fileMenu,
                                               this,false);
    
    _recentProjectManager = new RecentFileManager(_projectMenu.getItemCount()-2,
                                                  _projectMenu,
                                                  this,true);
    
    
    setIconImage(getIcon("drjava64.png").getImage());
    
    
    int x = config.getSetting(WINDOW_X).intValue();
    int y = config.getSetting(WINDOW_Y).intValue();
    int width = config.getSetting(WINDOW_WIDTH).intValue();
    int height = config.getSetting(WINDOW_HEIGHT).intValue();
    
    
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    final int menubarHeight = 24;
    if (height > screenSize.height - menubarHeight) {
      
      height = screenSize.height - menubarHeight;
    }
    if (width > screenSize.width) {
      
      width = screenSize.width;
    }
    
    
    
    Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
      .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
    
    if (x == Integer.MAX_VALUE) {
      
      x = (bounds.width - width + bounds.x) / 2;
    }
    if (y == Integer.MAX_VALUE) {
      
      y = (bounds.height - height + bounds.y) / 2;
    }
    
    if (x < bounds.x) {
      
      x = bounds.x;
    }
    if (y < bounds.y) {
      
      y = bounds.y;
    }
    if ((x + width) > (bounds.x + bounds.width)) {
      
      x = bounds.width - width + bounds.x;
    }
    if ((y + height) > (bounds.y + bounds.height)) {
      
      y = bounds.height - height + bounds.y;
    }
    
    
    setBounds(x, y, width, height);
    
    _setUpPanes();
    updateFileTitle();
    
    _promptBeforeQuit = config.getSetting(QUIT_PROMPT).booleanValue();
    
    
    _setMainFont();
    Font doclistFont = config.getSetting(FONT_DOCLIST);
    _model.getDocCollectionWidget().setFont(doclistFont);
    
    
    _updateNormalColor();
    _updateBackgroundColor();
    
    
    config.addOptionListener(DEFINITIONS_NORMAL_COLOR, new NormalColorOptionListener());
    config.addOptionListener(DEFINITIONS_BACKGROUND_COLOR, new BackgroundColorOptionListener());
    
    
    
    
    
    config.addOptionListener(FONT_MAIN, new MainFontOptionListener());
    config.addOptionListener(FONT_LINE_NUMBERS, new LineNumbersFontOptionListener());
    config.addOptionListener(FONT_DOCLIST, new DoclistFontOptionListener());
    config.addOptionListener(FONT_TOOLBAR, new ToolbarFontOptionListener());
    config.addOptionListener(TOOLBAR_ICONS_ENABLED, new ToolbarOptionListener());
    config.addOptionListener(TOOLBAR_TEXT_ENABLED, new ToolbarOptionListener());
    config.addOptionListener(TOOLBAR_ENABLED, new ToolbarOptionListener());
    config.addOptionListener(WORKING_DIRECTORY, new WorkingDirOptionListener());
    config.addOptionListener(LINEENUM_ENABLED, new LineEnumOptionListener());
    config.addOptionListener(QUIT_PROMPT, new QuitPromptOptionListener());
    config.addOptionListener(RECENT_FILES_MAX_SIZE, new RecentFilesOptionListener());
    
    config.addOptionListener(LOOK_AND_FEEL, new OptionListener<String>() {
      public void optionChanged(OptionEvent<String> oe) {





























        
        String title = "Apply Look and Feel";
        String msg = "Look and feel changes will take effect when you restart DrJava.";
        if (config.getSetting(WARN_CHANGE_LAF).booleanValue()) {
          ConfirmCheckBoxDialog dialog =
            new ConfirmCheckBoxDialog(_configFrame, title, msg,
                                      "Do not show this message again",
                                      JOptionPane.INFORMATION_MESSAGE,
                                      JOptionPane.DEFAULT_OPTION);
          if (dialog.show() == JOptionPane.OK_OPTION && dialog.getCheckBoxValue()) {
            config.setSetting(WARN_CHANGE_LAF, Boolean.FALSE);
          }
        }
      }
    });
    
   
    config.addOptionListener(JVM_ARGS, new OptionListener<String>() {
      public void optionChanged(OptionEvent<String> oe) {
        if (!oe.value.equals("")) {
          int result = JOptionPane.
            showConfirmDialog(_configFrame,
                              "Specifying JVM Args is an advanced option. Invalid arguments may cause the\n" +
                              "Interactions Pane to stop working.\n" + "Are you sure you want to set this option?\n" +
                              "(You will have to reset the interactions pane before changes take effect.)",
                              "Confirm JVM Arguments", JOptionPane.YES_NO_OPTION);
          if (result!=JOptionPane.YES_OPTION) config.setSetting(oe.option, "");
        }
      }
    });
    
    config.addOptionListener(ALLOW_PRIVATE_ACCESS, new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        _model.getInteractionsModel().setPrivateAccessible(oce.value.booleanValue());
      }
    });
    
    config.addOptionListener(FORCE_TEST_SUFFIX, new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        _model.getJUnitModel().setForceTestSuffix(oce.value.booleanValue());
      }
    });
    
    
    _breakpointHighlights = new java.util.Hashtable<Breakpoint, HighlightManager.HighlightInfo>();
    
    
    _configFrame = null;
    _helpFrame = null;
    _aboutDialog = null;
    _interactionsScriptController = null;

    
    
    _showConfigException();
    
    KeyBindingManager.Singleton.setShouldCheckConflict(false);
    
    
    PlatformFactory.ONLY.afterUISetup(_aboutAction, _editPreferencesAction, _quitAction);
    setUpKeys();
  }
  
  private DirectoryChooser makeFolderChooser(File workDir) {
    DirectoryChooser dc = new DirectoryChooser(this);
    dc.setSelectedFile(workDir);
    dc.setApproveButtonText("Select");
    dc.setDialogTitle("Open Folder");
    dc.setAccessory(_openRecursiveCheckBox);
    return dc;
  }






























  














  
  
  RecentDocFrame _recentDocFrame;
  
  
  private void setUpKeys() { setFocusTraversalKeysEnabled(false); }
  
  
  public void dispose() {
    _model.dispose();
    Utilities.invokeAndWait(new Runnable() { public void run() { disposeHelp(); }});
  }
  
  
  private void disposeHelp() { super.dispose(); }
  
  
  public SingleDisplayModel getModel() { return _model; }
  
  
  InteractionsPane getInteractionsPane() { return _interactionsPane; }
  
  
  InteractionsController getInteractionsController() { return _interactionsController; }
  
  
  JButton getCloseButton() { return _closeButton; }
  
  
  JButton getCompileAllButton() { return _compileButton; }
  
  
  private int hourglassNestLevel = 0;
  public void hourglassOn() {
    hourglassNestLevel++;
    if (hourglassNestLevel == 1) {      
      Utilities.invokeAndWait(new Runnable() {
        public void run() { 
          getGlassPane().setVisible(true);
          _currentDefPane.setEditable(false);
          setAllowKeyEvents(false); }
      });
    }
  }
  
  
  public void hourglassOff() { 
    hourglassNestLevel--;

    if (hourglassNestLevel == 0) {
      Utilities.invokeAndWait(new Runnable() {
        public void run() {
          getGlassPane().setVisible(false);
          _currentDefPane.setEditable(true);
          setAllowKeyEvents(true);
        }
      });
    }
  }
  
  private boolean allow_key_events = true;
  public void setAllowKeyEvents(boolean a) { this.allow_key_events = a; }
  
  public boolean getAllowKeyEvents() { return this.allow_key_events; }
  
  
  public void debuggerToggle() {
    
    Debugger debugger = _model.getDebugger();
    if (!debugger.isAvailable()) return;
    
    try { 
      if (inDebugMode()) debugger.shutdown();
      else {
        
        hourglassOn();
        try {
          debugger.startup();  
          _model.refreshActiveDocument();
          _updateDebugStatus();
        }
        finally { hourglassOff(); }
      }
    }
    catch (DebugException de) {
      _showError(de, "Debugger Error", "Could not start the debugger.");
    }
    catch (NoClassDefFoundError err) {
      _showError(err, "Debugger Error",
                 "Unable to find the JPDA package for the debugger.\n" +
                 "Please make sure either tools.jar or jpda.jar is\n" +
                 "in your classpath when you start DrJava.");
      _setDebugMenuItemsEnabled(false);
    }
  }
  
  
  public void showDebugger() {
    _setDebugMenuItemsEnabled(true);
    _showDebuggerPanel();
  }
  
  
  public void hideDebugger() {
    _setDebugMenuItemsEnabled(false);
    _hideDebuggerPanel();
  }
  
  private void _showDebuggerPanel() {
    _debugSplitPane.setTopComponent(_docSplitPane);
    _mainSplit.setTopComponent(_debugSplitPane);
    _debugPanel.updateData();
    _lastFocusOwner.requestFocusInWindow();
  }
  
  private void _hideDebuggerPanel() {
    _mainSplit.setTopComponent(_docSplitPane);
    _lastFocusOwner.requestFocusInWindow();
  }
  
  public void updateFileTitle(String text) {
    _fileNameField.setText(text);
  }
  
  
  public void updateFileTitle() {
    OpenDefinitionsDocument doc = _model.getActiveDocument();
    String fileName = doc.getCompletePath();
    if (!fileName.equals(_fileTitle)) {
      _fileTitle = fileName;
      setTitle("File: " + fileName);
      _model.getDocCollectionWidget().repaint();
    }
    
    String fileTitle = doc.getCompletePath();







    if (! _fileNameField.getText().equals(fileTitle)) { _fileNameField.setText(fileTitle); }
    
    
    _fileNameField.setToolTipText("Full path for file: " + doc.getCompletePath());

  }
  
  
  public File[] getOpenFiles(JFileChooser jfc) throws OperationCanceledException {
    
    File selection = jfc.getSelectedFile();
    if (selection != null) { 
      jfc.setSelectedFile(selection.getParentFile());
      jfc.setSelectedFile(selection);
      jfc.setSelectedFile(null);
    }
    int rc = jfc.showOpenDialog(this);
    return getChosenFiles(jfc, rc);
  }
  
  
  public File getSaveFile(JFileChooser jfc) throws OperationCanceledException {
    
    File selection = jfc.getSelectedFile();
    if (selection != null) {
      jfc.setSelectedFile(selection.getParentFile());
      jfc.setSelectedFile(selection);
      jfc.setSelectedFile(null);
    }
    
    OpenDefinitionsDocument active = _model.getActiveDocument();
    
    
    
    try {
      String className = active.getFirstTopLevelClassName();
      if (!className.equals("")) {
        jfc.setSelectedFile(new File(jfc.getCurrentDirectory(), className));
      }
    }
    catch (ClassNameNotFoundException e) {
      
    }
    
    _saveChooser.removeChoosableFileFilter(_projectFilter);
    _saveChooser.removeChoosableFileFilter(_javaSourceFilter);
    _saveChooser.setFileFilter(_javaSourceFilter);
    int rc = jfc.showSaveDialog(this);
    return getChosenFile(jfc, rc);
  }
  
  
  public DefinitionsPane getCurrentDefPane() { return _currentDefPane; }
  
  
  public ErrorPanel getSelectedErrorPanel() {
    Component c = _tabbedPane.getSelectedComponent();
    if (c instanceof ErrorPanel) return (ErrorPanel) c;
    return null;
  }
  
  
  public boolean isCompilerTabSelected() {
    return _tabbedPane.getSelectedComponent() == _compilerErrorPanel;
  }
  
  
  public boolean isTestTabSelected() {
    return _tabbedPane.getSelectedComponent() == _junitErrorPanel;
  }
  
  
  public boolean isJavadocTabSelected() {
    return _tabbedPane.getSelectedComponent() == _javadocErrorPanel;
  }
  
  
  private void _installNewDocumentListener(final Document d) {
    d.addDocumentListener(new DocumentUIListener() {
      public void changedUpdate(DocumentEvent e) {
        Utilities.invokeLater(new Runnable() {
          public void run() {
            OpenDefinitionsDocument doc = _model.getActiveDocument();
            if (doc.isModifiedSinceSave()) {
              _saveAction.setEnabled(true);
              if (inDebugMode() && _debugPanel.getStatusText().equals(""))
                _debugPanel.setStatusText(DEBUGGER_OUT_OF_SYNC);
              updateFileTitle();
            }
          }
        });
      }
      public void insertUpdate(DocumentEvent e) {
        Utilities.invokeLater(new Runnable() {
          public void run() {
            _saveAction.setEnabled(true);
            if (inDebugMode() && _debugPanel.getStatusText().equals(""))
              _debugPanel.setStatusText(DEBUGGER_OUT_OF_SYNC);
            updateFileTitle();
          }
        });
      }
      public void removeUpdate(DocumentEvent e) {
        Utilities.invokeLater(new Runnable() {
          public void run() {
            _saveAction.setEnabled(true);
            if (inDebugMode() && _debugPanel.getStatusText().equals(""))
              _debugPanel.setStatusText(DEBUGGER_OUT_OF_SYNC);
            updateFileTitle();
          }
        });
      }
    });
  }
  
  
  
  public void setStatusMessage(String msg) { _sbMessage.setText(msg); }
  
  
  public void clearStatusMessage() { _sbMessage.setText(""); }
  
  
  public void setStatusMessageFont(Font f) { _sbMessage.setFont(f); }
  
  
  public void setStatusMessageColor(Color c) { _sbMessage.setForeground(c); }
  
  
  void _moveToAuxiliary() {
    OpenDefinitionsDocument d = _model.getDocumentNavigator().getCurrent();
    if (d != null) {
      if (! d.isUntitled()) {
        _model.addAuxiliaryFile(d);
        try{
          _model.getDocumentNavigator().refreshDocument(d, _model.fixPathForNavigator(d.getFile().getCanonicalPath()));
        }
        catch(IOException e) {  }
      }
    }
  }
  
  private void _removeAuxiliary() {
    OpenDefinitionsDocument d = _model.getDocumentNavigator().getCurrent();
    if (d != null) {
      if (! d.isUntitled()) {
        _model.removeAuxiliaryFile(d);
        try{
          _model.getDocumentNavigator().refreshDocument(d, _model.fixPathForNavigator(d.getFile().getCanonicalPath()));
        }
        catch(IOException e) {  }
      }
    }
  }
  
  private void _new() { _model.newFile(); }
  
  private void _open() { open(_openSelector); }
  
  private void _openFolder() { openFolder(_folderChooser); }
  
  private void _openFileOrProject() {
    try {
      final File[] fileList = _openFileOrProjectSelector.getFiles();
      
      FileOpenSelector fos = new FileOpenSelector() {
        public File[] getFiles() { return fileList; }
      };
      
      if (_openChooser.getFileFilter().equals(_projectFilter)) openProject(fos);
      else open(fos);
    }
    catch(OperationCanceledException oce) {  }
  }
  
  
  private void _putTextIntoDefinitions(String text) {
    int caretPos = _currentDefPane.getCaretPosition();
    
    try { _model.getActiveDocument().insertString(caretPos, text, null); }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }
  }
  
  
  private void _resetNavigatorPane() {
    if (_model.getDocumentNavigator() instanceof JTreeSortNavigator) {
      JTreeSortNavigator<?> nav = (JTreeSortNavigator<?>)_model.getDocumentNavigator();
      nav.setDisplayManager(getNavPaneDisplayManager());
      nav.setRootIcon(_djProjectIcon);
    }
    _docSplitPane.remove(_docSplitPane.getLeftComponent());
    _docSplitPane.setLeftComponent(new JScrollPane(_model.getDocumentNavigator().asContainer()));
    Font doclistFont = DrJava.getConfig().getSetting(FONT_DOCLIST);
    _model.getDocCollectionWidget().setFont(doclistFont);
    _updateNormalColor();
    _updateBackgroundColor();
  }
  
  
  private void _openProject() { openProject(_openProjectSelector); }
  
  public void openProject(FileOpenSelector projectSelector) {
    
    try {
      hourglassOn();
      final File[] file = projectSelector.getFiles();
      if (file.length < 1)
        throw new IllegalStateException("Open project file selection not canceled but no project file was selected.");
      
      
      if (!_model.isProjectActive() || (_model.isProjectActive() && _closeProject())) _openProjectHelper(file[0]);
    }
    catch(OperationCanceledException oce) {
      
    }
    catch(Exception e) { e.printStackTrace(System.out); }
    finally { hourglassOff(); }    
  }
  
  
  
  private void _openProjectHelper(File projectFile) {
    _currentProjFile = projectFile;
    try {
      _mainListener.resetFNFCount();
      _model.openProject(projectFile);
      if (_mainListener.filesNotFound()) _model.setProjectChanged(true);
      _completeClassList = new ArrayList<GoToFileListEntry>(); 
    }
    catch(MalformedProjectFileException e) {
      _showProjectFileParseError(e); 
      return;
    }
    catch(FileNotFoundException e) {
      _showFileNotFoundError(e); 
      return;
    }
    catch(IOException e) {
      _showIOError(e); 
      return;
    }
  }
  
  private void _openProjectUpdate() {
    if (_model.isProjectActive()) {
      _closeProjectAction.setEnabled(true);
      _saveProjectAction.setEnabled(true);
      _saveProjectAsAction.setEnabled(true);
      _projectPropertiesAction.setEnabled(true);

      _junitOpenProjectFilesAction.setEnabled(true);

      _compileProjectAction.setEnabled(true);
      _jarProjectAction.setEnabled(true);
      if (_model.getBuildDirectory() != null) _cleanAction.setEnabled(true);
      _resetNavigatorPane();
      _compileButton.setToolTipText("<html>Compile all documents in the project.<br>External files are excluded.</html>");
    }
  }
  
  
  
  
  
  boolean _closeProject() {
    _completeClassList = new ArrayList<GoToFileListEntry>(); 
    return _closeProject(false);
  }
  
  
  boolean _closeProject(boolean quitting) {
    if (_checkProjectClose()) {
      List<OpenDefinitionsDocument> projDocs = _model.getProjectDocuments();

      
      
      
      boolean couldClose = _model.closeFiles(projDocs);
      if (! couldClose) return false;
      _model.closeProject(quitting);
      Component renderer = _model.getDocumentNavigator().getRenderer();
      new ForegroundColorListener(renderer);
      new BackgroundColorListener(renderer);
      _resetNavigatorPane();
      if (_model.getDocumentCount() == 1) _model.setActiveFirstDocument();
      _closeProjectAction.setEnabled(false);
      _saveProjectAction.setEnabled(false);
      _saveProjectAsAction.setEnabled(false);
      _projectPropertiesAction.setEnabled(false);

      _jarProjectAction.setEnabled(false);
      _junitOpenProjectFilesAction.setEnabled(false);

      _compileProjectAction.setEnabled(false);
      _setUpContextMenus();
      _currentProjFile = null;
      _compileButton.setToolTipText("Compile all open documents");
      return true;
    }
    else return false;  
  }
  
  private boolean _checkProjectClose() {
    if (_model.isProjectChanged()) {
      String fname = _model.getProjectFile().getName();
      String text = fname + " has been modified. Would you like to save it?";
      int rc = 
        JOptionPane.showConfirmDialog(MainFrame.this, text, "Save " + fname + "?", JOptionPane.YES_NO_CANCEL_OPTION);
      switch (rc) {
        case JOptionPane.YES_OPTION:
          _saveProject();
          return true;
        case JOptionPane.NO_OPTION:
          return true;
        case JOptionPane.CLOSED_OPTION:
        case JOptionPane.CANCEL_OPTION:
          return false;
        default:
          throw new RuntimeException("Invalid rc: " + rc);        
      }
    } 
    return true;
  }
  
  public File getCurrentProject() { return _currentProjFile;  }
  
  
  public void open(FileOpenSelector openSelector) {
    try {
      hourglassOn();
      _model.openFiles(openSelector);
    }
    catch (AlreadyOpenException aoe) {
      OpenDefinitionsDocument openDoc = aoe.getOpenDocument();
      String fileName;
      try { fileName = openDoc.getFile().getName(); }
      catch (IllegalStateException ise) {
        
        throw new UnexpectedException(ise);
      }
      catch (FileMovedException fme) {
        
        fileName = fme.getFile().getName();
      }
      
      
      _model.setActiveDocument(openDoc);
      
      
      if (openDoc.isModifiedSinceSave()) {
        String title = "Revert to Saved?";
        String message = fileName + " is already open and modified.\n" +
          "Would you like to revert to the version on disk?\n";
        int choice = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) _revert();
      }
      try {
        File f = openDoc.getFile();
        if (! _model.inProject(f)) _recentFileManager.updateOpenFiles(f);
      }
      catch (IllegalStateException ise) {
        
        throw new UnexpectedException(ise);
      }
      catch (FileMovedException fme) {
        File f = fme.getFile();
        
        if (! _model.inProject(f))
          _recentFileManager.updateOpenFiles(f);
      }
    }  
    catch (OperationCanceledException oce) {  }
    catch (FileNotFoundException fnf) { 
      _showFileNotFoundError(fnf); 
    }
    catch (IOException ioe) { _showIOError(ioe); }
    finally { hourglassOff(); }
  }
  
  
  
  public void openFolder(DirectoryChooser chooser) {
    String type = "'" + DrJavaRoot.LANGUAGE_LEVEL_EXTENSIONS[DrJava.getConfig().getSetting(LANGUAGE_LEVEL)] + "' ";
    chooser.setDialogTitle("Open All " + type + "Files in ...");
    
    File openDir = null;
    try { 
      File activeFile = _model.getActiveDocument().getFile();
      if (activeFile != null) openDir = activeFile.getParentFile();
      else openDir = _model.getProjectRoot();
    }
    catch(FileMovedException e) {  }
    
    int result = chooser.showDialog(openDir);
    if (result != DirectoryChooser.APPROVE_OPTION)  return; 
    
    File dir = chooser.getSelectedDirectory();
    boolean rec = _openRecursiveCheckBox.isSelected();
    DrJava.getConfig().setSetting(OptionConstants.OPEN_FOLDER_RECURSIVE, Boolean.valueOf(rec));
    _openFolder(dir, rec);
  }
  
  
  private void _openFolder(File dir, boolean rec) {
    hourglassOn();
    try { _model.openFolder(dir, rec); }
    catch(AlreadyOpenException e) {  }
    catch(IOException e) { _showIOError(e); }
    catch(OperationCanceledException oce) {  }
    finally { hourglassOff(); }
  }
    
  
  private void _close() {
    
    
    
    
    if ((_model.isProjectActive() && _model.getActiveDocument().inProjectPath()) ||
        _model.getActiveDocument().isAuxiliaryFile()) {
      
      String fileName = null;
      OpenDefinitionsDocument doc = _model.getActiveDocument();
      try{
        if (doc.isUntitled()) fileName = "File";
        else fileName = _model.getActiveDocument().getFile().getName();
      }
      catch(FileMovedException e) { fileName = e.getFile().getName(); }
      String text = "Closing this file will permanently remove it from the current project." + 
        "\nAre you sure that you want to close this file?";
      
      Object[] options = {"Yes", "No"};
      int rc = 
        JOptionPane.showOptionDialog(MainFrame.this, text,"Close " + fileName + "?", JOptionPane.YES_NO_OPTION,
                                     JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
      if (rc != JOptionPane.YES_OPTION) return;
      _model.setProjectChanged(true);
    }
    
    
    _model.closeFile(_model.getActiveDocument());
  }
  
  private void _closeFolder() {
    OpenDefinitionsDocument d;
    Enumeration<OpenDefinitionsDocument> e = _model.getDocumentNavigator().getDocuments();
    final LinkedList<OpenDefinitionsDocument> l = new LinkedList<OpenDefinitionsDocument>();
    if (_model.getDocumentNavigator().isGroupSelected()) {
      while (e.hasMoreElements()) {
        d = e.nextElement();
        if (_model.getDocumentNavigator().isSelectedInGroup(d)) { l.add(d); }
      }
      _model.closeFiles(l);
      if (! l.isEmpty()) _model.setProjectChanged(true);
    }
  }
  
  private void _printDefDoc() {
    try {
      _model.getActiveDocument().print();
    }
    catch (FileMovedException fme) {
      _showFileMovedError(fme);
    }
    catch (PrinterException e) {
      _showError(e, "Print Error", "An error occured while printing.");
    }
    catch (BadLocationException e) {
      _showError(e, "Print Error", "An error occured while printing.");
    }
  }
  
  private void _printConsole() {
    try {
      _model.getConsoleDocument().print();
    }
    catch (PrinterException e) {
      _showError(e, "Print Error", "An error occured while printing.");
    }
  }
  
  private void _printInteractions() {
    try {
      _model.getInteractionsDocument().print();
    }
    catch (PrinterException e) {
      _showError(e, "Print Error", "An error occured while printing.");
    }
  }
  
  
  private void _printDefDocPreview() {
    try {
      _model.getActiveDocument().preparePrintJob();
      new PreviewDefDocFrame(_model, this);
    }
    catch (FileMovedException fme) {
      _showFileMovedError(fme);
    }
    catch (BadLocationException e) {
      _showError(e, "Print Error",
                 "An error occured while preparing the print preview.");
    }
    catch (IllegalStateException e) {
      _showError(e, "Print Error",
                 "An error occured while preparing the print preview.");
    }
  }

  private void _printConsolePreview() {
    try {
      _model.getConsoleDocument().preparePrintJob();
      new PreviewConsoleFrame(_model, this, false);
    }
    catch (IllegalStateException e) {
      _showError(e, "Print Error",
                 "An error occured while preparing the print preview.");
    }
  }
  
  private void _printInteractionsPreview() {
    try {
      _model.getInteractionsDocument().preparePrintJob();
      new PreviewConsoleFrame(_model, this, true);
    }
    catch (IllegalStateException e) {
      _showError(e, "Print Error",
                 "An error occured while preparing the print preview.");
    }
  }
  
  private void _pageSetup() {
    PrinterJob job = PrinterJob.getPrinterJob();
    _model.setPageFormat(job.pageDialog(_model.getPageFormat()));
  }
  
  
  void closeAll() { _closeAll(); }
  
  private void _closeAll() {
    if (!_model.isProjectActive() || _model.isProjectActive() && _closeProject())    
      _model.closeAllFiles();
  }
  
  private boolean _save() {
    try {
      if (_model.getActiveDocument().saveFile(_saveSelector)) {
        _currentDefPane.hasWarnedAboutModified(false); 
        
        
        _model.setActiveDocument(_model.getActiveDocument());
        
        return true;
      }
      else return false;
    }
    catch (IOException ioe) { 
      _showIOError(ioe);
      return false;
    }
  }
  
  
  private boolean _saveAs() {
    try {
      boolean toReturn = _model.getActiveDocument().saveFileAs(_saveAsSelector);
      
      _model.setActiveDocument(_model.getActiveDocument());
      return toReturn;
    }
    catch (IOException ioe) {
      _showIOError(ioe);
      return false;
    }
  }
  
  private void _saveAll() {
    hourglassOn();
    try {
      if (_model.isProjectActive()) _saveProject();
      _model.saveAllFiles(_saveSelector);
    }
    catch (IOException ioe) { _showIOError(ioe); }
    finally { hourglassOff(); }
  }
  
  
  void saveProject() { _saveProject(); }
  
  private void _saveProject() {
    
    _saveProjectHelper(_currentProjFile);
  }
  
    
  private void _editProject() {
    ProjectPropertiesFrame ppf = new ProjectPropertiesFrame(this);
    ppf.setVisible(true);
    ppf.reset();
    ppf.toFront();  
  }
  
  
  private void _newProject() {

    _closeProject(true);  
    _saveChooser.setFileFilter(_projectFilter);
    int rc = _saveChooser.showSaveDialog(this);
    if (rc == JFileChooser.APPROVE_OPTION) {      
      File pf = _saveChooser.getSelectedFile();  
      if (pf.exists() && !_verifyOverwrite()) { return; }

      String fileName = pf.getName();
      
      if (! fileName.endsWith(".pjt")) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) pf = new File (pf.getAbsolutePath() + ".pjt");
        else pf = new File(fileName.substring(0, lastIndex) + ".pjt");
      }
      
      _model.createNewProject(pf); 


      _editProject();  
      try { _model.configNewProject(); }  
      catch(IOException e) { throw new UnexpectedException(e); }
      _currentProjFile = pf;
    }
  }

  
  private boolean _saveProjectAs() {
    

    _saveChooser.removeChoosableFileFilter(_projectFilter);
    _saveChooser.removeChoosableFileFilter(_javaSourceFilter);
    _saveChooser.setFileFilter(_projectFilter);






    
    if (_currentProjFile != null) _saveChooser.setSelectedFile(_currentProjFile);
    
    int rc = _saveChooser.showSaveDialog(this);
    if (rc == JFileChooser.APPROVE_OPTION) {
      File file = _saveChooser.getSelectedFile();
      if (! file.exists() || _verifyOverwrite()) { 
        _model.setProjectFile(file);
        _currentProjFile = file;
      }
    }
    
    return (rc == JFileChooser.APPROVE_OPTION);
  }
  
  void _saveProjectHelper(File file) {
    try {
      if (file.getName().indexOf(".") == -1) file = new File (file.getAbsolutePath() + ".pjt");
      String fileName = file.getCanonicalPath();
      _model.saveProject(file, gatherProjectDocInfo());



    }
    catch(IOException ioe) { _showIOError(ioe); }
    _recentProjectManager.updateOpenFiles(file);
    _model.setProjectChanged(false);
  }
  
  public Hashtable<OpenDefinitionsDocument,DocumentInfoGetter> gatherProjectDocInfo() {
    Hashtable<OpenDefinitionsDocument,DocumentInfoGetter> map =
      new Hashtable<OpenDefinitionsDocument,DocumentInfoGetter>();
    List<OpenDefinitionsDocument> docs = _model.getProjectDocuments();
    for(OpenDefinitionsDocument doc: docs) {
      map.put(doc, _makeInfoGetter(doc));
    }
    return map;
  }
  
  private DocumentInfoGetter _makeInfoGetter(final OpenDefinitionsDocument doc) {
    JScrollPane s = _defScrollPanes.get(doc);
    if (s == null) {
      s = _createDefScrollPane(doc);
    }
    final JScrollPane scroller = s;
    final DefinitionsPane pane = (DefinitionsPane)scroller.getViewport().getView();
    
    return new DocumentInfoGetter() {
      public Pair<Integer,Integer> getSelection() {
        Integer selStart = new Integer(pane.getSelectionStart());
        Integer selEnd = new Integer(pane.getSelectionEnd());
        if (pane.getCaretPosition() == selStart) return new Pair<Integer,Integer>(selEnd,selStart);
        return new Pair<Integer,Integer>(selStart,selEnd);
      }
      public Pair<Integer,Integer> getScroll() {
        Integer scrollv = new Integer(pane.getVerticalScroll());
        Integer scrollh = new Integer(pane.getHorizontalScroll());
        return new Pair<Integer,Integer>(scrollv,scrollh); 
      }
      public File getFile() { return doc.getRawFile(); }
      public String getPackage() {
        try { return doc.getPackageName(); }
        catch(InvalidPackageException e) { return null; }
      }
      public boolean isActive() { return _model.getActiveDocument() == doc; }
      public boolean isUntitled() { return doc.isUntitled(); }
    };
  }
  
  private void _revert() {
    try {
      _model.getActiveDocument().revertFile();
    }
    catch (FileMovedException fme) {
      _showFileMovedError(fme);
    }
    catch (IOException ioe) {
      _showIOError(ioe);
    }
  }
  
  
  
  
  private void _saveCurrentDirectory() {
    try {
      try {
        DrJava.getConfig().setSetting(LAST_DIRECTORY, _getFullFile(_model.getActiveDocument().getFile()));
      }
      catch (IllegalStateException ise) {
        
        
        DrJava.getConfig().setSetting(LAST_DIRECTORY, _getFullFile(_openChooser.getCurrentDirectory()));
      }
      catch (FileMovedException fme) {
        
        DrJava.getConfig().setSetting(LAST_DIRECTORY, _getFullFile(fme.getFile()));
      }
    }
    catch (IOException ioe) {
      
      
    }
    catch (Throwable t) {
      

    }
  }
  
  private void _quit() {
    if (_promptBeforeQuit) {
      String title = "Quit DrJava?";
      String message = "Are you sure you want to quit DrJava?";
      ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(MainFrame.this, title, message);
      int rc = dialog.show();
      if (rc != JOptionPane.YES_OPTION) return;
      else {
        
        if (dialog.getCheckBoxValue() == true) {
          DrJava.getConfig().setSetting(QUIT_PROMPT, Boolean.FALSE);
        }
      }
    }

    if (!_closeProject(true)) { return;  }
    
    _recentFileManager.saveRecentFiles();
    _recentProjectManager.saveRecentFiles();
    _storePositionInfo();
    _saveCurrentDirectory();
    
    
    
    if (!DrJava.getConfig().hadStartupException()) {
      try { DrJava.getConfig().saveConfiguration(); }
      catch (IOException ioe) { _showIOError(ioe); }
    }
    
    _model.quit();
  }
  
  
  private void _storePositionInfo() {
    Configuration config = DrJava.getConfig();
    
    
    if (config.getSetting(WINDOW_STORE_POSITION).booleanValue()) {
      Rectangle bounds = getBounds();
      config.setSetting(WINDOW_HEIGHT, new Integer(bounds.height));
      config.setSetting(WINDOW_WIDTH, new Integer(bounds.width));
      config.setSetting(WINDOW_X, new Integer(bounds.x));
      config.setSetting(WINDOW_Y, new Integer(bounds.y));
    }
    else {
      
      config.setSetting(WINDOW_HEIGHT, WINDOW_HEIGHT.getDefault());
      config.setSetting(WINDOW_WIDTH, WINDOW_WIDTH.getDefault());
      config.setSetting(WINDOW_X, WINDOW_X.getDefault());
      config.setSetting(WINDOW_Y, WINDOW_Y.getDefault());
    }
    
    
    if ((DrJava.getConfig().getSetting(DIALOG_GOTOFILE_STORE_POSITION).booleanValue())
          && (_gotoFileDialog != null) && (_gotoFileDialog.getFrameState() != null)) {
      config.setSetting(DIALOG_GOTOFILE_STATE, (_gotoFileDialog.getFrameState().toString()));
    }
    else {
      
      config.setSetting(DIALOG_GOTOFILE_STATE, DIALOG_GOTOFILE_STATE.getDefault());
    }
    
    
    if ((DrJava.getConfig().getSetting(DIALOG_COMPLETE_FILE_STORE_POSITION).booleanValue())
          && (_completeFileDialog != null) && (_completeFileDialog.getFrameState() != null)) {
      config.setSetting(DIALOG_COMPLETE_FILE_STATE, (_completeFileDialog.getFrameState().toString()));
    }
    else {
      
      config.setSetting(DIALOG_COMPLETE_FILE_STATE, DIALOG_COMPLETE_FILE_STATE.getDefault());
    }
        
    
    if ((DrJava.getConfig().getSetting(DIALOG_JAROPTIONS_STORE_POSITION).booleanValue())
          && (_jarOptionsDialog != null) && (_jarOptionsDialog.getFrameState() != null)) {
      config.setSetting(DIALOG_JAROPTIONS_STATE, (_jarOptionsDialog.getFrameState().toString()));
    }
    else {
      
      config.setSetting(DIALOG_JAROPTIONS_STATE, DIALOG_JAROPTIONS_STATE.getDefault());
    }
    
    
    if (_debugPanel != null) {
      config.setSetting(DEBUG_PANEL_HEIGHT, new Integer(_debugPanel.getHeight()));
    }
    
    
    config.setSetting(DOC_LIST_WIDTH, new Integer(_docSplitPane.getDividerLocation()));
  }
  
  private void _cleanUpForCompile() { if (inDebugMode()) _model.getDebugger().shutdown(); }
  
  private void _compile() {
    _cleanUpForCompile();
    hourglassOn();
    try {
      final OpenDefinitionsDocument doc = _model.getActiveDocument();


          try { _model.getCompilerModel().compile(doc); }
          catch (FileMovedException fme) { _showFileMovedError(fme); }
          catch (IOException ioe) { _showIOError(ioe); }


    }
    finally { hourglassOff();}

  }
  
  private void _compileFolder() {
    _cleanUpForCompile();
    hourglassOn();
    try {
      OpenDefinitionsDocument d;
      Enumeration<OpenDefinitionsDocument> e = _model.getDocumentNavigator().getDocuments();
      final LinkedList<OpenDefinitionsDocument> l = new LinkedList<OpenDefinitionsDocument>();
      if (_model.getDocumentNavigator().isGroupSelected()) {
        while (e.hasMoreElements()) {
          d = e.nextElement();
          if (_model.getDocumentNavigator().isSelectedInGroup(d)) l.add(d);
        }
        


            try { _model.getCompilerModel().compile(l); }
            catch (FileMovedException fme) { _showFileMovedError(fme); }
            catch (IOException ioe) { _showIOError(ioe); }


      }
    }
    finally { hourglassOff(); }

  }
  
  private void _compileProject() { _compileAll(); }
  
  private void _compileAll() {
    _cleanUpForCompile();


    hourglassOn();
    try { _model.getCompilerModel().compileAll(); }
    catch (FileMovedException fme) { _showFileMovedError(fme); }
    catch (IOException ioe) { _showIOError(ioe); }
    finally { hourglassOff();}



  }
  
  private boolean showCleanWarning() {
    if (DrJava.getConfig().getSetting(PROMPT_BEFORE_CLEAN).booleanValue()) {
      String buildDirTxt = "";
      try {
        buildDirTxt = _model.getBuildDirectory().getCanonicalPath();
      }
      catch (Exception e) {
        buildDirTxt = _model.getBuildDirectory().getPath();
      }
      ConfirmCheckBoxDialog dialog =
        new ConfirmCheckBoxDialog(MainFrame.this,
                                  "Clean Build Directory?",
                                  "Cleaning your build directory will delete all\n" + 
                                  "class files and empty folders within that directory.\n" + 
                                  "Are you sure you want to clean\n" + 
                                  buildDirTxt + "?",
                                  "Do not show this message again");
      int rc = dialog.show();
      switch (rc) {
        case JOptionPane.YES_OPTION:
          _saveAll();
          
          if (dialog.getCheckBoxValue()) {
            DrJava.getConfig().setSetting(PROMPT_BEFORE_CLEAN, Boolean.FALSE);
          }
          return true;
        case JOptionPane.NO_OPTION:
          return false;
        case JOptionPane.CANCEL_OPTION:
          return false;
        case JOptionPane.CLOSED_OPTION:
          return false;
        default:
          throw new RuntimeException("Invalid rc from showConfirmDialog: " + rc);
      }
    }
    return true;
  }
  
  private void _clean() {
    final SwingWorker worker = new SwingWorker() {
      public Object construct() {
        if (showCleanWarning()) {
          try {
            hourglassOn();
            _model.cleanBuildDirectory();
          }
          catch (FileMovedException fme) { _showFileMovedError(fme); }
          catch (IOException ioe) { _showIOError(ioe); }
          finally { hourglassOff(); }
        }
        return null;
      }
    };
    worker.start();
  }

  
  ArrayList<GoToFileListEntry> _completeClassList = new ArrayList<GoToFileListEntry>();
  
  
  private void _scanClassFiles() {
    Thread t = new Thread(new Runnable() {
      public void run() {
        List<File> classFiles = _model.getClassFiles();
        
        HashSet<GoToFileListEntry> hs = new HashSet<GoToFileListEntry>(classFiles.size());
        DummyOpenDefDoc dummyDoc = new DummyOpenDefDoc();
        for(File f: classFiles) {          
          String s = f.toString();
          if (s.lastIndexOf(java.io.File.separatorChar)>=0) {
            s = s.substring(s.lastIndexOf(java.io.File.separatorChar)+1);
          }
          s = s.substring(0, s.lastIndexOf(".class"));
          s = s.replace('$', '.');
          int pos = 0;
          boolean ok = true;
          while((pos=s.indexOf('.', pos)) >= 0) {
            if ((s.length()<=pos+1) || (Character.isDigit(s.charAt(pos+1)))) {
              ok = false;
              break;
            }
            ++pos;
          }
          if (ok) {
            if (s.lastIndexOf('.')>=0) {
              s = s.substring(s.lastIndexOf('.')+1);
            }
            GoToFileListEntry entry = new GoToFileListEntry(dummyDoc, s);
            hs.add(entry);
          }
        }
        _completeClassList = new ArrayList<GoToFileListEntry>(hs);
      }
    });
    t.setPriority(Thread.MIN_PRIORITY);
    t.start();
  }
  
  private void _runProject() {
    if (_model.isProjectActive()) {
      try {
        final File f = _model.getMainClass();
        if (f != null) {
          OpenDefinitionsDocument doc = _model.getDocumentForFile(f);
          doc.runMain();
        }
      }
      catch (ClassNameNotFoundException e) {
        
        String msg =
          "DrJava could not find the top level class name in the\n" +
          "current document, so it could not run the class.  Please\n" +
          "make sure that the class is properly defined first.";
        
        JOptionPane.showMessageDialog(MainFrame.this, msg, "No Class Found", JOptionPane.ERROR_MESSAGE);
      }
      catch (FileMovedException fme) { _showFileMovedError(fme); }
      catch (IOException ioe) { _showIOError(ioe); }
    }
    else _runMain();
  }
  
  
  private void _runMain() {
    
    try { _model.getActiveDocument().runMain(); }
    
    catch (ClassNameNotFoundException e) {
      
      String msg =
        "DrJava could not find the top level class name in the\n" +
        "current document, so it could not run the class.  Please\n" +
        "make sure that the class is properly defined first.";
      
      JOptionPane.showMessageDialog(MainFrame.this, msg, "No Class Found", JOptionPane.ERROR_MESSAGE);
    }
    catch (FileMovedException fme) { _showFileMovedError(fme); }
    catch (IOException ioe) { _showIOError(ioe); }
  }
  
  private void _junit() {
    new Thread("Run JUnit on Current Document") {
      public void run() {
        _disableJUnitActions();
        hourglassOn();  
        try { _model.getActiveDocument().startJUnit(); }
        catch (FileMovedException fme) { _showFileMovedError(fme); }
        catch (IOException ioe) { _showIOError(ioe); }
        catch (ClassNotFoundException cnfe) { _showClassNotFoundError(cnfe); }
        catch (NoClassDefFoundError ncde) { _showNoClassDefError(ncde); }
        catch (ExitingNotAllowedException enae) {
          JOptionPane.showMessageDialog(MainFrame.this,
                                        "An exception occurred while running JUnit, which could\n" +
                                        "not be caught by DrJava.  Details about the exception should\n" +
                                        "have been printed to your console.\n\n",
                                        "Error Running JUnit",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
    }.start();
  }
  
  private void _junitFolder() {
    new Thread("Run JUnit on specified folder") {
      public void run() { 
        INavigatorItem n;
        _disableJUnitActions();
        hourglassOn();  
        if (_model.getDocumentNavigator().isGroupSelected()) {
          Enumeration<OpenDefinitionsDocument> docs = _model.getDocumentNavigator().getDocuments();
          final LinkedList<OpenDefinitionsDocument> l = new LinkedList<OpenDefinitionsDocument>();
          while (docs.hasMoreElements()) {
            OpenDefinitionsDocument doc = docs.nextElement();
            if (_model.getDocumentNavigator().isSelectedInGroup(doc))
              l.add(doc);
          }
          try { _model.getJUnitModel().junitDocs(l); }
          catch(UnexpectedException e) { _junitInterrupted(e); }
        }
      }
    }.start();
  }
  
  private void _junitProject() { _junitAll(); }
  
  private void _junitAll() {
    new Thread("Running Junit Tests") {
      public void run() {
        _disableJUnitActions();
        hourglassOn();  
        try {
          if (_model.isProjectActive()) _model.getJUnitModel().junitProject();
          else _model.getJUnitModel().junitAll();
        } 
        catch(UnexpectedException e) { _junitInterrupted(e); }
      }
    }.start();
  }
  
  
  private DecoratedAction _junit_compileProjectDecoratedAction;
  private DecoratedAction _junit_compileAllDecoratedAction;
  private DecoratedAction _junit_compileFolderDecoratedAction;
  private DecoratedAction _junit_junitFolderDecoratedAction;
  private DecoratedAction _junit_junitAllDecoratedAction;
  private DecoratedAction _junit_junitDecoratedAction;
  private DecoratedAction _junit_junitOpenProjectFilesDecoratedAction;
  private DecoratedAction _junit_cleanDecoratedAction;
  private DecoratedAction _junit_projectPropertiesDecoratedAction;
  private DecoratedAction _junit_runProjectDecoratedAction;
  private DecoratedAction _junit_runDecoratedAction;
  
  
  private class DecoratedAction extends AbstractAction {
    
    AbstractAction _decoree;
    
    boolean _shallowEnabled;
    
    public DecoratedAction(AbstractAction a, boolean b) {
      super((String)a.getValue("Name"));
      _decoree = a;
      _shallowEnabled = _decoree.isEnabled();
      _decoree.setEnabled(b);
    }
    public void actionPerformed(ActionEvent ae) { _decoree.actionPerformed(ae); }
    
    public void setEnabled(boolean b) { _shallowEnabled = b; }
    
    public AbstractAction getUpdatedDecoree() { _decoree.setEnabled(_shallowEnabled); return _decoree; }
  }
  
  
  private void _disableJUnitActions() {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    _compileProjectAction = _junit_compileProjectDecoratedAction = new DecoratedAction(_compileProjectAction, false);
    _compileAllAction = _junit_compileAllDecoratedAction = new DecoratedAction(_compileAllAction, false);
    _compileFolderAction = _junit_compileFolderDecoratedAction = new DecoratedAction(_compileFolderAction, false);
    _junitFolderAction = _junit_junitFolderDecoratedAction = new DecoratedAction(_junitFolderAction, false);
    _junitAllAction = _junit_junitAllDecoratedAction = new DecoratedAction(_junitAllAction, false);
    _junitAction = _junit_junitDecoratedAction = new DecoratedAction(_junitAction, false);
    _junitOpenProjectFilesAction = _junit_junitOpenProjectFilesDecoratedAction = 
      new DecoratedAction(_junitOpenProjectFilesAction, false);
    _cleanAction = _junit_cleanDecoratedAction = new DecoratedAction(_cleanAction, false);
    _projectPropertiesAction = _junit_projectPropertiesDecoratedAction = new DecoratedAction(_projectPropertiesAction, false);
    _runProjectAction = _junit_runProjectDecoratedAction = new DecoratedAction(_runProjectAction, false);
    _runAction = _junit_runDecoratedAction = new DecoratedAction(_runAction, false);
  }
  private void _restoreJUnitActionsEnabled() {












    
    _compileProjectAction = _junit_compileProjectDecoratedAction.getUpdatedDecoree();
    _compileAllAction = _junit_compileAllDecoratedAction.getUpdatedDecoree();
    _compileFolderAction = _junit_compileFolderDecoratedAction.getUpdatedDecoree();
    _junitFolderAction = _junit_junitFolderDecoratedAction.getUpdatedDecoree();
    _junitAllAction = _junit_junitAllDecoratedAction.getUpdatedDecoree();
    _junitAction = _junit_junitDecoratedAction.getUpdatedDecoree();
    _junitOpenProjectFilesAction = _junit_junitOpenProjectFilesDecoratedAction.getUpdatedDecoree();
    _cleanAction = _junit_cleanDecoratedAction.getUpdatedDecoree();
    _projectPropertiesAction = _junit_projectPropertiesDecoratedAction.getUpdatedDecoree();
    _runProjectAction = _junit_runProjectDecoratedAction.getUpdatedDecoree();
    _runAction = _junit_runDecoratedAction.getUpdatedDecoree();
  }
  







  
  
  void debuggerResume() throws DebugException {
    if (inDebugMode()) {
      _model.getDebugger().resume();
      _removeThreadLocationHighlight();
    }
  }
  
  
  void debuggerStep(int flag) {
    if (inDebugMode()) {
      try { _model.getDebugger().step(flag); }
      catch (IllegalStateException ise) {
        
        
        
        
        
        
      }
      catch (DebugException de) {
        _showError(de, "Debugger Error",
                   "Could not create a step request.");
      }
    }
  }
  
  
  void debuggerToggleBreakpoint() {
      OpenDefinitionsDocument doc = _model.getActiveDocument();
      
      boolean isUntitled = doc.isUntitled();
      if (isUntitled) {
        JOptionPane.showMessageDialog(this,
                                      "You must save and compile this document before you can\n" +
                                      "set a breakpoint in it.",
                                      "Must Save and Compile",
                                      JOptionPane.ERROR_MESSAGE);
        return;
      }
      
      boolean isModified = doc.isModifiedSinceSave();
      if (isModified  && !_currentDefPane.hasWarnedAboutModified() &&
          DrJava.getConfig().getSetting(WARN_BREAKPOINT_OUT_OF_SYNC).booleanValue()) {
        String message =
          "This document has been modified and may be out of sync\n" +
          "with the debugger.  It is recommended that you first\n" +
          "save and recompile before continuing to use the debugger,\n" +
          "to avoid any unexpected errors.  Would you still like to\n" +
          "toggle the breakpoint on the specified line?";
        String title = "Toggle breakpoint on modified file?";
        
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(this, title, message);
        int rc = dialog.show();
        switch (rc) {
          case JOptionPane.YES_OPTION:
            _currentDefPane.hasWarnedAboutModified(true);
            if (dialog.getCheckBoxValue()) {
              DrJava.getConfig().setSetting(WARN_BREAKPOINT_OUT_OF_SYNC, Boolean.FALSE);
            }
            break;
            
          case JOptionPane.NO_OPTION:
            if (dialog.getCheckBoxValue()) {
                DrJava.getConfig().setSetting(WARN_BREAKPOINT_OUT_OF_SYNC, Boolean.FALSE);
            }
            break;
            
          case JOptionPane.CANCEL_OPTION:
          case JOptionPane.CLOSED_OPTION:
            
            return;
            
          default:
            throw new RuntimeException("Invalid rc from showConfirmDialog: " + rc);
        }
        
      }
      
      try {
        Debugger debugger = _model.getDebugger();
        debugger.toggleBreakpoint(doc, _currentDefPane.getCaretPosition(), _currentDefPane.getCurrentLine(), true);
      }
      catch (DebugException de) {
        _showError(de, "Debugger Error", "Could not set a breakpoint at the current line.");
      }
    }
  
  

  






















  


  
  
  void debuggerClearAllBreakpoints() {
    try { _model.getDebugger().removeAllBreakpoints(); }
    catch (DebugException de) {
      _showError(de, "Debugger Error", "Could not remove all breakpoints.");
    }
  }
  
  void _showFileMovedError(FileMovedException fme) {
    try {
      File f = fme.getFile();
      OpenDefinitionsDocument doc = _model.getDocumentForFile(f);
      if (doc != null && _saveSelector.shouldSaveAfterFileMoved(doc, f)) _saveAs();
    }
    catch (IOException ioe) {  }
  }
  
  void _showProjectFileParseError(MalformedProjectFileException mpfe) {
    _showError(mpfe, "Invalid Project File", "DrJava could not read the given project file.");
  }
  
  void _showFileNotFoundError(FileNotFoundException fnf) {
    _showError(fnf, "File Not Found", "The specified file was not found on disk.");
  }
  
  void _showIOError(IOException ioe) {
    _showError(ioe, "Input/output error", "An I/O exception occurred during the last operation.");
  }
  
  void _showClassNotFoundError(ClassNotFoundException cnfe) {
    _showError(cnfe, "Class Not Found",
               "A ClassNotFound exception occurred during the last operation.\n" +
               "Please check that your classpath includes all relevant directories.\n\n");
  }
  
  void _showNoClassDefError(NoClassDefFoundError ncde) {
    _showError(ncde, "No Class Def",
               "A NoClassDefFoundError occurred during the last operation.\n" +
               "Please check that your classpath includes all relevant paths.\n\n");
  }
  
  void _showDebugError(DebugException de) {
    _showError(de, "Debug Error", "A Debugger error occurred in the last operation.\n\n");
  }
  
  void _showJUnitInterrupted(UnexpectedException e) {
    _showWarning(e.getCause(), "JUnit Testing Interrupted", 
                 "The slave JVM has thrown a RemoteException probably indicating that it has been reset.\n\n");
  }
  
  private void _showError(Throwable e, String title, String message) {
    JOptionPane.showMessageDialog(this, message + "\n" + e, title, JOptionPane.ERROR_MESSAGE);
  }
  
  private void _showWarning(Throwable e, String title, String message) {
    JOptionPane.showMessageDialog(this, message + "\n" + e, title, JOptionPane.WARNING_MESSAGE);
  }
  
  
  private void _showConfigException() {
    if (DrJava.getConfig().hadStartupException()) {
      Exception e = DrJava.getConfig().getStartupException();
      _showError(e, "Error in Config File",
                 "Could not read the '.drjava' configuration file\n" +
                 "in your home directory.  Starting with default\n" +
                 "values instead.\n\n" + "The problem was:\n");
    }
  }
  
  
  private void _showDebuggingModifiedFileWarning() {
    if (DrJava.getConfig().getSetting(WARN_DEBUG_MODIFIED_FILE).booleanValue()) {
      String msg =
        "This document has been modified since its last save and\n" +
        "may be out of sync with the debugger. It is suggested that\n" +
        "you save and recompile before continuing to debug in order\n" +
        "to avoid any unexpected errors.";
      String title = "Debugging modified file!";
      
      ConfirmCheckBoxDialog dialog =
        new ConfirmCheckBoxDialog(MainFrame.this, title, msg,
                                  "Do not show this message again",
                                  JOptionPane.WARNING_MESSAGE,
                                  JOptionPane.DEFAULT_OPTION);
      if (dialog.show() == JOptionPane.OK_OPTION && dialog.getCheckBoxValue())
        DrJava.getConfig().setSetting(WARN_DEBUG_MODIFIED_FILE, Boolean.FALSE);
      
      _currentDefPane.hasWarnedAboutModified(true);
    }
  }
  
  
  private File getChosenFile(JFileChooser fc, int choice) throws OperationCanceledException {
    switch (choice) {
      case JFileChooser.CANCEL_OPTION:
      case JFileChooser.ERROR_OPTION:
        throw new OperationCanceledException();
      case JFileChooser.APPROVE_OPTION:
        File chosen = fc.getSelectedFile();
        if (chosen != null) {
          
          if (fc.getFileFilter() instanceof JavaSourceFilter) {
            if (chosen.getName().indexOf(".") == -1)
              return new File(chosen.getAbsolutePath() + 
                              DrJavaRoot.LANGUAGE_LEVEL_EXTENSIONS[DrJava.getConfig().getSetting(LANGUAGE_LEVEL)]);
          }
          return chosen;
        }
        else
          throw new RuntimeException("Filechooser returned null file");
      default:                  
        throw  new RuntimeException("Filechooser returned bad rc " + choice);
    }
  }
  
  private File[] getChosenFiles(JFileChooser fc, int choice) throws OperationCanceledException {
    switch (choice) {
      case JFileChooser.CANCEL_OPTION:case JFileChooser.ERROR_OPTION:
        throw new OperationCanceledException();
      case JFileChooser.APPROVE_OPTION:
        File[] chosen = fc.getSelectedFiles();
        if (chosen == null)
          throw new UnexpectedException(new OperationCanceledException(), "filechooser returned null file");
        
        
        
        
        if (chosen.length == 0) {
          if (!fc.isMultiSelectionEnabled()) {
            return new File[] { fc.getSelectedFile() };
          }
          else {
            
            throw new OperationCanceledException();
          }
        }
        else {
          return chosen;
        }
        
      default:                  
        throw new UnexpectedException(new OperationCanceledException(), "filechooser returned bad rc " + choice);
    }
  }
  
  private void _selectAll() {
    _currentDefPane.selectAll();
  }
  
  
  private int _gotoLine() {
    final String msg = "What line would you like to go to?";
    final String title = "Go to Line";
    String lineStr = JOptionPane.showInputDialog(this, msg, title, JOptionPane.QUESTION_MESSAGE);
    try {
      if (lineStr != null) {
        int lineNum = Integer.parseInt(lineStr);
        _currentDefPane.centerViewOnLine(lineNum);
        int pos = _model.getActiveDocument().gotoLine(lineNum);
        _currentDefPane.setCaretPosition(pos);
        return pos;
      }
    }
    catch (NumberFormatException nfe) {
      
      Toolkit.getDefaultToolkit().beep();
      
    }
    
    return -1;
  }
  
  
  private void _removeErrorListener(OpenDefinitionsDocument doc) {
    JScrollPane scroll = _defScrollPanes.get(doc);
    if (scroll != null) {
      DefinitionsPane pane = (DefinitionsPane) scroll.getViewport().getView();
      pane.removeCaretListener(pane.getErrorCaretListener());
    }
  }
  
  
  private void _setUpActions() {
    _setUpAction(_newAction, "New", "Create a new document");
    _setUpAction(_newJUnitTestAction, "New", "Create a new JUnit test case class");
    _setUpAction(_newProjectAction, "New", "Make a new project");
    _setUpAction(_openAction, "Open", "Open an existing file");
    _setUpAction(_openFolderAction, "Open Folder", "OpenAll", "Open all files within a directory");
    _setUpAction(_openFileOrProjectAction, "Open", "Open an existing file or project");
    _setUpAction(_openProjectAction, "Open", "Open an existing project");
    _setUpAction(_saveAction, "Save", "Save the current document");
    _setUpAction(_saveAsAction, "Save As", "SaveAs", "Save the current document with a new name");
    _setUpAction(_saveProjectAction, "Save", "Save", "Save the current project");
    _saveProjectAction.setEnabled(false);
    _setUpAction(_saveProjectAsAction, "Save As", "SaveAs", "Save current project to new project file");
    _saveProjectAsAction.setEnabled(false);
    _setUpAction(_revertAction, "Revert", "Revert the current document to the saved version");
    


    
    _setUpAction(_closeAction, "Close", "Close the current document");
    _setUpAction(_closeAllAction, "Close All", "CloseAll", "Close all documents");
    _setUpAction(_closeProjectAction, "Close", "CloseAll", "Close the current project");
    _closeProjectAction.setEnabled(false);
    
    _setUpAction(_projectPropertiesAction, "Project Properties", "Preferences", "Edit Project Properties");
    _projectPropertiesAction.setEnabled(false);    
    


    _setUpAction(_junitOpenProjectFilesAction, "Test", "Test Project");
    _junitOpenProjectFilesAction.setEnabled(false);
    

    _setUpAction(_compileProjectAction, "Compile", "Compile", "Compile the current project");

    _compileProjectAction.setEnabled(false);
    
    _setUpAction(_runProjectAction, "Run", "Run the project's main method");
    _runProjectAction.setEnabled(false);
    
    _setUpAction(_jarProjectAction, "Jar", "Create a jar archive from this project");
    _jarProjectAction.setEnabled(false);
    
    _setUpAction(_saveAllAction, "Save All", "SaveAll", "Save all open documents");
    
    _setUpAction(_cleanAction, "Clean", "Clean Build directory");
    _cleanAction.setEnabled(false);
    _setUpAction(_compileAction, "Compile", "Compile the current document");
    _setUpAction(_compileAllAction, "Compile All", "CompileAll", "Compile all open documents");
    _setUpAction(_printDefDocAction, "Print", "Print the current main document");
    _setUpAction(_printConsoleAction, "Print", "Print the Console pane");
    _setUpAction(_printInteractionsAction, "Print", "Print the Interactions pane");
    _setUpAction(_pageSetupAction, "Page Setup", "PageSetup", "Change the printer settings");
    _setUpAction(_printDefDocPreviewAction, "Print Preview", "PrintPreview", "Preview how the document will be printed");
    _setUpAction(_printConsolePreviewAction, "Print Preview", "PrintPreview", 
                 "Preview how the console document will be printed");
    _setUpAction(_printInteractionsPreviewAction, "Print Preview", "PrintPreview", 
                 "Preview how the interactions document will be printed");    
    
    _setUpAction(_quitAction, "Quit", "Quit", "Quit DrJava");
    
    _setUpAction(_undoAction, "Undo", "Undo previous command");
    _setUpAction(_redoAction, "Redo", "Redo last undo");
    _undoAction.putValue(Action.NAME, "Undo Previous Command");
    _redoAction.putValue(Action.NAME, "Redo Last Undo");
    
    _setUpAction(cutAction, "Cut", "Cut selected text to the clipboard");
    _setUpAction(copyAction, "Copy", "Copy selected text to the clipboard");
    _setUpAction(pasteAction, "Paste", "Paste text from the clipboard");
    _setUpAction(_selectAllAction, "Select All", "Select all text");
    
    cutAction.putValue(Action.NAME, "Cut");
    copyAction.putValue(Action.NAME, "Copy");
    pasteAction.putValue(Action.NAME, "Paste");
    
    _setUpAction(_indentLinesAction, "Indent Lines", "Indent all selected lines");
    _setUpAction(_commentLinesAction, "Comment Lines", "Comment out all selected lines");
    _setUpAction(_uncommentLinesAction, "Uncomment Lines", "Uncomment all selected lines");
    
    _setUpAction(_findReplaceAction, "Find", "Find or replace text in the document");
    _setUpAction(_findNextAction, "Find Next", "Repeats the last find");
    _setUpAction(_findPrevAction, "Find Previous", "Repeats the last find in the opposite direction");
    _setUpAction(_gotoLineAction, "Go to line", "Go to a line number in the document");
    
    _setUpAction(_switchToPrevAction, "Back", "Switch to the previous document");
    _setUpAction(_switchToNextAction, "Forward", "Switch to the next document");
    _setUpAction(_switchToPreviousPaneAction, "Previous Pane", "Switch focus to the previous pane");
    _setUpAction(_switchToNextPaneAction, "Next Pane", "Switch focus to the next pane");
    
    _setUpAction(_editPreferencesAction, "Preferences", "Edit configurable settings in DrJava");
    
    _setUpAction(_junitAction, "Test Current", "Run JUnit over the current document");
    _setUpAction(_junitAllAction, "Test", "Run JUnit over all open JUnit tests");
    _setUpAction(_javadocAllAction, "Javadoc", "Create and save Javadoc for the packages of all open documents");
    _setUpAction(_javadocCurrentAction, "Preview Javadoc Current", "Preview the Javadoc for the current document");
    _setUpAction(_runAction, "Run", "Run the main method of the current document");
    
    _setUpAction(_executeHistoryAction, "Execute History", "Load and execute a history of interactions from a file");
    _setUpAction(_loadHistoryScriptAction, "Load History as Script", 
                 "Load a history from a file as a series of interactions");
    _setUpAction(_saveHistoryAction, "Save History", "Save the history of interactions to a file");
    _setUpAction(_clearHistoryAction, "Clear History", "Clear the current history of interactions");
    
    
    _setUpAction(_resetInteractionsAction, "Reset", "Reset the Interactions Pane");
    _resetInteractionsAction.setEnabled(true);
    
    _setUpAction(_viewInteractionsClassPathAction, "View Interactions Classpath", 
                 "Display the classpath in use by the Interactions Pane");
    _setUpAction(_copyInteractionToDefinitionsAction, "Lift Current Interaction", 
                 "Copy the current interaction into the Definitions Pane");
    
    _setUpAction(_clearConsoleAction, "Clear Console", "Clear all text in the Console Pane");
    _setUpAction(_showDebugConsoleAction, "Show DrJava Debug Console", "<html>Show a console for debugging DrJava<br>" +
                 "(with \"mainFrame\", \"model\", and \"config\" variables defined)</html>");
    
    if (_model.getDebugger().isAvailable()) {
      _setUpAction(_toggleDebuggerAction, "Debug Mode", "Enable or disable DrJava's debugger");
      _setUpAction(_toggleBreakpointAction, "Toggle Breakpoint", "Set or clear a breakpoint on the current line");
      _setUpAction(_clearAllBreakpointsAction, "Clear Breakpoints", "Clear all breakpoints in all classes");
      _setUpAction(_resumeDebugAction, "Resume", "Resume the current suspended thread");
      _setUpAction(_stepIntoDebugAction, "Step Into", "Step into the current line or method call");
      _setUpAction(_stepOverDebugAction, "Step Over", "Step over the current line or method call");
      _setUpAction(_stepOutDebugAction, "Step Out", "Step out of the current method");
    }
    
    _setUpAction(_helpAction, "Help", "Show documentation on how to use DrJava");
    _setUpAction(_quickStartAction, "Help", "View Quick Start Guide for DrJava");
    _setUpAction(_aboutAction, "About", "About DrJava");
    _setUpAction(_errorsAction, "DrJava Errors", "drjavaerror", "Show a window with internal DrJava errors");
  }
  
  private void _setUpAction(Action a, String name, String icon, String shortDesc) {
    a.putValue(Action.SMALL_ICON, _getIcon(icon + "16.gif"));
    a.putValue(Action.DEFAULT, name);
    a.putValue(Action.SHORT_DESCRIPTION, shortDesc);
  }
  
  private void _setUpAction(Action a, String icon, String shortDesc) {
    _setUpAction(a, icon, icon, shortDesc);
  }
  
  
  
  private ImageIcon _getIcon(String name) { return getIcon(name); }
  
  public static ImageIcon getIcon(String name) {
    URL url = MainFrame.class.getResource(ICON_PATH + name);
    if (url != null)  return new ImageIcon(url);
    
    return null;
  }
  
  
  
  private class MenuBar extends JMenuBar {
    public boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
      if (MainFrame.this.getAllowKeyEvents()) return super.processKeyBinding(ks, e, condition, pressed);
      return false;
    }
  }
  
  
  private void _setUpMenuBar() {
    boolean showDebugger = (_model.getDebugger().isAvailable());
    
    
    int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    
    _menuBar = new MenuBar();
    _fileMenu = _setUpFileMenu(mask);
    _editMenu = _setUpEditMenu(mask);
    _toolsMenu = _setUpToolsMenu(mask);
    _projectMenu = _setUpProjectMenu(mask);
    if (showDebugger) _debugMenu = _setUpDebugMenu(mask);
    _languageLevelMenu = _setUpLanguageLevelMenu(mask);
    _helpMenu = _setUpHelpMenu(mask);
    
    _menuBar.add(_fileMenu);
    _menuBar.add(_editMenu);
    _menuBar.add(_toolsMenu);
    _menuBar.add(_projectMenu);
    if (showDebugger) _menuBar.add(_debugMenu);
    _menuBar.add(_languageLevelMenu);
    _menuBar.add(_helpMenu);
    setJMenuBar(_menuBar);
  }
  
  
  private void _addMenuItem(JMenu menu, Action a, Option<KeyStroke> opt) {
    JMenuItem item;
    item = menu.add(a);
    _setMenuShortcut(item, a, opt);
  }
  
  
  private void _setMenuShortcut(JMenuItem item, Action a, Option<KeyStroke> opt) {
    KeyStroke ks = DrJava.getConfig().getSetting(opt);
    
    
    
    
    
    KeyBindingManager.Singleton.put(opt, a, item, item.getText());
    if ((ks != KeyStrokeOption.NULL_KEYSTROKE) &&
        (KeyBindingManager.Singleton.get(ks) == a)) {
      item.setAccelerator(ks);
      
    }
  }
  
  
  private JMenu _setUpFileMenu(int mask) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    
    _addMenuItem(fileMenu, _newAction, KEY_NEW_FILE);
    _addMenuItem(fileMenu, _newJUnitTestAction, KEY_NEW_TEST);
    _addMenuItem(fileMenu, _openAction, KEY_OPEN_FILE);
    _addMenuItem(fileMenu, _openFolderAction, KEY_OPEN_FOLDER);
    
    
    fileMenu.addSeparator();
    
    _addMenuItem(fileMenu, _saveAction, KEY_SAVE_FILE);
    _saveAction.setEnabled(true);
    _addMenuItem(fileMenu, _saveAsAction, KEY_SAVE_FILE_AS);
    _addMenuItem(fileMenu, _saveAllAction, KEY_SAVE_ALL_FILES);

    
    _addMenuItem(fileMenu, _revertAction, KEY_REVERT_FILE);
    _revertAction.setEnabled(false);
    
    
    
    fileMenu.addSeparator();
    _addMenuItem(fileMenu, _closeAction, KEY_CLOSE_FILE);
    _addMenuItem(fileMenu, _closeAllAction, KEY_CLOSE_ALL_FILES);
    
    
    
    fileMenu.addSeparator();
    _addMenuItem(fileMenu, _pageSetupAction, KEY_PAGE_SETUP);
    _addMenuItem(fileMenu, _printDefDocPreviewAction, KEY_PRINT_PREVIEW);
    _addMenuItem(fileMenu, _printDefDocAction, KEY_PRINT);
    
    
    fileMenu.addSeparator();
    _addMenuItem(fileMenu, _quitAction, KEY_QUIT);
    
    return fileMenu;
  }
  
  
  private JMenu _setUpEditMenu(int mask) {
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic(KeyEvent.VK_E);
    
    _addMenuItem(editMenu, _undoAction, KEY_UNDO);
    _addMenuItem(editMenu, _redoAction, KEY_REDO);
    
    
    editMenu.addSeparator();
    _addMenuItem(editMenu, cutAction, KEY_CUT);
    _addMenuItem(editMenu, copyAction, KEY_COPY);
    _addMenuItem(editMenu, pasteAction, KEY_PASTE);
    _addMenuItem(editMenu, _selectAllAction, KEY_SELECT_ALL);
    
    
    editMenu.addSeparator();
    
    JMenuItem editItem = editMenu.add(_indentLinesAction);
    editItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
    _addMenuItem(editMenu, _commentLinesAction, KEY_COMMENT_LINES);
    _addMenuItem(editMenu, _uncommentLinesAction, KEY_UNCOMMENT_LINES);
    _addMenuItem(editMenu, completeFileUnderCursorAction, KEY_COMPLETE_FILE);
    
    
    editMenu.addSeparator();
    _addMenuItem(editMenu, _findReplaceAction, KEY_FIND_REPLACE);
    _addMenuItem(editMenu, _findNextAction, KEY_FIND_NEXT);
    _addMenuItem(editMenu, _findPrevAction, KEY_FIND_PREV);
    _addMenuItem(editMenu, _gotoLineAction, KEY_GOTO_LINE);
    _addMenuItem(editMenu, _gotoFileAction, KEY_GOTO_FILE);
    _addMenuItem(editMenu, gotoFileUnderCursorAction, KEY_GOTO_FILE_UNDER_CURSOR);
    
    
    editMenu.addSeparator();
    _addMenuItem(editMenu, _switchToPrevAction, KEY_PREVIOUS_DOCUMENT);
    _addMenuItem(editMenu, _switchToNextAction, KEY_NEXT_DOCUMENT);
    _addMenuItem(editMenu, _switchToPreviousPaneAction, KEY_PREVIOUS_PANE);
    _addMenuItem(editMenu, _switchToNextPaneAction, KEY_NEXT_PANE);
    _addMenuItem(editMenu, _gotoOpeningBraceAction, KEY_OPENING_BRACE);
    _addMenuItem(editMenu, _gotoClosingBraceAction, KEY_CLOSING_BRACE);
    
    
    editMenu.addSeparator();
    _addMenuItem(editMenu, _editPreferencesAction, KEY_PREFERENCES);
    
    
    return editMenu;
  }
  
  
  private JMenu _setUpToolsMenu(int mask) {
    JMenu toolsMenu = new JMenu("Tools");
    toolsMenu.setMnemonic(KeyEvent.VK_T);
    
    
    _addMenuItem(toolsMenu, _compileAllAction, KEY_COMPILE_ALL);
    _addMenuItem(toolsMenu, _compileAction, KEY_COMPILE);
    _addMenuItem(toolsMenu, _junitAllAction, KEY_TEST_ALL);
    _addMenuItem(toolsMenu, _junitAction, KEY_TEST);
    _addMenuItem(toolsMenu, _javadocAllAction, KEY_JAVADOC_ALL);
    _addMenuItem(toolsMenu, _javadocCurrentAction, KEY_JAVADOC_CURRENT);
    toolsMenu.addSeparator();
    
    
    _addMenuItem(toolsMenu, _runAction, KEY_RUN);
    toolsMenu.addSeparator();
    
    _addMenuItem(toolsMenu, _executeHistoryAction, KEY_EXECUTE_HISTORY);
    _addMenuItem(toolsMenu, _loadHistoryScriptAction, KEY_LOAD_HISTORY_SCRIPT);
    _addMenuItem(toolsMenu, _saveHistoryAction, KEY_SAVE_HISTORY);
    _addMenuItem(toolsMenu, _clearHistoryAction, KEY_CLEAR_HISTORY);
    toolsMenu.addSeparator();
    
    
    
    _addMenuItem(toolsMenu, _resetInteractionsAction, KEY_RESET_INTERACTIONS);
    _addMenuItem(toolsMenu, _viewInteractionsClassPathAction, KEY_VIEW_INTERACTIONS_CLASSPATH);
    _addMenuItem(toolsMenu, _copyInteractionToDefinitionsAction, KEY_LIFT_CURRENT_INTERACTION);
    _addMenuItem(toolsMenu, _printInteractionsAction, KEY_PRINT_INTERACTIONS);
    toolsMenu.addSeparator();
    
    _addMenuItem(toolsMenu, _clearConsoleAction, KEY_CLEAR_CONSOLE);
    _addMenuItem(toolsMenu, _printConsoleAction, KEY_PRINT_CONSOLE);
    if (DrJava.getConfig().getSetting(SHOW_DEBUG_CONSOLE).booleanValue()) {
      toolsMenu.add(_showDebugConsoleAction);
    }
    
    
    return toolsMenu;
  }
  
  
  private JMenu _setUpProjectMenu(int mask) {
    JMenu projectMenu = new JMenu("Project");
    projectMenu.setMnemonic(KeyEvent.VK_P);
    
    projectMenu.add(_newProjectAction);
    _addMenuItem(projectMenu, _openProjectAction, KEY_OPEN_PROJECT);
    
    
    projectMenu.add(_saveProjectAction);
    
    projectMenu.add(_saveProjectAsAction);
    
    
    _addMenuItem(projectMenu, _closeProjectAction, KEY_CLOSE_PROJECT);
    
    projectMenu.addSeparator();
    
    projectMenu.add(_cleanAction);

    projectMenu.add(_compileProjectAction);
    projectMenu.add(_jarProjectAction);
    _addMenuItem(projectMenu, _runProjectAction, KEY_RUN_MAIN);
    projectMenu.add(_junitOpenProjectFilesAction);

    
    projectMenu.addSeparator();
    
    projectMenu.add(_projectPropertiesAction);
    
    return projectMenu;
  }
  
  
  private JMenu _setUpDebugMenu(int mask) {
    JMenu debugMenu = new JMenu("Debugger");
    debugMenu.setMnemonic(KeyEvent.VK_D);
    
    _debuggerEnabledMenuItem = _newCheckBoxMenuItem(_toggleDebuggerAction);
    _debuggerEnabledMenuItem.setSelected(false);
    _setMenuShortcut(_debuggerEnabledMenuItem, _toggleDebuggerAction, KEY_DEBUG_MODE_TOGGLE);
    debugMenu.add(_debuggerEnabledMenuItem);
    debugMenu.addSeparator();
    
    _addMenuItem(debugMenu, _toggleBreakpointAction, KEY_DEBUG_BREAKPOINT_TOGGLE);
    
    
    _addMenuItem(debugMenu, _clearAllBreakpointsAction, KEY_DEBUG_CLEAR_ALL_BREAKPOINTS);
    _addMenuItem(debugMenu, _breakpointsPanelAction, KEY_DEBUG_BREAKPOINT_PANEL);
    debugMenu.addSeparator();
    
    
    _addMenuItem(debugMenu, _resumeDebugAction, KEY_DEBUG_RESUME);
    _addMenuItem(debugMenu, _stepIntoDebugAction, KEY_DEBUG_STEP_INTO);
    _addMenuItem(debugMenu, _stepOverDebugAction, KEY_DEBUG_STEP_OVER);
    _addMenuItem(debugMenu, _stepOutDebugAction, KEY_DEBUG_STEP_OUT);
    
    
    _setDebugMenuItemsEnabled(false);
    
    
    return debugMenu;
  }
  
  
  private void _setDebugMenuItemsEnabled(boolean isEnabled) {
    
    _debuggerEnabledMenuItem.setSelected(isEnabled);
    
    _resumeDebugAction.setEnabled(false);
    _stepIntoDebugAction.setEnabled(false);
    _stepOverDebugAction.setEnabled(false);
    _stepOutDebugAction.setEnabled(false);
    
    if (_debugPanel != null) _debugPanel.disableButtons();
  }
  
  
  private void _setThreadDependentDebugMenuItems(boolean isSuspended) {
    
    _resumeDebugAction.setEnabled(isSuspended);
    _stepIntoDebugAction.setEnabled(isSuspended);
    _stepOverDebugAction.setEnabled(isSuspended);
    _stepOutDebugAction.setEnabled(isSuspended);
    _debugPanel.setThreadDependentButtons(isSuspended);
  }
  
  
  private JMenu _setUpLanguageLevelMenu(int mask) {
    JMenu languageLevelMenu = new JMenu("Language Level");
    languageLevelMenu.setMnemonic(KeyEvent.VK_L);
    ButtonGroup group = new ButtonGroup();
    
    final Configuration config = DrJava.getConfig();
    int currentLanguageLevel = config.getSetting(LANGUAGE_LEVEL);
    JRadioButtonMenuItem rbMenuItem;
    rbMenuItem = new JRadioButtonMenuItem("Full Java");
    rbMenuItem.setToolTipText("Use full Java syntax");
    if (currentLanguageLevel == DrJavaRoot.FULL_JAVA) { rbMenuItem.setSelected(true); }
    rbMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        config.setSetting(LANGUAGE_LEVEL, DrJavaRoot.FULL_JAVA);
      }});
      group.add(rbMenuItem);
      languageLevelMenu.add(rbMenuItem);
      languageLevelMenu.addSeparator();
      
      rbMenuItem = new JRadioButtonMenuItem("Elementary");
      rbMenuItem.setToolTipText("Use Elementary language-level features");
      if (currentLanguageLevel == DrJavaRoot.ELEMENTARY_LEVEL) { rbMenuItem.setSelected(true); }
      rbMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          config.setSetting(LANGUAGE_LEVEL, DrJavaRoot.ELEMENTARY_LEVEL);
        }});
        group.add(rbMenuItem);
        languageLevelMenu.add(rbMenuItem);
        
        rbMenuItem = new JRadioButtonMenuItem("Intermediate");
        rbMenuItem.setToolTipText("Use Intermediate language-level features");
        if (currentLanguageLevel == DrJavaRoot.INTERMEDIATE_LEVEL) { rbMenuItem.setSelected(true); }
        rbMenuItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            config.setSetting(LANGUAGE_LEVEL, DrJavaRoot.INTERMEDIATE_LEVEL);
          }});
          group.add(rbMenuItem);
          languageLevelMenu.add(rbMenuItem);
          
          rbMenuItem = new JRadioButtonMenuItem("Advanced");
          rbMenuItem.setToolTipText("Use Advanced language-level features");
          if (currentLanguageLevel == DrJavaRoot.ADVANCED_LEVEL) { rbMenuItem.setSelected(true); }
          rbMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              config.setSetting(LANGUAGE_LEVEL, DrJavaRoot.ADVANCED_LEVEL);
            }});
            group.add(rbMenuItem);
            languageLevelMenu.add(rbMenuItem);
            return languageLevelMenu;
  }
  
  
  private JMenu _setUpHelpMenu(int mask) {
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic(KeyEvent.VK_H);
    _addMenuItem(helpMenu, _helpAction, KEY_HELP);
    _addMenuItem(helpMenu, _quickStartAction, KEY_QUICKSTART);
    _addMenuItem(helpMenu, _aboutAction, KEY_ABOUT);
    _addMenuItem(helpMenu, _errorsAction, KEY_DRJAVA_ERRORS);
    return helpMenu;
  }
  
  
  JButton _createManualToolbarButton(Action a) {
    final JButton ret;
    
    Font buttonFont = DrJava.getConfig().getSetting(FONT_TOOLBAR);
    
    
    boolean useIcon = DrJava.getConfig().getSetting(TOOLBAR_ICONS_ENABLED).booleanValue();
    boolean useText = DrJava.getConfig().getSetting(TOOLBAR_TEXT_ENABLED).booleanValue();
    final Icon icon = (useIcon) ? (Icon) a.getValue(Action.SMALL_ICON) : null;
    if (icon == null) {
      ret = new UnfocusableButton((String) a.getValue(Action.DEFAULT));
    }
    else {
      ret = new UnfocusableButton(icon);
      if (useText) {
        ret.setText((String) a.getValue(Action.DEFAULT));
      }
    }
    ret.setEnabled(false);
    ret.addActionListener(a);
    ret.setToolTipText( (String) a.getValue(Action.SHORT_DESCRIPTION));
    ret.setFont(buttonFont);
    Boolean test = a instanceof DelegatingAction;
    a.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        if ("enabled".equals(evt.getPropertyName())) {
          Boolean val = (Boolean) evt.getNewValue();
          ret.setEnabled(val.booleanValue());
        }
      }
    });
    
    return ret;
  }
  
  
  public JButton _createToolbarButton(Action a) {
    boolean useText = DrJava.getConfig().getSetting(TOOLBAR_TEXT_ENABLED).booleanValue();
    boolean useIcons = DrJava.getConfig().getSetting(TOOLBAR_ICONS_ENABLED).booleanValue();
    Font buttonFont = DrJava.getConfig().getSetting(FONT_TOOLBAR);
    
    final JButton result = new UnfocusableButton(a);
    result.setText((String) a.getValue(Action.DEFAULT));
    result.setFont(buttonFont);
    if (!useIcons) result.setIcon(null);
    if (!useText && (result.getIcon() != null)) result.setText("");
    return result;
  }
  
  
  private void _setUpToolBar() {
    _toolBar = new JToolBar();
    
    _toolBar.setFloatable(false);
    

    
    
    _toolBar.add(_createToolbarButton(_newAction));
    _toolBar.add(_createToolbarButton(_openFileOrProjectAction));
    _toolBar.add(_createToolbarButton(_saveAction));
    _closeButton = _createToolbarButton(_closeAction);
    _toolBar.add(_closeButton);
    
    
    _toolBar.addSeparator();
    _toolBar.add(_createToolbarButton(cutAction));
    _toolBar.add(_createToolbarButton(copyAction));
    _toolBar.add(_createToolbarButton(pasteAction));
    
    
    
    
    
    
    
    _undoButton = _createManualToolbarButton(_undoAction);
    _toolBar.add(_undoButton);
    _redoButton = _createManualToolbarButton(_redoAction);
    _toolBar.add(_redoButton);
    
    
    _toolBar.addSeparator();
    _toolBar.add(_createToolbarButton(_findReplaceAction));
    
    
    _toolBar.addSeparator();
    _compileButton = _createToolbarButton(_compileAllAction);
    _toolBar.add(_compileButton);
    _toolBar.add(_createToolbarButton(_resetInteractionsAction));
    
    
    
    _toolBar.addSeparator();
    
    _toolBar.add(_createToolbarButton(_runAction));
    _toolBar.add(_createToolbarButton(_junitAllAction));
    _toolBar.add(_createToolbarButton(_javadocAllAction));

    
    _toolBar.addSeparator();
    final JButton errorsButton = _createToolbarButton(_errorsAction);
    errorsButton.setVisible(false);
    errorsButton.setBackground(DrJava.getConfig().getSetting(DRJAVA_ERRORS_BUTTON_COLOR));
    DrJavaErrorHandler.setButton(errorsButton);
    _toolBar.add(errorsButton);
    
    OptionListener<Color> errBtnColorOptionListener = new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oce) {
        errorsButton.setBackground(oce.value);
      }
    };
    DrJava.getConfig().addOptionListener(DRJAVA_ERRORS_BUTTON_COLOR, errBtnColorOptionListener);
    
    
    _fixToolbarHeights();
    
    getContentPane().add(_toolBar, BorderLayout.NORTH);
    _updateToolbarVisible();
  }
  
  
  private void _updateToolbarVisible() {
    _toolBar.setVisible(DrJava.getConfig().getSetting(TOOLBAR_ENABLED));
  }  
  
  
  private void _updateToolbarButtons() {
    _updateToolbarVisible();
    Component[] buttons = _toolBar.getComponents();
    
    Font toolbarFont = DrJava.getConfig().getSetting(FONT_TOOLBAR);
    boolean iconsEnabled = DrJava.getConfig().getSetting(TOOLBAR_ICONS_ENABLED).booleanValue();
    boolean textEnabled = DrJava.getConfig().getSetting(TOOLBAR_TEXT_ENABLED).booleanValue();
    
    for (int i = 0; i< buttons.length; i++) {
      
      if (buttons[i] instanceof JButton) {
        
        JButton b = (JButton) buttons[i];
        Action a = b.getAction();
        
        
        
        
        b.setFont(toolbarFont);
        
        if (a == null) {
          if (b == _undoButton) a = _undoAction;
          else if (b == _redoButton) a = _redoAction;
          else continue;
        }
        
        if (b.getIcon() == null) {
          if (iconsEnabled) b.setIcon( (Icon) a.getValue(Action.SMALL_ICON));
        }
        else if (!iconsEnabled && b.getText().equals(""))  b.setIcon(null);
        
        if (b.getText().equals("")) {
          if (textEnabled) b.setText( (String) a.getValue(Action.DEFAULT));
        }
        else if (!textEnabled && b.getIcon() != null) b.setText("");
        
      }
    }
    
    
    _fixToolbarHeights();
  }
  
  
  private void _fixToolbarHeights() {
    Component[] buttons = _toolBar.getComponents();
    
    
    int max = 0;
    for (int i = 0; i< buttons.length; i++) {
      
      if (buttons[i] instanceof JButton) {
        JButton b = (JButton) buttons[i];
        
        
        b.setPreferredSize(null);
        
        
        Dimension d = b.getPreferredSize();
        int cur = (int) d.getHeight();
        if (cur > max) {
          max = cur;
        }
      }
    }
    
    
    for (int i = 0; i< buttons.length; i++) {
      
      if (buttons[i] instanceof JButton) {
        JButton b = (JButton) buttons[i];
        Dimension d = new Dimension((int) b.getPreferredSize().getWidth(), max);
        
        
        
        b.setPreferredSize(d);
        b.setMaximumSize(d);
      }
    }
    
    
  }
  
  
  private void _setUpStatusBar() {
    
    _fileNameField = new JLabel();
    _fileNameField.setFont(_fileNameField.getFont().deriveFont(Font.PLAIN));
    
    _sbMessage = new JLabel();
    _sbMessage.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JPanel fileNameAndMessagePanel = new JPanel(new BorderLayout());
    fileNameAndMessagePanel.add(_fileNameField, BorderLayout.CENTER);
    fileNameAndMessagePanel.add(_sbMessage, BorderLayout.EAST);
    
    _currLocationField = new JLabel();
    _currLocationField.setFont(_currLocationField.getFont().deriveFont(Font.PLAIN));
    _currLocationField.setHorizontalAlignment(SwingConstants.RIGHT);
    _currLocationField.setPreferredSize(new Dimension(165,12));
    
    
    
    
    _statusBar = new JPanel(new BorderLayout());
    _statusBar.add( fileNameAndMessagePanel, BorderLayout.CENTER );

    _statusBar.add( _currLocationField, BorderLayout.EAST );
    _statusBar.setBorder(
                         new CompoundBorder(new EmptyBorder(2,2,2,2),
                                            new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),
                                                               new EmptyBorder(2,2,2,2))));
    getContentPane().add(_statusBar, BorderLayout.SOUTH);
    
    
  }
  
  
  private class PositionListener implements CaretListener {
    
    public void caretUpdate( CaretEvent ce ) {
      OpenDefinitionsDocument doc = _model.getActiveDocument();
      doc.setCurrentLocation(ce.getDot());  
      updateLocation();
    }
    
    public void updateLocation() {
      DefinitionsPane p = _currentDefPane;
      _currLocationField.setText(p.getCurrentLine() + ":" + p.getCurrentCol() +"\t"); 
      
      
    }
  }
  
  private void _setUpTabs() {
    _compilerErrorPanel = new CompilerErrorPanel(_model, this);
    
    _consoleController = new ConsoleController(_model.getConsoleDocument(), _model.getSwingConsoleDocument());
    _consolePane = _consoleController.getPane();
    
    
    _interactionsController =
      new InteractionsController(_model.getInteractionsModel(), _model.getSwingInteractionsDocument());
    _interactionsController.setPrevPaneAction(_switchToPreviousPaneAction);
    _interactionsController.setNextPaneAction(_switchToNextPaneAction);
    _interactionsPane = _interactionsController.getPane();
    


    
    
    _findReplace = new FindReplaceDialog(this, _model);
    
    _consoleScroll = new BorderlessScrollPane(_consolePane) {
      public boolean requestFocusInWindow() { return _consolePane.requestFocusInWindow(); }
    };
    JScrollPane interactionsScroll = new BorderlessScrollPane(_interactionsPane);
    _interactionsContainer = new JPanel(new BorderLayout()) {
      public boolean requestFocusInWindow() { return _interactionsPane.requestFocusInWindow(); }
    };
    _interactionsContainer.add(interactionsScroll, BorderLayout.CENTER);
    
    _junitErrorPanel = new JUnitPanel(_model, this);
    _javadocErrorPanel = new JavadocErrorPanel(_model, this);
    if (_model.getDebugger().isAvailable()) { _breakpointsPanel = new BreakpointsPanel(this); }
    
    _tabbedPane = new JTabbedPane();
    _tabbedPane.addChangeListener(new ChangeListener () {
      public void stateChanged(ChangeEvent e) {

        clearStatusMessage();
        
        if (_tabbedPane.getSelectedComponent() == _consoleScroll)
          
          SwingUtilities.invokeLater(new Runnable() { public void run() { _consolePane.requestFocusInWindow(); } });
          
        
        if (_currentDefPane != null) {
          int pos = _currentDefPane.getCaretPosition();
          _currentDefPane.removeErrorHighlight(); 
          _currentDefPane.getErrorCaretListener().updateHighlight(pos);
        }
      }
    });
    
    
    
    
    
    
    _tabbedPane.add("Interactions", _interactionsContainer);
    _tabbedPane.add("Console", _consoleScroll);
    
    _tabs = new LinkedList<TabbedPanel>();
    
    _tabs.addLast(_compilerErrorPanel);
    _tabs.addLast(_junitErrorPanel);
    _tabs.addLast(_javadocErrorPanel);
    _tabs.addLast(_findReplace);
    if (_model.getDebugger().isAvailable()) { _tabs.addLast(_breakpointsPanel); }
    
    _interactionsPane.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) { _lastFocusOwner = _interactionsContainer; }
    });
    _consolePane.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) { _lastFocusOwner = _consoleScroll; }
    });
    _compilerErrorPanel.getMainPanel().addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) { _lastFocusOwner = _compilerErrorPanel; }
    });
    _junitErrorPanel.getMainPanel().addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) { _lastFocusOwner = _junitErrorPanel; }
    });
    _javadocErrorPanel.getMainPanel().addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) { _lastFocusOwner = _javadocErrorPanel; }
    });
    _findReplace.getFindField().addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) { _lastFocusOwner = _findReplace; }
    });
    
    
    showTab(_compilerErrorPanel);
    
    _tabbedPane.setSelectedIndex(0);
  }
  
  
  private void _setUpContextMenus() {
    
    _navPaneFolderPopupMenu = new JPopupMenu();
    
    _navPaneFolderPopupMenu.add(_newFileFolderAction);
    _navPaneFolderPopupMenu.add(_openOneFolderAction);
    _navPaneFolderPopupMenu.add(_openAllFolderAction);
    _navPaneFolderPopupMenu.add(_closeFolderAction);
    _navPaneFolderPopupMenu.add(_compileFolderAction);
    _navPaneFolderPopupMenu.add(_junitFolderAction);
    
    _navPanePopupMenuForRoot = new JPopupMenu();
    _navPanePopupMenuForRoot.add(_saveProjectAction);
    _navPanePopupMenuForRoot.add(_closeProjectAction);
    _navPanePopupMenuForRoot.addSeparator();

    _navPanePopupMenuForRoot.add(_compileProjectAction);
    _navPanePopupMenuForRoot.add(_runProjectAction);
    _navPanePopupMenuForRoot.add(_junitOpenProjectFilesAction);

    _navPanePopupMenuForRoot.addSeparator();
    _navPanePopupMenuForRoot.add(_projectPropertiesAction);
    
    _navPanePopupMenuForExternal = new JPopupMenu();
    _navPanePopupMenuForExternal.add(_saveAction);
    _navPanePopupMenuForExternal.add(_saveAsAction);
    _navPanePopupMenuForExternal.add(_revertAction);
    _navPanePopupMenuForExternal.addSeparator();
    _navPanePopupMenuForExternal.add(_closeAction);
    _navPanePopupMenuForExternal.addSeparator();
    _navPanePopupMenuForExternal.add(_printDefDocAction);
    _navPanePopupMenuForExternal.add(_printDefDocPreviewAction);
    _navPanePopupMenuForExternal.addSeparator();
    _navPanePopupMenuForExternal.add(_compileAction);
    _navPanePopupMenuForExternal.add(_junitAction);
    _navPanePopupMenuForExternal.add(_javadocCurrentAction);
    _navPanePopupMenuForExternal.add(_runAction);
    _navPanePopupMenuForExternal.addSeparator();
    _navPanePopupMenuForExternal.add(_moveToAuxiliaryAction);
    
    _navPanePopupMenuForAuxiliary = new JPopupMenu();
    _navPanePopupMenuForAuxiliary.add(_saveAction);
    _navPanePopupMenuForAuxiliary.add(_saveAsAction);
    _navPanePopupMenuForAuxiliary.add(_revertAction);
    _navPanePopupMenuForAuxiliary.addSeparator();
    _navPanePopupMenuForAuxiliary.add(_closeAction);
    _navPanePopupMenuForAuxiliary.addSeparator();
    _navPanePopupMenuForAuxiliary.add(_printDefDocAction);
    _navPanePopupMenuForAuxiliary.add(_printDefDocPreviewAction);
    _navPanePopupMenuForAuxiliary.addSeparator();
    _navPanePopupMenuForAuxiliary.add(_compileAction);
    _navPanePopupMenuForAuxiliary.add(_junitAction);
    _navPanePopupMenuForAuxiliary.add(_javadocCurrentAction);
    _navPanePopupMenuForAuxiliary.add(_runAction);
    _navPanePopupMenuForAuxiliary.addSeparator();
    _navPanePopupMenuForAuxiliary.add(_removeAuxiliaryAction);
    
    
    _navPanePopupMenu = new JPopupMenu();
    _navPanePopupMenu.add(_saveAction);
    _navPanePopupMenu.add(_saveAsAction);
    _navPanePopupMenu.add(_revertAction);
    _navPanePopupMenu.addSeparator();
    _navPanePopupMenu.add(_closeAction);
    _navPanePopupMenu.addSeparator();
    _navPanePopupMenu.add(_printDefDocAction);
    _navPanePopupMenu.add(_printDefDocPreviewAction);
    _navPanePopupMenu.addSeparator();
    _navPanePopupMenu.add(_compileAction);
    _navPanePopupMenu.add(_junitAction);
    _navPanePopupMenu.add(_javadocCurrentAction);
    _navPanePopupMenu.add(_runAction);
    _model.getDocCollectionWidget().addMouseListener(new RightClickMouseAdapter() {
      protected void _popupAction(MouseEvent e) {
        if (_model.getDocumentNavigator().selectDocumentAt(e.getX(), e.getY())) {
          if (_model.getDocumentNavigator().isGroupSelected())
            _navPaneFolderPopupMenu.show(e.getComponent(), e.getX(), e.getY());
          
          else {
            try {
              String groupName = _model.getDocumentNavigator().getNameOfSelectedTopLevelGroup();
              if (groupName.equals(_model.getSourceBinTitle()))
                _navPanePopupMenu.show(e.getComponent(), e.getX(), e.getY());
              else if (groupName.equals(_model.getExternalBinTitle())) {
                INavigatorItem n = _model.getDocumentNavigator().getCurrent();
                if (n != null) {
                  OpenDefinitionsDocument d = (OpenDefinitionsDocument) n;
                  if (d.isUntitled()) { _navPanePopupMenu.show(e.getComponent(), e.getX(), e.getY()); }
                  else _navPanePopupMenuForExternal.show(e.getComponent(), e.getX(), e.getY());
                }
              }
              else if (groupName.equals(_model.getAuxiliaryBinTitle()))
                _navPanePopupMenuForAuxiliary.show(e.getComponent(), e.getX(), e.getY());
            }
            catch(GroupNotSelectedException ex) {
              
              if (_model.isProjectActive())
                _navPanePopupMenuForRoot.show(e.getComponent(), e.getX(), e.getY());
              else  _navPanePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
          }
        }
      }
    });
    
    
    _interactionsPanePopupMenu = new JPopupMenu();
    _interactionsPanePopupMenu.add(cutAction);
    _interactionsPanePopupMenu.add(copyAction);
    _interactionsPanePopupMenu.add(pasteAction);
    _interactionsPanePopupMenu.addSeparator();
    _interactionsPanePopupMenu.add(_printInteractionsAction);
    _interactionsPanePopupMenu.add(_printInteractionsPreviewAction);
    _interactionsPanePopupMenu.addSeparator();
    _interactionsPanePopupMenu.add(_executeHistoryAction);
    _interactionsPanePopupMenu.add(_loadHistoryScriptAction);
    _interactionsPanePopupMenu.add(_saveHistoryAction);
    _interactionsPanePopupMenu.add(_clearHistoryAction);
    _interactionsPanePopupMenu.addSeparator();
    _interactionsPanePopupMenu.add(_resetInteractionsAction);
    _interactionsPanePopupMenu.add(_viewInteractionsClassPathAction);
    _interactionsPanePopupMenu.add(_copyInteractionToDefinitionsAction);
    _interactionsPane.addMouseListener(new RightClickMouseAdapter() {
      protected void _popupAction(MouseEvent e) {
        _interactionsPane.requestFocusInWindow();
        _interactionsPanePopupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    });
    
    _consolePanePopupMenu = new JPopupMenu();
    _consolePanePopupMenu.add(_clearConsoleAction);
    _consolePanePopupMenu.addSeparator();
    _consolePanePopupMenu.add(_printConsoleAction);
    _consolePanePopupMenu.add(_printConsolePreviewAction);
    _consolePane.addMouseListener(new RightClickMouseAdapter() {
      protected void _popupAction(MouseEvent e) {
        _consolePane.requestFocusInWindow();
        _consolePanePopupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    });
  }
  
  private void nextRecentDoc() {
    if (_recentDocFrame.isVisible()) _recentDocFrame.next();
    else _recentDocFrame.setVisible(true);
  }
  
  private void prevRecentDoc() {
    if (_recentDocFrame.isVisible()) {
      _recentDocFrame.prev();
    }else{
      _recentDocFrame.setVisible(true);
    }
  }
  
  private void hideRecentDocFrame() {
    if (_recentDocFrame.isVisible()) {
      _recentDocFrame.setVisible(false);
      OpenDefinitionsDocument doc = _recentDocFrame.getDocument();
      if (doc != null) {
        _model.getDocumentNavigator().setActiveDoc(doc);
      }
    }
  }
  
  KeyListener _historyListener = new KeyListener() {
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode()==java.awt.event.KeyEvent.VK_BACK_QUOTE && e.isControlDown() && !e.isShiftDown()) {
        nextRecentDoc();
      }
      if (e.getKeyCode()==java.awt.event.KeyEvent.VK_BACK_QUOTE && e.isControlDown() && e.isShiftDown()) {
        prevRecentDoc();
      }



    }
    public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL) {
        hideRecentDocFrame();
      }
    }
    public void keyTyped(KeyEvent e) {
      
    }
  };
  
  FocusListener _focusListenerForRecentDocs = new FocusListener() {
    public void focusLost(FocusEvent e) {
      hideRecentDocFrame();
    }
    public void focusGained(FocusEvent e) {
    }
  };
  
  
  
  JScrollPane _createDefScrollPane(OpenDefinitionsDocument doc) {
    
    
    DefinitionsPane pane = new DefinitionsPane(this, doc);
    pane.addKeyListener(_historyListener);
    pane.addFocusListener(_focusListenerForRecentDocs);
    
    
    
    _installNewDocumentListener(doc);
    ErrorCaretListener caretListener = new ErrorCaretListener(doc, pane, this);
    pane.addErrorCaretListener(caretListener);
    
    
    doc.addDocumentListener(new DocumentUIListener() {
      public void changedUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            revalidateLineNums();
          }
        });
      }
      public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            revalidateLineNums();
          }
        });
      }
      public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            revalidateLineNums();
          }
        });
      }
    });
    
    
    pane.addCaretListener(_posListener);
    
    
    pane.addFocusListener(new LastFocusListener());
    
    
    final JScrollPane scroll = new BorderlessScrollPane(pane,
                                                  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    pane.setScrollPane(scroll);
    
    
    
    
    
    if (DrJava.getConfig().getSetting(LINEENUM_ENABLED).booleanValue()) {
      scroll.setRowHeaderView(new LineEnumRule(pane));
    }
    
    _defScrollPanes.put(doc, scroll);
    
    return scroll;
  }
  
  
  private void _setUpPanes() {
    
    JScrollPane defScroll = _defScrollPanes.get(_model.getActiveDocument());
    
    
    if (_model.getDebugger().isAvailable()) {
      try {
        _debugPanel = new DebugPanel(this);
        
        
        int debugHeight = DrJava.getConfig().getSetting(DEBUG_PANEL_HEIGHT).intValue();
        Dimension debugMinSize = _debugPanel.getMinimumSize();
        
        
        if ((debugHeight > debugMinSize.height)) debugMinSize.height = debugHeight;
        _debugPanel.setPreferredSize(debugMinSize);
      }
      catch(NoClassDefFoundError e) {
        
        _debugPanel = null;
      }





    } 
    else _debugPanel = null;
    
    
    _docSplitPane = 
      new BorderlessSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                              new JScrollPane(_model.getDocumentNavigator().asContainer()), defScroll);
    _debugSplitPane = new BorderlessSplitPane(JSplitPane.VERTICAL_SPLIT, true);
    _debugSplitPane.setBottomComponent(_debugPanel);
    _mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, _docSplitPane, _tabbedPane);
    _mainSplit.setResizeWeight(1.0);
    _debugSplitPane.setResizeWeight(1.0);
    getContentPane().add(_mainSplit, BorderLayout.CENTER);
    
    
    
    
    

    
    
    _mainSplit.setDividerLocation(_mainSplit.getHeight() - 132);

    _mainSplit.setOneTouchExpandable(true);
    _debugSplitPane.setOneTouchExpandable(true);
    
    int docListWidth = DrJava.getConfig().getSetting(DOC_LIST_WIDTH).intValue();
    
    
    _docSplitPane.setDividerLocation(docListWidth);
    _docSplitPane.setOneTouchExpandable(true);
  }
  
  
  void _switchDefScrollPane() {
    
    
    
    
    _currentDefPane.notifyInactive();
    


    JScrollPane scroll = _defScrollPanes.get(_model.getActiveDocument());
    
    if (scroll == null)  scroll = _createDefScrollPane(_model.getActiveDocument());
    
    
    _reenableScrollBar();
    
    int oldLocation = _docSplitPane.getDividerLocation();
    _docSplitPane.setRightComponent(scroll); 
    _docSplitPane.setDividerLocation(oldLocation);
    
    
    
    
    if (_currentDefPane.isEditable()) {
      _currentDefPane = (DefinitionsPane) scroll.getViewport().getView();
      _currentDefPane.notifyActive();
    }
    else {
      try { _currentDefPane.setEditable(true); }
      catch(NoSuchDocumentException e) {  }
      
      _currentDefPane = (DefinitionsPane) scroll.getViewport().getView();
      _currentDefPane.notifyActive();
      _currentDefPane.setEditable(false);
    }
    
    resetUndo();
    _updateDebugStatus();
  }
  
  
  public void resetUndo() {
    _undoAction.setDelegatee(_currentDefPane.getUndoAction());
    _redoAction.setDelegatee(_currentDefPane.getRedoAction());
  }
  
  public DefinitionsPane getDefPaneGivenODD(OpenDefinitionsDocument doc) {
    JScrollPane scroll = _defScrollPanes.get(doc);
    if (scroll == null)
      throw new UnexpectedException(new Exception("Breakpoint set in a closed document."));
    
    DefinitionsPane pane = (DefinitionsPane) scroll.getViewport().getView();
    return pane;
  }
  
  
  private void _reenableScrollBar() {
    JScrollPane scroll = _defScrollPanes.get(_model.getActiveDocument());
    if (scroll == null)
      throw new UnexpectedException(new Exception("Current definitions scroll pane not found."));
    
    JScrollBar oldbar = scroll.getVerticalScrollBar();
    JScrollBar newbar = scroll.createVerticalScrollBar();
    newbar.setMinimum(oldbar.getMinimum());
    newbar.setMaximum(oldbar.getMaximum());
    newbar.setValue(oldbar.getValue());
    newbar.setVisibleAmount(oldbar.getVisibleAmount());
    newbar.setEnabled(true);
    newbar.revalidate();
    scroll.setVerticalScrollBar(newbar);
    
    
    oldbar = scroll.getHorizontalScrollBar();
    newbar = scroll.createHorizontalScrollBar();
    newbar.setMinimum(oldbar.getMinimum());
    newbar.setMaximum(oldbar.getMaximum());
    newbar.setValue(oldbar.getValue());
    newbar.setVisibleAmount(oldbar.getVisibleAmount());
    newbar.setEnabled(true);
    newbar.revalidate();
    scroll.setHorizontalScrollBar(newbar);
    scroll.revalidate();
  }
  
  
  private JMenuItem _newCheckBoxMenuItem(Action action) {
    String RADIO_ICON_KEY = "RadioButtonMenuItem.checkIcon";
    String CHECK_ICON_KEY = "CheckBoxMenuItem.checkIcon";
    
    
    Object radioIcon = UIManager.get(RADIO_ICON_KEY);
    
    
    
    UIManager.put(RADIO_ICON_KEY, UIManager.get(CHECK_ICON_KEY));
    JRadioButtonMenuItem pseudoCheckBox = new JRadioButtonMenuItem(action);
    
    
    UIManager.put(RADIO_ICON_KEY, radioIcon);
    
    return pseudoCheckBox;
  }
  
  
  private File _getFullFile(File f) throws IOException {
    if (PlatformFactory.ONLY.isWindowsPlatform() &&
        ((f.getAbsolutePath().indexOf("..") != -1) || (f.getAbsolutePath().indexOf("./") != -1) ||
         (f.getAbsolutePath().indexOf(".\\") != -1))) {
      return f.getCanonicalFile();
    }
    return f.getAbsoluteFile();
  }
  
  
  private void _setCurrentDirectory(File file) {
    
    try {
      file = _getFullFile(file);
      _openChooser.setCurrentDirectory(file);
      _saveChooser.setCurrentDirectory(file);

    }
    catch (IOException ioe) {
      
    }
  }
  
  
  private void _setCurrentDirectory(OpenDefinitionsDocument doc) {
    try {
      File file = doc.getFile();
      if (file != null) _setCurrentDirectory(file); 
    }
    catch (FileMovedException fme) {
      
      _setCurrentDirectory(fme.getFile());
    }
  }
  
  
  private void _setMainFont() {
    
    Font f = DrJava.getConfig().getSetting(FONT_MAIN);
    
    for (JScrollPane scroll: _defScrollPanes.values()) {
      if (scroll != null) {
        DefinitionsPane pane = (DefinitionsPane) scroll.getViewport().getView();
        pane.setFont(f);
        
        if (DrJava.getConfig().getSetting(LINEENUM_ENABLED).booleanValue()) {
          scroll.setRowHeaderView( new LineEnumRule(pane) );
        }
      }
    }
    
    
    _interactionsPane.setFont(f);
    _interactionsController.setDefaultFont(f);
    
    
    _consolePane.setFont(f);
    _consoleController.setDefaultFont(f);
    
    _findReplace.setFieldFont(f);
    _compilerErrorPanel.setListFont(f);
    _junitErrorPanel.setListFont(f);
    _javadocErrorPanel.setListFont(f);
  }
  
  
  private void _updateNormalColor() {
    
    Color norm = DrJava.getConfig().getSetting(DEFINITIONS_NORMAL_COLOR);
    
    
    _model.getDocCollectionWidget().setForeground(norm);
    
    
    _repaintLineNums();
  }
  
  
  private void _updateBackgroundColor() {
    
    Color back = DrJava.getConfig().getSetting(DEFINITIONS_BACKGROUND_COLOR);
    
    
    _model.getDocCollectionWidget().setBackground(back);
    
    
    _repaintLineNums();
  }
  
  
  private void _updateLineNums() {
    if (DrJava.getConfig().getSetting(LINEENUM_ENABLED).booleanValue()) {
      
      
      for (JScrollPane spane: _defScrollPanes.values()) { 
        
        LineEnumRule ler = (LineEnumRule) spane.getRowHeader().getView();
        ler.updateFont();
        ler.revalidate();
      }
      
      
      _repaintLineNums();
    }
  }
  
  
  private void _repaintLineNums() {
    JScrollPane front = _defScrollPanes.get(_model.getActiveDocument());
    if (front != null) {
      JViewport rhvport = front.getRowHeader();
      
      if (rhvport != null) {
        Component view = rhvport.getView();
        view.repaint();
      }
    }
  }
  
  
  public void revalidateLineNums() {
    if (DrJava.getConfig().getSetting(LINEENUM_ENABLED).booleanValue()) {
      JScrollPane sp = _defScrollPanes.get(_model.getActiveDocument());
      if (sp!=null) {
        LineEnumRule ler = (LineEnumRule)sp.getRowHeader().getView();
        ler.revalidate();
        _repaintLineNums();
      }
    }
  }
  
  
  private void _updateDefScrollRowHeader() {
    boolean ruleEnabled = DrJava.getConfig().getSetting(LINEENUM_ENABLED).booleanValue();
    
    for (JScrollPane scroll: _defScrollPanes.values()) {
      if (scroll != null) {
        DefinitionsPane pane = (DefinitionsPane) scroll.getViewport().getView();
        if (scroll.getRowHeader() == null || scroll.getRowHeader().getView() == null) {
          if (ruleEnabled) scroll.setRowHeaderView(new LineEnumRule(pane));
        }
        else if (! ruleEnabled) scroll.setRowHeaderView(null);
      }
    }
  }
  
  
  private void _removeThreadLocationHighlight() {
    if (_currentThreadLocationHighlight != null) {
      _currentThreadLocationHighlight.remove();
      _currentThreadLocationHighlight = null;
    }
  }
  
  
  private void _disableStepTimer() {
    synchronized(_debugStepTimer) {  
      if (_debugStepTimer.isRunning()) _debugStepTimer.stop();
    }
  }
  
  
  private void _updateDebugStatus() {
    if (! inDebugMode()) return;
    
    
    if (_model.getActiveDocument().isUntitled() || _model.getActiveDocument().getClassFileInSync()) {
      
      if (_debugPanel.getStatusText().equals(DEBUGGER_OUT_OF_SYNC)) _debugPanel.setStatusText("");
    } 
    else {
      
      if (_debugPanel.getStatusText().equals("")) {
        _debugPanel.setStatusText(DEBUGGER_OUT_OF_SYNC);
      }
    }
    _debugPanel.repaint();  
  }
  
  
  protected void _disableInteractionsPane() {
    
    Runnable command = new Runnable() {
      public void run() {
        _interactionsPane.setEditable(false);
        _interactionsPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        if (_interactionsScriptController != null) _interactionsScriptController.setActionsDisabled();
      }
    };
    Utilities.invokeLater(command);
  }
  
  
  protected void _enableInteractionsPane() {
    
    Runnable command = new Runnable() {
      public void run() {
        
        
        _interactionsPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        _interactionsPane.setEditable(true);
        _interactionsController.moveToEnd();
        if (_interactionsPane.hasFocus()) _interactionsPane.getCaret().setVisible(true);
        if (_interactionsScriptController != null) _interactionsScriptController.setActionsEnabled();
      }
    };
    Utilities.invokeLater(command);
  }
  
  
  public void commentLines() {
    
    OpenDefinitionsDocument openDoc = _model.getActiveDocument();
    int caretPos = _currentDefPane.getCaretPosition();
    openDoc.setCurrentLocation(caretPos);
    int start = _currentDefPane.getSelectionStart();
    int end = _currentDefPane.getSelectionEnd();
    _currentDefPane.endCompoundEdit();
    DummyOpenDefDoc dummy = new DummyOpenDefDoc();
    _currentDefPane.notifyInactive();
    int newEnd = openDoc.commentLines(start, end);
    _currentDefPane.notifyActive();
    _currentDefPane.setCaretPosition(start+2);
    if (start != end) _currentDefPane.moveCaretPosition(newEnd);
  }
  
  
  public void uncommentLines() {
    
    OpenDefinitionsDocument openDoc = _model.getActiveDocument();
    int caretPos = _currentDefPane.getCaretPosition();
    openDoc.setCurrentLocation(caretPos);
    int start = _currentDefPane.getSelectionStart();
    int end = _currentDefPane.getSelectionEnd();
    _currentDefPane.endCompoundEdit();
    
    
    _currentDefPane.notifyInactive();
    openDoc.setCurrentLocation(start);
    Position startPos;
    try {startPos = openDoc.createPosition(start);}
    catch (BadLocationException e) {throw new UnexpectedException(e);}
    
    int startOffset = startPos.getOffset();        
    int newEnd = openDoc.uncommentLines(start, end);
    _currentDefPane.notifyActive();
    if (startOffset != startPos.getOffset()) start -= 2;      
    _currentDefPane.setCaretPosition(start);
    if (start != end)   _currentDefPane.moveCaretPosition(newEnd);
  }
  
  
  
  private static class GlassPane extends JComponent {
    
    
    public GlassPane() {
      addKeyListener(new KeyAdapter() { });
      addMouseListener(new MouseAdapter() { });
      super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
  }
  
  
  private class UIDebugListener implements DebugListener {
    
    

    
    
    public void debuggerStarted() { showDebugger(); }
    
    
    public void debuggerShutdown() {
      _disableStepTimer();
      



          hideDebugger();
          _removeThreadLocationHighlight();



    }
    
    public void currThreadSet(DebugThreadData dtd) { }
    
    
    public void threadLocationUpdated(final OpenDefinitionsDocument doc, final int lineNumber,
                                      final boolean shouldHighlight) {


          
          
          
          
          
          








          
          if (!_model.getActiveDocument().equals(doc)) _model.setActiveDocument(doc);
          else _model.refreshActiveDocument();
          
          
          
          if (_currentDefPane.getSize().getWidth() > 0 && _currentDefPane.getSize().getHeight() > 0) {



                _currentDefPane.centerViewOnLine(lineNumber);
                _currentDefPane.requestFocusInWindow();


          }
          
          
          SwingUtilities.invokeLater(new Runnable() {  
            public void run() {
              if (shouldHighlight) {
                _removeThreadLocationHighlight();
                int startOffset = doc.getOffset(lineNumber);
                if (startOffset > -1) {
                  int endOffset = doc.getLineEndPos(startOffset);
                  if (endOffset > -1) {
                    _currentThreadLocationHighlight =
                      _currentDefPane.getHighlightManager().
                      addHighlight(startOffset, endOffset, DefinitionsPane.THREAD_PAINTER);
                  }
                }
              }
              
              if (doc.isModifiedSinceSave() && !_currentDefPane.hasWarnedAboutModified()) {
                
                _showDebuggingModifiedFileWarning();
                
                
                
              }
              if (shouldHighlight) {
                
                _interactionsPane.requestFocusInWindow();
                showTab(_interactionsPane);
              }
              _updateDebugStatus();
            }
          });


    }
                            
    
    public void breakpointSet(final Breakpoint bp) {



          DefinitionsPane bpPane = getDefPaneGivenODD(bp.getDocument());
          _breakpointHighlights.
            put(bp, bpPane.getHighlightManager().
                    addHighlight(bp.getStartOffset(), bp.getEndOffset(), 
                                 bp.isEnabled() ? DefinitionsPane.BREAKPOINT_PAINTER
                                                : DefinitionsPane.DISABLED_BREAKPOINT_PAINTER));
          _updateDebugStatus();



    }
    
    
    public void breakpointReached(Breakpoint bp) { 
      


    }
    
    
    public void breakpointChanged(Breakpoint bp) { 
      breakpointRemoved(bp);
      breakpointSet(bp);
    }
    
    
    public void breakpointRemoved(final Breakpoint bp) {
      
      HighlightManager.HighlightInfo highlight = _breakpointHighlights.get(bp);
      if (highlight != null) highlight.remove();
      _breakpointHighlights.remove(bp);
    }
    
    public void watchSet(final DebugWatchData w) { }
    public void watchRemoved(final DebugWatchData w) { }
    
    
    public void stepRequested() {
      
      synchronized(_debugStepTimer) {  
        if (!_debugStepTimer.isRunning()) _debugStepTimer.start();
      }
    }
    
    public void currThreadSuspended() {
      _disableStepTimer();
      
      
      Runnable command = new Runnable() {
        public void run() { _setThreadDependentDebugMenuItems(true); }
      };
      Utilities.invokeLater(command);
    }
    
    
    public void currThreadResumed() {
      


          _setThreadDependentDebugMenuItems(false);
          _removeThreadLocationHighlight();



    }
    
    
    public void threadStarted() { }
    
    
    public void currThreadDied() {
      _disableStepTimer();
      


          if (inDebugMode()) {
            try {
              if (!_model.getDebugger().hasSuspendedThreads()) {
                
                
                _setThreadDependentDebugMenuItems(false);
                _removeThreadLocationHighlight();
                
                
                _interactionsController.moveToPrompt(); 
              }
            }
            catch (DebugException de) {
              _showError(de, "Debugger Error", "Error with a thread in the debugger.");
            }
          }



    }
    
    
    public void nonCurrThreadDied() { }
  }
  
  
  private class ModelListener implements GlobalModelListener {
    
    private int _fnfCount = 0;
    
    private boolean resetFNFCount() { return _fnfCount == 0; }
    
    private boolean filesNotFound() { return _fnfCount > 0; }
    
    public void fileNotFound(File f) {
      _fnfCount++;
      _showFileNotFoundError(new FileNotFoundException("File " + f + " cannot be found"));
    }
    
    public void newFileCreated(final OpenDefinitionsDocument doc) {
      Utilities.invokeLater(new Runnable() { public void run() { _createDefScrollPane(doc); } });
    }
    
    public void fileSaved(final OpenDefinitionsDocument doc) {

      Utilities.invokeLater(new Runnable() {
        public void run() {
          doc.documentSaved();  
          _saveAction.setEnabled(false);
          _revertAction.setEnabled(true);
          updateFileTitle();
          _currentDefPane.requestFocusInWindow();
          try {
            File f = doc.getFile();
            if (! _model.inProject(f)) _recentFileManager.updateOpenFiles(f);
          }
          catch (FileMovedException fme) {
            File f = fme.getFile();
            
            if (! _model.inProject(f)) _recentFileManager.updateOpenFiles(f);
          }
          
          _updateDebugStatus();
        }
      });
    }
    
    public void fileOpened(final OpenDefinitionsDocument doc) { 
      Utilities.invokeLater(new Runnable() { public void run() { _fileOpened(doc); } });  
    }
    
    private void _fileOpened(final OpenDefinitionsDocument doc) {
      
      try {
        File f = doc.getFile();
        if (! _model.inProject(f)) {
          _recentFileManager.updateOpenFiles(f);
          if (_model.inProjectPath(doc)) _model.setProjectChanged(true);
        }
      }
      catch (FileMovedException fme) {
        File f = fme.getFile();
        
        if (! _model.inProject(f)) _recentFileManager.updateOpenFiles(f);
      }
    }
    
    
    public void fileClosed(final OpenDefinitionsDocument doc) {
      Utilities.invokeLater(new Runnable() { public void run() { _fileClosed(doc); } });
    }
    
    
    private void _fileClosed(OpenDefinitionsDocument doc) {
      _recentDocFrame.closeDocument(doc);
      _removeErrorListener(doc);
      JScrollPane jsp = _defScrollPanes.get(doc);
      if (jsp != null) {
        ((DefinitionsPane)jsp.getViewport().getView()).close();
        _defScrollPanes.remove(doc);
      }
    }
    
    public void fileReverted(OpenDefinitionsDocument doc) {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          updateFileTitle();
          _saveAction.setEnabled(false);
          _currentDefPane.resetUndo();
          _currentDefPane.hasWarnedAboutModified(false);
          _currentDefPane.setPositionAndScroll(0);
          _updateDebugStatus();
        }
      });
    }
    
    public void undoableEditHappened() {
      Utilities.invokeLater(new Runnable() {
        public void run() {      
          _currentDefPane.getUndoAction().updateUndoState();
          _currentDefPane.getRedoAction().updateRedoState();
        }
      });
    }
    
    public void activeDocumentChanged(final OpenDefinitionsDocument active) {

      
      Utilities.invokeLater(new Runnable() {  
        public void run() {
          _recentDocFrame.pokeDocument(active);
          _switchDefScrollPane();
          
          boolean isModified = active.isModifiedSinceSave();
          boolean canCompile = (! isModified && ! active.isUntitled());
          _saveAction.setEnabled(! canCompile);
          _revertAction.setEnabled(! active.isUntitled());
          
          
          int pos = _currentDefPane.getCaretPosition();
          _currentDefPane.getErrorCaretListener().updateHighlight(pos);
          
          
          _setCurrentDirectory(active);
          
          
          updateFileTitle();
          _currentDefPane.requestFocusInWindow();
          _posListener.updateLocation();
          
          
          if (isModified) _model.getDocumentNavigator().repaint();
          
          try { active.revertIfModifiedOnDisk(); }
          catch (FileMovedException fme) { _showFileMovedError(fme); }
          catch (IOException e) { _showIOError(e); }
          
          
          if (_findReplace.isDisplayed()) {
            _findReplace.stopListening();
            _findReplace.beginListeningTo(_currentDefPane);
            
            
          }
        }
      });
    }
    
    public void focusOnDefinitionsPane() {
      _currentDefPane.requestFocusInWindow();
    }
    
    public void interactionStarted() {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          _disableInteractionsPane();
          _runAction.setEnabled(false);
          _runProjectAction.setEnabled(false);
        }
      });
    }
    
    public void interactionEnded() {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          _enableInteractionsPane();
          _runAction.setEnabled(true);
          _runProjectAction.setEnabled(true);
        }
      });
    }
    
    public void interactionErrorOccurred(final int offset, final int length) {
      Utilities.invokeLater(new Runnable() { public void run() { _interactionsPane.highlightError(offset, length); } });
    }
    
    
    public void interpreterChanged(final boolean inProgress) {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          _runAction.setEnabled(! inProgress);
          _runProjectAction.setEnabled(! inProgress);
          if (inProgress) _disableInteractionsPane();
          else _enableInteractionsPane();
        }
      });
    }
    
    public void compileStarted() {
      
     Utilities.invokeLater(new Runnable() {
        public void run() {

          showTab(_compilerErrorPanel);
          _compilerErrorPanel.setCompilationInProgress();
          _saveAction.setEnabled(false);
        }
      });
    }    
    
    public void compileEnded(File workDir, final File[] excludedFiles) {
      
      Utilities.invokeLater(new Runnable() {
        public void run() {

          _compilerErrorPanel.reset(excludedFiles);
          if (inDebugMode()) {

            
            _updateDebugStatus();
          }


          if ((DrJava.getConfig().getSetting(DIALOG_COMPLETE_SCAN_CLASS_FILES).booleanValue()) && 
              (_model.getBuildDirectory()!=null)) {
            _scanClassFiles();
          }
          _model.refreshActiveDocument();
        }
      });
    }
    
    public void runStarted(final OpenDefinitionsDocument doc) {
      
      Utilities.invokeLater(new Runnable() {
        public void run() {
          
          showTab(_interactionsPane);
        }
      });
    }
    
    public void junitStarted() {
      
      
      

      Utilities.invokeLater(new Runnable() {
        public void run() {
          
          
          try { showTab(_junitErrorPanel);
            _junitErrorPanel.setJUnitInProgress();
            
            
          }
          finally { hourglassOff(); }  
        }
      });
    }
    
    
    public void junitClassesStarted() {
      
      
      Utilities.invokeLater(new Runnable() {
        public void run() {


          showTab(_junitErrorPanel);
          _junitErrorPanel.setJUnitInProgress();
          
          
        } 
      });
    }
    
    
    
    public void junitSuiteStarted(final int numTests) {
      Utilities.invokeLater(new Runnable() { public void run() { _junitErrorPanel.progressReset(numTests); } });
    }
    
    public void junitTestStarted(final String name) {
      Utilities.invokeLater(new Runnable() {
        public void run() { _junitErrorPanel.getErrorListPane().testStarted(name);  }
      });          
    }
    
    public void junitTestEnded(final String name, final boolean succeeded, final boolean causedError) {

      
      Utilities.invokeLater(new Runnable() {
        public void run() {
          _junitErrorPanel.getErrorListPane().testEnded(name, succeeded, causedError); 
          _junitErrorPanel.progressStep(succeeded);
          _model.refreshActiveDocument();
        }
      });
    }
    
    public void junitEnded() {
      

      Utilities.invokeLater(new Runnable() {
        public void run() {
          try {
            _restoreJUnitActionsEnabled();
            _junitErrorPanel.reset();
            _model.refreshActiveDocument();
          }
          finally { 


          }
        }
      });
    }
    
    public void javadocStarted() {
      
      
      Runnable command = new Runnable() {
        public void run() {
          
          hourglassOn();
          
          showTab(_javadocErrorPanel);
          _javadocErrorPanel.setJavadocInProgress();
          _javadocAllAction.setEnabled(false);
          _javadocCurrentAction.setEnabled(false);
        }
      };
      Utilities.invokeLater(command);
    }
    
    public void javadocEnded(final boolean success, final File destDir,
                             final boolean allDocs) {
      
      Runnable command = new Runnable() {
        public void run() {
          
          try {
            showTab(_javadocErrorPanel);
            _javadocAllAction.setEnabled(true);
            _javadocCurrentAction.setEnabled(true);
            _javadocErrorPanel.reset();
            _model.refreshActiveDocument();
          }
          finally { hourglassOff(); }
          
          

          if (success) {
            String className;
            try {
              className = _model.getActiveDocument().getQualifiedClassName();
              className = className.replace('.', File.separatorChar);
            }
            catch (ClassNameNotFoundException cnf) {
              
              
              className = "";
            }
            try {
              String fileName = (allDocs || className.equals("")) ?
                "index.html" : (className + ".html");
              File index = new File(destDir, fileName);
              URL address = index.getAbsoluteFile().toURL();
              if (PlatformFactory.ONLY.openURL(address)) {
                JavadocFrame _javadocFrame = new JavadocFrame(destDir, className, allDocs);
                _javadocFrame.setVisible(true);
              }
            }
            catch (MalformedURLException me) { throw new UnexpectedException(me); }
            catch (IllegalStateException ise) {
              
              
              String msg =
                "Javadoc completed successfully, but did not produce any HTML files.\n" +
                "Please ensure that your access level in Preferences is appropriate.";
              JOptionPane.showMessageDialog(MainFrame.this, msg,
                                            "No output to display.",
                                            JOptionPane.INFORMATION_MESSAGE);
            }
          }
        }
      };
      Utilities.invokeLater(command);
    }
    
    public void interpreterExited(final int status) {
      
      if (DrJava.getConfig().getSetting(INTERACTIONS_EXIT_PROMPT).booleanValue() && 
          ! Utilities.TextAreaMessageDialog.TEST_MODE) {
        
        
        Runnable command = new Runnable() {
          public void run() {
            String msg = "The interactions window was terminated by a call " +
              "to System.exit(" + status + ").\n" +
              "The interactions window will now be restarted.";
            
            String title = "Interactions terminated by System.exit(" + status + ")";
            
            ConfirmCheckBoxDialog dialog =
              new ConfirmCheckBoxDialog(MainFrame.this, title, msg,
                                        "Do not show this message again",
                                        JOptionPane.INFORMATION_MESSAGE,
                                        JOptionPane.DEFAULT_OPTION);
            if (dialog.show() == JOptionPane.OK_OPTION && dialog.getCheckBoxValue()) {
              DrJava.getConfig().setSetting(INTERACTIONS_EXIT_PROMPT, Boolean.FALSE);
            }
          }
        };
        Utilities.invokeLater(command);
      }
    }
    
    public void interpreterResetFailed(Throwable t) { interpreterReady(FileOption.NULL_FILE); }
    
    public void interpreterResetting() {
      
      Runnable command = new Runnable() {
        public void run() {
          Debugger dm = _model.getDebugger();


          _junitAction.setEnabled(false);
          _junitAllAction.setEnabled(false);
          _runAction.setEnabled(false);
          _runProjectAction.setEnabled(false);
          _closeInteractionsScript();
          _interactionsPane.setEditable(false);
          _interactionsPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          if (_model.getDebugger().isAvailable()) _toggleDebuggerAction.setEnabled(false);
        }
      };
      Utilities.invokeLater(command);
    }
    
    public void interpreterReady(File wd) {
      
      Runnable command = new Runnable() {
        public void run() {
          interactionEnded();
          _runAction.setEnabled(true);
          _runProjectAction.setEnabled(true);
          _junitAction.setEnabled(true);
          _junitAllAction.setEnabled(true);


          if (_model.getDebugger().isAvailable()) {
            _toggleDebuggerAction.setEnabled(true);
          }
          
          
          
          
          _interactionsController.interruptConsoleInput();
        }
      };
      Utilities.invokeLater(command);
    }
    
    public void slaveJVMUsed() {  }
    
    public void consoleReset() { }
    
    public void saveBeforeCompile() {
      
      
      Utilities.invokeAndWait(new Runnable() {  
        public void run() {
          _saveAllBeforeProceeding
            ("To compile, you must first save ALL modified files.\n" + "Would you like to save and then compile?",
             ALWAYS_SAVE_BEFORE_COMPILE,
             "Always save before compiling");
        }
      });
    }
    
    
    public void compileBeforeJUnit(final CompilerListener testAfterCompile) {
      if (DrJava.getConfig().getSetting(ALWAYS_COMPILE_BEFORE_JUNIT).booleanValue() || ! MainFrame.this.isVisible()) {
        
        _model.getCompilerModel().addListener(testAfterCompile);  
        _compileAll();
      }
      else { 
       Utilities.invokeLater(new Runnable() {  
          public void run() {
            String title = "Must Compile All Source Files to Run Unit Tests";
            String msg = "To unit test all documents, you must first compile all out of sync source files.\n" + 
              "Would you like to compile all files and run the specified test?";
            int rc = JOptionPane.showConfirmDialog(MainFrame.this, msg, title, JOptionPane.YES_NO_OPTION); 
            
            switch (rc) {
              case JOptionPane.YES_OPTION:  
                _model.getCompilerModel().addListener(testAfterCompile);  
                _compileAll();
                break;
              case JOptionPane.NO_OPTION:  
                _model.getJUnitModel().nonTestCase(true);  
                break;
              default:
                throw new RuntimeException("Invalid returnCode from showConfirmDialog: " + rc);
            }
          }
        });
      }
    }
                              
    public void saveBeforeJavadoc() {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          _saveAllBeforeProceeding
            ("To run Javadoc, you must first save ALL modified files.\n" +
             "Would you like to save and then run Javadoc?", ALWAYS_SAVE_BEFORE_JAVADOC,
             "Always save before running Javadoc");
        }
      });
    }
    
    
    private void _saveAllBeforeProceeding(String message, BooleanOption option, String checkMsg) {

      if (_model.hasModifiedDocuments()) {
        if (!DrJava.getConfig().getSetting(option).booleanValue()) {
          ConfirmCheckBoxDialog dialog =
            new ConfirmCheckBoxDialog(MainFrame.this,
                                      "Must Save All Files to Continue",
                                      message,
                                      checkMsg);
          int rc = dialog.show();
          
          switch (rc) {
            case JOptionPane.YES_OPTION:
              _saveAll();
              
              if (dialog.getCheckBoxValue())  DrJava.getConfig().setSetting(option, Boolean.TRUE);
              break;
            case JOptionPane.NO_OPTION:
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
              
              break;
            default:
              throw new RuntimeException("Invalid rc from showConfirmDialog: " + rc);
          }
        }
        else _saveAll();
      }
    }
    
    
    public void saveUntitled() { _saveAs(); }
    
    public void filePathContainsPound() {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          if (DrJava.getConfig().getSetting(WARN_PATH_CONTAINS_POUND).booleanValue()) {
            String msg =
              "Files whose paths contain the '#' symbol cannot be used in the\n" +
              "Interactions Pane due to a bug in Java's file to URL conversion.\n" +
              "It is suggested that you change the name of the directory\n" +
              "containing the '#' symbol.";
            
            String title = "Path Contains Pound Sign";
            
            ConfirmCheckBoxDialog dialog =
              new ConfirmCheckBoxDialog(MainFrame.this, title, msg,
                                        "Do not show this message again",
                                        JOptionPane.WARNING_MESSAGE,
                                        JOptionPane.DEFAULT_OPTION);
            if (dialog.show() == JOptionPane.OK_OPTION && dialog.getCheckBoxValue()) {
              DrJava.getConfig().setSetting(WARN_PATH_CONTAINS_POUND, Boolean.FALSE);
            }
          }
        }
      });
    }
    
     
    public void nonTestCase(boolean isTestAll) {
      

      
      final String message = isTestAll ?
        "There are no compiled JUnit TestCases available for execution.\n" +
        "Perhaps you have not yet compiled your test files."
        :
        "The current document is not a valid JUnit test case.\n" +
        "Please make sure that:\n" +
        "- it has been compiled and\n" +
        "- it is a subclass of junit.framework.TestCase.\n";

      
      
      Utilities.invokeLater(new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(MainFrame.this, message,
                                        "Test Only Executes JUnit test cases",
                                        JOptionPane.ERROR_MESSAGE);
          
          try {
            showTab(_junitErrorPanel);
            _junitAction.setEnabled(true);
            _junitAllAction.setEnabled(true);
            _junitErrorPanel.reset();
          }
          finally { 
            hourglassOff();
            _restoreJUnitActionsEnabled();
          }
        }});
    }
    
     
    public void classFileError(ClassFileError e) {
      
      final String message = 
        "The class file for class " + e.getClassName() + " in source file " + e.getCanonicalPath() + " cannot be loaded.\n "
        + "When DrJava tries to load it, the following error is generated:\n" +  e.getError();
      
      
      
      Utilities.invokeLater(new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(MainFrame.this, message,
                                        "Testing works only on valid class files",
                                        JOptionPane.ERROR_MESSAGE);
          
          showTab(_junitErrorPanel);
          _junitAction.setEnabled(true);
          _junitAllAction.setEnabled(true);
          _junitErrorPanel.reset();
        }});
    }
   
    
    public void currentDirectoryChanged(final File dir) { _setCurrentDirectory(dir); }
    
    
    public boolean canAbandonFile(OpenDefinitionsDocument doc) {
      return _fileSaveHelper(doc, JOptionPane.YES_NO_CANCEL_OPTION);
    }
    
    private boolean _fileSaveHelper(OpenDefinitionsDocument doc, int paneOption) {
      String text,fname;
      OpenDefinitionsDocument lastActive = _model.getActiveDocument();
      if (lastActive != doc) _model.setActiveDocument(doc);
      boolean notFound = false;
      try {
        File file = doc.getFile();
        if (file == null) {
          fname = "Untitled file";
          text = "Untitled file has been modified. Would you like to save it?";
        }
        else {
          fname = file.getName();
          text = fname + " has been modified. Would you like to save it?";
        }
      }
      catch (FileMovedException fme) {
        
        fname = fme.getFile().getName();
        text = fname + " not found on disk. Would you like to save to another file?";
        notFound = true;
      }
      
      int rc = JOptionPane.showConfirmDialog(MainFrame.this, text, "Save " + fname + "?", paneOption);
      switch (rc) {
        case JOptionPane.YES_OPTION:
          boolean saved = false;
          if (notFound) saved = _saveAs(); 
          else saved = _save();
          if (doc != lastActive) _model.setActiveDocument(lastActive);  
          return saved;
        case JOptionPane.NO_OPTION:
          if (doc != lastActive) _model.setActiveDocument(lastActive);  
          return true;
        case JOptionPane.CLOSED_OPTION:
        case JOptionPane.CANCEL_OPTION:
          return false;
        default:                         
          throw new RuntimeException("Invalid option: " + rc);
      }
    }
    
    
    public boolean quitFile(OpenDefinitionsDocument doc) { return _fileSaveHelper(doc, JOptionPane.YES_NO_CANCEL_OPTION); }
    
    
    public boolean shouldRevertFile(OpenDefinitionsDocument doc) {
      String fname;
      if (! _model.getActiveDocument().equals(doc)) _model.setActiveDocument(doc);
      try {
        File file = doc.getFile();
        if (file == null) fname = "Untitled file";
        else fname = file.getName();
      }
      catch (FileMovedException fme) { fname = fme.getFile().getName(); }
      
      
      String text = fname + " has changed on disk. Would you like to reload it?\n" + 
        "This will discard any changes you have made.";
      int rc = JOptionPane.showConfirmDialog(MainFrame.this, text, fname + " Modified on Disk", 
                                             JOptionPane.YES_NO_OPTION);
      switch (rc) {
        case JOptionPane.YES_OPTION:
          return true;
        case JOptionPane.NO_OPTION:
          return false;
        case JOptionPane.CLOSED_OPTION:
        case JOptionPane.CANCEL_OPTION:
          return false;
        default:
          throw new RuntimeException("Invalid rc: " + rc);
      }
    }
    
    
    public void interactionIncomplete() { }
    
    
    
    public void projectBuildDirChanged() {
      if (_model.getBuildDirectory() != null) {
        _cleanAction.setEnabled(true);
      }
      else _cleanAction.setEnabled(false);
    }
    
    public void projectWorkDirChanged() { }
      
    public void projectModified() {

    }
    
    public void projectClosed() {
      Utilities.invokeAndWait(new Runnable() {
        public void run() {
          _model.getDocumentNavigator().asContainer().addKeyListener(_historyListener);
          _model.getDocumentNavigator().asContainer().addFocusListener(_focusListenerForRecentDocs);
          _model.getDocumentNavigator().asContainer().addMouseListener(_resetFindReplaceListener);

          removeTab(_junitErrorPanel);
        }
      });
    }
    
    public void projectOpened(File projectFile, FileOpenSelector files) {
      _setUpContextMenus();
      _recentProjectManager.updateOpenFiles(projectFile);
      open(files);
      _openProjectUpdate();
      _model.getDocumentNavigator().asContainer().addKeyListener(_historyListener);
      _model.getDocumentNavigator().asContainer().addFocusListener(_focusListenerForRecentDocs);
      _model.getDocumentNavigator().asContainer().addMouseListener(_resetFindReplaceListener);
      _model.refreshActiveDocument();
    }
    
    public void projectRunnableChanged() {
      if (_model.getMainClass() != null && _model.getMainClass().exists()) {
        _runProjectAction.setEnabled(true);
      }
      else _runProjectAction.setEnabled(false);
    }
    
    public void documentNotFound(OpenDefinitionsDocument d, File f) {
      
      _model.setProjectChanged(true);
     
      String text = "File " + f.getAbsolutePath() +
        "\ncould not be found on disk!  It was probably moved\n" +
        "or deleted.  Would you like to try to find it?";
      int rc = JOptionPane.showConfirmDialog(MainFrame.this, text, "File Moved or Deleted", JOptionPane.YES_NO_OPTION);
      if (rc == JOptionPane.NO_OPTION) return;
      if (rc == JOptionPane.YES_OPTION) {
        try {
          File[] opened = _openSelector.getFiles(); 
          d.setFile(opened[0]);
        } 
        catch(OperationCanceledException oce) {
          
        }
      }


    }
  }
  
  public JViewport getDefViewport() {
    OpenDefinitionsDocument doc = _model.getActiveDocument();

    JScrollPane defScroll = _defScrollPanes.get(doc);
    return defScroll.getViewport();
  }
  
  public void removeTab(final Component c) {
    Utilities.invokeLater(new Runnable() {
      public void run() {  
        _tabbedPane.remove(c);
        ((TabbedPanel)c).setDisplayed(false);
        _tabbedPane.setSelectedIndex(0);
        _currentDefPane.requestFocusInWindow();
      }
    });
  }
  
  
  public void showTab(final Component c) {
    
    
    Utilities.invokeLater(new Runnable() {
      public void run() {
        int numVisible = 0;
        if (c == _interactionsPane) _tabbedPane.setSelectedIndex(0);
        else if (c == _consolePane) _tabbedPane.setSelectedIndex(1);
        else {
          for (TabbedPanel tp: _tabs) {
            if (tp == c) {
              
              
              if (! tp.isDisplayed()) {
                _tabbedPane.insertTab(tp.getName(), null, tp, null, numVisible + 2);
                tp.setDisplayed(true);
              }
              _tabbedPane.setSelectedIndex(numVisible + 2);
              
              c.requestFocusInWindow();
              return;
            }
            if (tp.isDisplayed()) numVisible++;
          }
        }
      }
    });
  }
  
  
  
  
  private boolean _warnFileOpen(File f) {
    OpenDefinitionsDocument d = null;
    try { d = _model.getDocumentForFile(f); }
    catch(IOException ioe) {  }
    Object[] options = {"Yes","No"};
    if (d == null) return false;
    boolean dMod = d.isModifiedSinceSave();
    String msg = "This file is already open in DrJava" + (dMod ? " and has been modified" : "") + 
      ".  Do you wish to overwrite it?";
    int choice = JOptionPane.showOptionDialog(MainFrame.this, msg, "File Open Warning", JOptionPane.YES_NO_OPTION,
                                              JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    if (choice == JOptionPane.YES_OPTION) return _model.closeFileWithoutPrompt(d);
    return false;
  }
  
  
  private boolean _verifyOverwrite() {
    Object[] options = {"Yes","No"};
    int n = JOptionPane.showOptionDialog(MainFrame.this,
                                         "This file already exists.  Do you wish to overwrite the file?",
                                         "Confirm Overwrite",
                                         JOptionPane.YES_NO_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         options,
                                         options[1]);
    return (n == JOptionPane.YES_OPTION);
  }
  
  
  private void _junitInterrupted(final UnexpectedException e) {
    Utilities.invokeLater(new Runnable() {
      public void run() {
        _showJUnitInterrupted(e);
        removeTab(_junitErrorPanel);
        _model.refreshActiveDocument();
        
      }
    });
  }
      
  boolean inDebugMode() {
    Debugger dm = _model.getDebugger();
    return (dm.isAvailable()) && dm.isReady() && (_debugPanel != null);
  }
  
  
  FindReplaceDialog getFindReplaceDialog() { return _findReplace; }
  
  
  
  private void _setUpKeyBindingMaps() {
    final ActionMap _actionMap = _currentDefPane.getActionMap();
    
    KeyBindingManager.Singleton.put(KEY_BACKWARD, _actionMap.get(DefaultEditorKit.backwardAction),null, "Backward");
    KeyBindingManager.Singleton.addShiftAction(KEY_BACKWARD, DefaultEditorKit.selectionBackwardAction);
    
    KeyBindingManager.Singleton.put(KEY_BEGIN_DOCUMENT, _actionMap.get(DefaultEditorKit.beginAction), null, 
                                    "Begin Document");
    KeyBindingManager.Singleton.addShiftAction(KEY_BEGIN_DOCUMENT, DefaultEditorKit.selectionBeginAction);
    


    KeyBindingManager.Singleton.put(KEY_BEGIN_LINE, _beginLineAction, null, "Begin Line");


    KeyBindingManager.Singleton.addShiftAction(KEY_BEGIN_LINE, _selectionBeginLineAction);
    
    KeyBindingManager.Singleton.put(KEY_PREVIOUS_WORD, _actionMap.get(DefaultEditorKit.previousWordAction), null, 
                                    "Previous Word");
    KeyBindingManager.Singleton.addShiftAction(KEY_PREVIOUS_WORD, DefaultEditorKit.selectionPreviousWordAction);
    
    KeyBindingManager.Singleton.put(KEY_DOWN, _actionMap.get(DefaultEditorKit.downAction), null, "Down");
    KeyBindingManager.Singleton.addShiftAction(KEY_DOWN, DefaultEditorKit.selectionDownAction);
    
    KeyBindingManager.Singleton.put(KEY_END_DOCUMENT, _actionMap.get(DefaultEditorKit.endAction), null, "End Document");
    KeyBindingManager.Singleton.addShiftAction(KEY_END_DOCUMENT, DefaultEditorKit.selectionEndAction);
    
    KeyBindingManager.Singleton.put(KEY_END_LINE, _actionMap.get(DefaultEditorKit.endLineAction), null, "End Line");
    KeyBindingManager.Singleton.addShiftAction(KEY_END_LINE, DefaultEditorKit.selectionEndLineAction);
    
    KeyBindingManager.Singleton.put(KEY_NEXT_WORD, _actionMap.get(DefaultEditorKit.nextWordAction), null, "Next Word");
    KeyBindingManager.Singleton.addShiftAction(KEY_NEXT_WORD, DefaultEditorKit.selectionNextWordAction);
    
    KeyBindingManager.Singleton.put(KEY_FORWARD, _actionMap.get(DefaultEditorKit.forwardAction), null, "Forward");
    KeyBindingManager.Singleton.addShiftAction(KEY_FORWARD, DefaultEditorKit.selectionForwardAction);
    
    KeyBindingManager.Singleton.put(KEY_UP, _actionMap.get(DefaultEditorKit.upAction), null, "Up");
    KeyBindingManager.Singleton.addShiftAction(KEY_UP, DefaultEditorKit.selectionUpAction);
    
    
    KeyBindingManager.Singleton.put(KEY_PAGE_DOWN, _actionMap.get(DefaultEditorKit.pageDownAction), null, "Page Down");
    KeyBindingManager.Singleton.put(KEY_PAGE_UP, _actionMap.get(DefaultEditorKit.pageUpAction), null, "Page Up");
    KeyBindingManager.Singleton.put(KEY_CUT_LINE, _cutLineAction, null, "Cut Line");
    KeyBindingManager.Singleton.put(KEY_CLEAR_LINE, _clearLineAction, null, "Clear Line");
    KeyBindingManager.Singleton.put(KEY_SHIFT_DELETE_PREVIOUS, _actionMap.get(DefaultEditorKit.deletePrevCharAction), 
                                    null, "Delete Previous");
    KeyBindingManager.Singleton.put(KEY_SHIFT_DELETE_NEXT, _actionMap.get(DefaultEditorKit.deleteNextCharAction), 
                                    null, "Delete Next");
  }
  
  
  public void addComponentListenerToOpenDocumentsList(ComponentListener listener) {
    _docSplitPane.getLeftComponent().addComponentListener(listener);
  }
  
  
  public String getFileNameField() {
    return _fileNameField.getText();
  }
  
  
  public JMenu getEditMenu() {
    return _editMenu;
  }
  
  
  private class MainFontOptionListener implements OptionListener<Font> {
    public void optionChanged(OptionEvent<Font> oce) {
      _setMainFont();
    }
  }
  
  
  private class LineNumbersFontOptionListener implements OptionListener<Font> {
    public void optionChanged(OptionEvent<Font> oce) {
      _updateLineNums();
    }
  }
  
  
  private class DoclistFontOptionListener implements OptionListener<Font> {
    public void optionChanged(OptionEvent<Font> oce) {
      Font doclistFont = DrJava.getConfig().getSetting(FONT_DOCLIST);
      _model.getDocCollectionWidget().setFont(doclistFont);
    }
  }
  
  
  private class ToolbarFontOptionListener implements OptionListener<Font> {
    public void optionChanged(OptionEvent<Font> oce) {
      _updateToolbarButtons();
    }
  }
  
  
  private class NormalColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      _updateNormalColor();
    }
  }
  
  
  private class BackgroundColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      _updateBackgroundColor();
    }
  }
  
  
  private class ToolbarOptionListener implements OptionListener<Boolean> {
    public void optionChanged(OptionEvent<Boolean> oce) {
      _updateToolbarButtons();
    }
  }
  
  
  private class WorkingDirOptionListener implements OptionListener<File> {
    public void optionChanged(OptionEvent<File> oce) {
      _setCurrentDirectory(oce.value);
    }
  }
  
  
  private class LineEnumOptionListener implements OptionListener<Boolean> {
    public void optionChanged(OptionEvent<Boolean> oce) {
      _updateDefScrollRowHeader();
    }
  }
  
  
  private class QuitPromptOptionListener implements OptionListener<Boolean> {
    public void optionChanged(OptionEvent<Boolean> oce) {
      _promptBeforeQuit = oce.value.booleanValue();
    }
  }
  
  
  private class RecentFilesOptionListener implements OptionListener<Integer> {
    public void optionChanged(OptionEvent<Integer> oce) {
      _recentFileManager.updateMax(oce.value.intValue());
      _recentFileManager.numberItems();
      _recentProjectManager.updateMax(oce.value.intValue());
      _recentProjectManager.numberItems();
    }
  }
  
  private class LastFocusListener extends FocusAdapter {
    public void focusGained(FocusEvent e) {
      _lastFocusOwner = e.getComponent();
    }
  };
}
