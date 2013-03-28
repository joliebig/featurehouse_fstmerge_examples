

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

  
  protected void _close() {
    super._close();
    getModel().getJavadocModel().resetJavadocErrors();
    reset();
  }

  
  public void reset() {
    CompilerErrorModel em = getModel().getJavadocModel().getJavadocErrorModel();
    if (em != null) {
      _numErrors = em.getNumErrors();
    } else {
      _numErrors = 0;
    }
    _errorListPane.updateListPane(true);
  }

  
  public class JavadocErrorListPane extends ErrorPanel.ErrorListPane {




    
    public void setJavadocInProgress() {
      _errorListPositions = new Position[0];

      SwingDocument doc = new SwingDocument();
      doc.append("Generating Javadoc.  Please wait...\n", NORMAL_ATTRIBUTES);
      setDocument(doc);
      selectNothing();
    }

    
    protected void _updateWithErrors() throws BadLocationException {
      SwingDocument doc = new SwingDocument();
      String failureName = "error";
      if (getErrorModel().hasOnlyWarnings()) failureName = "warning";
      _updateWithErrors(failureName, "found", doc);
    }

    
    protected void _updateNoErrors(boolean done) throws BadLocationException {
      SwingDocument doc = new SwingDocument();
      String msg = (done) ? "Javadoc generated successfully." : "";
      doc.append(msg, NORMAL_ATTRIBUTES);
      setDocument(doc);
      selectNothing();
    }





  }

}
