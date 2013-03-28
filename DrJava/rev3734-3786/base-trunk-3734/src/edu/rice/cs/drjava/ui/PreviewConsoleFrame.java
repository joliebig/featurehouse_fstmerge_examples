

package edu.rice.cs.drjava.ui;

import java.awt.print.*;

import edu.rice.cs.drjava.model.*;

import edu.rice.cs.util.text.EditDocumentInterface;


public class PreviewConsoleFrame extends PreviewFrame {

  private EditDocumentInterface _document;

  
  public PreviewConsoleFrame(SingleDisplayModel model, MainFrame mainFrame, boolean interactions)
    throws IllegalStateException {
    super(model, mainFrame, interactions);
  }
  
  
   
  protected Pageable setUpDocument(SingleDisplayModel model, boolean interactions) {
    if (interactions) _document = model.getInteractionsDocument();
    else _document = model.getConsoleDocument();
    return _document.getPageable();
  }

  protected void _print() {
    try {
      _document.print();
    }
    catch (PrinterException e) {
      _showError(e, "Print Error", "An error occured while printing.");
    }
  }
}