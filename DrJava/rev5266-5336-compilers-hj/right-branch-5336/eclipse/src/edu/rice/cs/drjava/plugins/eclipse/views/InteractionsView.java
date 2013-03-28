

package edu.rice.cs.drjava.plugins.eclipse.views;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import edu.rice.cs.drjava.plugins.eclipse.EclipsePlugin;
import edu.rice.cs.drjava.plugins.eclipse.DrJavaConstants;
import edu.rice.cs.drjava.plugins.eclipse.repl.EclipseInteractionsModel;
import edu.rice.cs.drjava.plugins.eclipse.util.text.SWTDocumentAdapter;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class InteractionsView extends ViewPart {
  
  
  protected InteractionsController _controller;

  
  protected StyledText _styledText;

  
  protected Cursor _arrowCursor;

  
  protected Cursor _waitCursor;

  
  protected Runnable _beep;
  
  
  protected IToolBarManager _toolbar;

  
  protected IMenuManager _toolbarMenu;

  
  protected MenuManager _contextMenu;

  protected IActionBars _bars;

  
  public InteractionsView() {
    _beep = new Runnable() {
      public void run() {
        _styledText.getDisplay().beep();
      }
    };
  }

  
  public void dispose() {
    _arrowCursor.dispose();
    _waitCursor.dispose();
    _controller.dispose();
    _styledText.dispose();
    super.dispose();
  }

  
  public StyledText getTextPane() {
    return _styledText;
  }

  
  public Runnable getBeep() {
    return _beep;
  }

  
  public void setBeep(Runnable beep) {
    _beep = beep;
  }

  
  public void updateFont() {
    Font font = JFaceResources.getFont(DrJavaConstants.FONT_MAIN);
    _styledText.setFont(font);
  }


  
  public void createPartControl(Composite parent) {
    
    
    
    try { Class.forName("java.awt.Toolkit"); } catch (ClassNotFoundException e) {}

    setTextPane(new StyledText(parent, SWT.WRAP | SWT.V_SCROLL));

    

    _bars = getViewSite().getActionBars();
    

    _toolbar = _bars.getToolBarManager();
    _toolbarMenu = _bars.getMenuManager();
    _contextMenu = new MenuManager("#PopupMenu");
    Menu menu = _contextMenu.createContextMenu(_styledText);
    _styledText.setMenu(menu);

    SWTDocumentAdapter adapter = new SWTDocumentAdapter(_styledText);
    EclipseInteractionsModel model = new EclipseInteractionsModel(adapter);
    InteractionsController controller = new InteractionsController(model, adapter, this);
    setController(controller);
  }

  
  void setTextPane(StyledText text) {
    _styledText = text;
    _arrowCursor = new Cursor(_styledText.getDisplay(), SWT.CURSOR_ARROW);
    _waitCursor = new Cursor(_styledText.getDisplay(), SWT.CURSOR_WAIT);
  }

  
  void setController(InteractionsController controller) {
    _controller = controller;
  }

  
  public String getCurrentInteraction(int promptPos) {
    int length = _styledText.getText().length();
    return _styledText.getText(promptPos, length - 1);
  }

  
  public void setEditable(final boolean editable) {
    _styledText.getDisplay().syncExec(new Runnable() {
      public void run() {
        _styledText.setEditable(editable);
      }
    });
  }

  
  public void setBusyCursorShown(final boolean busy) {
    _styledText.getDisplay().syncExec(new Runnable() {
      public void run() {
        if (busy) {
          _styledText.setCursor(_waitCursor);
        }
        else {
          _styledText.setCursor(_arrowCursor);
        }
      }
    });
  }

  
  public void showInfoDialog(final String title, final String msg) {
    _styledText.getDisplay().asyncExec(new Runnable() {
      public void run() {
        MessageDialog.openInformation(_styledText.getShell(),
                                      title, msg);
      }
    });
  }

  
  public boolean showConfirmDialog(final String title, final String msg) {
    return MessageDialog.openQuestion(_styledText.getShell(),
                                      title, msg);
  }

  
  public void setFocus() {
    _styledText.setFocus();
  }

  
  public void addMenuItem(IAction action) {
    addToolbarMenuItem(action);
    addContextMenuItem(action);
  }
  
  public void addAction(String op, IAction action) {
   _bars.setGlobalActionHandler(op, action); 
   addToolbarMenuItem(action);
   addContextMenuItem(action);
  }
  
  
  public void addToolbarMenuItem(IAction action) {
    _toolbarMenu.add(action);
  }

  public void addSelectionListener(SelectionListener listener) { 
    _styledText.addSelectionListener(listener);
  }

  
  public void addContextMenuItem(IAction action) {
    _contextMenu.add(action);
  }
  
  
  public void addToolbarItem(IAction action) {
    _toolbar.add(action);
  }
         
}
