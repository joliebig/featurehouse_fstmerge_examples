

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.drjava.model.compiler.CompilerInterface;
import edu.rice.cs.drjava.model.compiler.NoCompilerAvailable;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.SwingDocument;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;


public class CompilerErrorPanel extends ErrorPanel {
  
  
  private boolean _compileHasOccurred;
  private CompilerErrorListPane _errorListPane;
  private final JComboBox _compilerChoiceBox;
  
  
  private File[] _excludedFiles = new File[0];
  
  
  public CompilerErrorPanel(SingleDisplayModel model, MainFrame frame) {
    super(model, frame, "Compiler Output", "Compiler");
    _compileHasOccurred = false;
    _numErrors = 0;
    
    _errorListPane = new CompilerErrorListPane();
    setErrorListPane(_errorListPane);
    
    
    
    
    
    
    
    _compilerChoiceBox =
      new JComboBox(getModel().getCompilerModel().getAvailableCompilers());
    _compilerChoiceBox.setEditable(false);
    _compilerChoiceBox.setSelectedItem
      (getModel().getCompilerModel().getActiveCompiler());
    _compilerChoiceBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        CompilerInterface compiler = (CompilerInterface)
          _compilerChoiceBox.getSelectedItem();
        if (compiler != null) {
          getModel().getCompilerModel().setActiveCompiler(compiler);
        }
        else {
          getModel().getCompilerModel()
            .setActiveCompiler(NoCompilerAvailable.ONLY);
        }
        getModel().getCompilerModel().resetCompilerErrors();
        _compileHasOccurred = false;
        reset();
      }
    });
    
    customPanel.add(_compilerChoiceBox, BorderLayout.NORTH);
    
    DrJava.getConfig().addOptionListener(OptionConstants.JAVAC_LOCATION, new CompilerLocationOptionListener<File>());
    DrJava.getConfig().addOptionListener(OptionConstants.EXTRA_COMPILERS, new CompilerLocationOptionListener<Vector<String>>());
  }
  
  
  
  private class CompilerLocationOptionListener<T> implements OptionListener<T> {
    
    public void optionChanged(OptionEvent<T> oce) {
      _compilerChoiceBox.removeAllItems();
      CompilerInterface[] availCompilers = getModel().getCompilerModel().getAvailableCompilers();
      for (int i=0; i<availCompilers.length; i++) {
        _compilerChoiceBox.addItem(availCompilers[i]);
      }
    }
  }
  
  
  public CompilerErrorListPane getErrorListPane() { return _errorListPane; }
  
  
  public void setCompilationInProgress() {
    _errorListPane.setCompilationInProgress();
  }
  
  protected CompilerErrorModel getErrorModel() {
    return getModel().getCompilerModel().getCompilerErrorModel();
  }
  
  
  protected void _close() {
    super._close();
    getModel().getCompilerModel().resetCompilerErrors();
    reset();
  }
  
  
  public void reset(File[] excludedFiles) {
    _excludedFiles = excludedFiles;
    reset();
  }
  
  
  public void reset() {
    
    

    _numErrors = getModel().getCompilerModel().getNumErrors();
    
    _errorListPane.updateListPane(true);
    
    
  }
  
  class CompilerErrorListPane extends ErrorPanel.ErrorListPane {
    
    protected void _updateWithErrors() throws BadLocationException {
      SwingDocument doc = new SwingDocument();
      if (_excludedFiles.length != 0) {
        StringBuffer msgBuffer = 
          new StringBuffer("Compilation completed.  The following files were not compiled:\n");
        for (File f: _excludedFiles) {
          if (f!=null) { msgBuffer.append("  ").append(f).append('\n'); } 
        }
        doc.insertString(0, msgBuffer.toString(), NORMAL_ATTRIBUTES);
      }

      String failureName = "error";
      if (getErrorModel().hasOnlyWarnings()) failureName = "warning";

      _updateWithErrors(failureName, "found", doc);
    }
    
    
    public void setCompilationInProgress() {
      _errorListPositions = new Position[0];
      _compileHasOccurred = true;
      
      SwingDocument doc = new SwingDocument();
       
      try {
        doc.insertString(0, "Compilation in progress, please wait...", NORMAL_ATTRIBUTES);
      }
      catch (BadLocationException ble) { throw new UnexpectedException(ble); }
      
      setDocument(doc);
      selectNothing();
    }
    
    
    protected void _updateNoErrors(boolean done) throws BadLocationException {
      SwingDocument doc = new SwingDocument();
      String message;
      if (_compileHasOccurred) {
        if (_excludedFiles.length == 0) message = "Compilation completed.";
        else {
          StringBuffer msgBuffer = 
            new StringBuffer("Compilation completed.  The following files were not compiled:\n");
          for (File f: _excludedFiles) {
            if (f!=null) { msgBuffer.append("  ").append(f).append('\n'); } 
          }
          message = msgBuffer.toString();
        }
      }
      else if (getModel().getCompilerModel().getAvailableCompilers().length == 0)
        message = "No compiler is available.  Please specify one in\nthe Preferences dialog in the Edit menu.";
      else if (getModel().getCompilerModel().getActiveCompiler() == NoCompilerAvailable.ONLY)
        message = "No compiler available.";
      else 
        message = getModel().getCompilerModel().getActiveCompiler().getName() + " compiler ready.";
      
      doc.insertString(0, message, NORMAL_ATTRIBUTES);
      setDocument(doc);
      _updateScrollButtons();
      selectNothing();
    }
  }
}
