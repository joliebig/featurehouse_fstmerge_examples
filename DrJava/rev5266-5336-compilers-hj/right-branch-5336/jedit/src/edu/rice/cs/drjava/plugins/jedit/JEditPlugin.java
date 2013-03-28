

package edu.rice.cs.drjava.plugins.jedit;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.plugins.jedit.repl.*;
import edu.rice.cs.drjava.ui.*;
import edu.rice.cs.util.text.*;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.util.swing.ScrollableDialog;

public class JEditPlugin extends EBPlugin {
  
  private InteractionsController _controller;

  
  private InteractionsPane _pane;

  
  private JEditInteractionsModel _model;

  
  private SwingDocumentAdapter _doc;

  
  private JPopupMenu _popup;

  
  private View _view;

  
  private Action _resetInteractionsAction = new AbstractAction("Reset Interactions Pane") {
    public void actionPerformed(ActionEvent e) {
      resetInteractions();
    }
  };

  
  private Action _showClasspathAction = new AbstractAction("Show Interpreter Classpath") {
    public void actionPerformed(ActionEvent e) {
      showClasspath();
    }
  };

  
  private static JEditPlugin _default;

  
  public JEditPlugin() {
    _default = this;
  }

  
  public static JEditPlugin getDefault() {
    return _default;
  }

  
  public void start() {
    _popup = new JPopupMenu();
    _popup.add(_resetInteractionsAction);
    _popup.add(_showClasspathAction);
  }

  
  public void stop() {
    if (_model != null) {
      _model.dispose();
    }
  }

  
  public String getConsoleInput() {
    return _controller.getInputListener().getConsoleInput();
  }

  
  public JComponent newPane(View view) {
    _doc = new SwingDocumentAdapter();
    if (_model != null) {
      _model.dispose();
    }
    _model = new JEditInteractionsModel(_doc);
    _controller = new InteractionsController(_model, _doc);
    _pane = _controller.getPane();
    _model.addInteractionsListener(new JEditInteractionsListener());
    _view = view;
    _pane.addMouseListener(new RightClickMouseAdapter() {
      protected void _popupAction(MouseEvent e) {
        _popup.show(e.getComponent(), e.getX(), e.getY());
      }
    });
    return new BorderlessScrollPane(_pane);
  }

  
  public void resetInteractions() {
    if (!_model.getDocument().inProgress()) {
      String msg = "Are you sure you want to reset the Interactions Pane?";
      String title = "Confirm Reset Interactions";
      int result = JOptionPane.showConfirmDialog(_view, msg, title, JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        _model.resetInterpreter();
      }
    }
  }

  
  public void showClasspath() {
    String classpath = "";
    Vector<String> classpathElements = _model.getClasspath();
    for(int i = 0; i < classpathElements.size(); i++) {
      classpath += classpathElements.elementAt(i);
      if (i + 1 < classpathElements.size()) {
        classpath += "\n";
      }
    }

    new ScrollableDialog(_view, "Interactions Classpath",
                         "Current Interpreter Classpath", classpath).show();
  }

  
  public void handleMessage(EBMessage msg) {
    if (msg instanceof BufferUpdate) {
      BufferUpdate bu = (BufferUpdate)msg;
      Object w = bu.getWhat();
      if (w == BufferUpdate.CREATED || w == BufferUpdate.LOADED || w == BufferUpdate.SAVED) {
        _model.addToClassPath(((BufferUpdate)msg).getBuffer());
      }
    }
  }

  
  protected void _disableInteractionsPane() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        _pane.setEditable(false);
        _pane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      }
    });
  }

  
  protected void _enableInteractionsPane() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        _pane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        _pane.setEditable(true);
        _pane.setCaretPosition(_doc.getLength());
        if (_pane.hasFocus()) {
          _pane.getCaret().setVisible(true);
        }
      }
    });
  }

  
  class JEditInteractionsListener implements InteractionsListener {
    public void interactionStarted() {
      _disableInteractionsPane();
    }

    public void interactionEnded() {
      _enableInteractionsPane();
    }

    public void interactionErrorOccurred(final int offset, final int length) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          _pane.highlightError(offset, length);
        }
      });
    }

    public void interpreterResetting() {
      _disableInteractionsPane();
    }

    public void interpreterReady() {
      _enableInteractionsPane();
    }

    public void interpreterExited(final int status) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          String msg = "The interactions window was terminated by a call " +
            "to System.exit(" + status + ").\n" +
            "The interactions window will now be restarted.";
          String title = "Interactions terminated by System.exit(" + status + ")";
          JOptionPane.showMessageDialog(_view, msg, title, JOptionPane.INFORMATION_MESSAGE);
        }
      });
    }

    public void interpreterChanged(boolean inProgress) {
      if (inProgress) {
        _disableInteractionsPane();
      }
      else {
        _enableInteractionsPane();
      }
    }

    public void interpreterResetFailed(Throwable t) {
      String title = "Interactions Could Not Reset";
      String msg = "The interactions window could not be reset:\n" + t;
      JOptionPane.showMessageDialog(_view, msg, title, JOptionPane.INFORMATION_MESSAGE);
      interpreterReady();
    }

    public void interactionIncomplete() {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          int caretPos = _pane.getCaretPosition();
          _controller.getConsoleDoc().insertNewLine(caretPos);
        }
      });
    }
  }
}