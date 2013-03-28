

package edu.rice.cs.drjava.ui;

import javax.swing.text.*;
import java.awt.print.*;
import edu.rice.cs.drjava.model.*;


public class PreviewDefDocFrame extends PreviewFrame {
  
  private OpenDefinitionsDocument _document;
  
  
  public PreviewDefDocFrame(SingleDisplayModel model, MainFrame mainFrame) throws IllegalStateException {
    super(model, mainFrame, false);
  }
  
  
  protected Pageable setUpDocument(SingleDisplayModel model, boolean notUsed) {
    _document = model.getActiveDocument();
    return  _document.getPageable();
  }
  
  
  protected void _print() {
    try { _document.print(); }
    catch (FileMovedException fme) { _mainFrame._showFileMovedError(fme); }
    catch (PrinterException e) { _showError(e, "Print Error", "An error occured while printing."); }
    catch (BadLocationException e) { _showError(e, "Print Error", "An error occured while printing."); }
  }
}