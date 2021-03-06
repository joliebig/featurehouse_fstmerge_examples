

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.util.text.SwingDocument;

import javax.swing.text.*;


public class JavadocErrorPanel extends ErrorPanel {

  protected JavadocErrorListPane _errorListPane;

  
  public JavadocErrorPanel(SingleDisplayModel model, MainFrame frame) {
    super(model, frame, "Javadoc Output", "Javadoc");

    _errorListPane = new JavadocErrorListPane();
    setErrorListPane(_errorListPane);
  }

  
  public JavadocErrorListPane getErrorListPane() {
    return _errorListPane;
  }

  protected CompilerErrorModel getErrorModel() {
    return getModel().getJavadocModel().getJavadocErrorModel();
  }

  
  public void setJavadocInProgress() {
    _errorListPane.setJavadocInProgress();
  }

  
  @Override
  protected void _close() {
    super._close();
    getModel().getJavadocModel().resetJavadocErrors();
    reset();
  }

  
  public void reset() {
    CompilerErrorModel model = getModel().getJavadocModel().getJavadocErrorModel();
    if (model != null) _numErrors = model.getNumErrors();
    else _numErrors = 0;

    _errorListPane.updateListPane(true);
  }

  
  public class JavadocErrorListPane extends ErrorPanel.ErrorListPane {
    protected boolean _wasSuccessful = false;
    




    
    public void setJavadocInProgress() {
      _errorListPositions = new Position[0];

      SwingDocument doc = new SwingDocument();
      doc.append("Generating Javadoc.  Please wait...\n", NORMAL_ATTRIBUTES);
      setDocument(doc);
      selectNothing();
      _wasSuccessful = false;
    }
    
    public void setJavadocEnded(boolean success) { _wasSuccessful = success; }

    
    protected void _updateWithErrors() throws BadLocationException {
      SwingDocument doc = new SwingDocument();
      String failureName = "error";
      if (getErrorModel().hasOnlyWarnings()) failureName = "warning";
      _updateWithErrors(failureName, "found", doc);
    }

    
    protected void _updateNoErrors(boolean done) throws BadLocationException {
      SwingDocument doc = new SwingDocument();
      String msg = "";
      if (done) {
        if (_wasSuccessful) msg = "Javadoc generated successfully.";
        else msg = "Javadoc generation failed.";
      }
      doc.append(msg, NORMAL_ATTRIBUTES);
      setDocument(doc);
      selectNothing();
    }





  }

}
