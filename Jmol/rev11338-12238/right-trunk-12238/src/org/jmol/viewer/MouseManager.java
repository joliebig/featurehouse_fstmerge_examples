
package org.jmol.viewer;

import java.awt.Event;

import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.viewer.binding.Binding;

import java.awt.event.*;
import java.awt.Component;

public abstract class MouseManager implements KeyListener {

  protected Viewer viewer;
  private ActionManager actionManager;

  abstract boolean handleOldJvm10Event(Event e);

  MouseManager(Component display, Viewer viewer, ActionManager actionManager) {
    this.viewer = viewer;
    this.actionManager = actionManager;
    display.addKeyListener(this);
  }

  void clear() {
    
  }

  void removeMouseListeners11() {
  }

  void removeMouseListeners14() {
  }

  void dispose() {
    Component display = viewer.getDisplay();
    if (display == null)
      return;
    actionManager.dispose();
    removeMouseListeners11();
    removeMouseListeners14();
    display.removeKeyListener(this);
  }

  private String keyBuffer = "";

  private void clearKeyBuffer() {
    if (keyBuffer.length() == 0)
      return;
    keyBuffer = "";
    if (viewer.getBooleanProperty("showKeyStrokes"))
      viewer
          .evalStringQuiet("!set echo _KEYSTROKES; set echo bottom left;echo \"\"");
  }

  private void addKeyBuffer(char ch) {
    if (ch == 10) {
      sendKeyBuffer();
      return;
    }
    if (ch == 8) {
      if (keyBuffer.length() > 0)
        keyBuffer = keyBuffer.substring(0, keyBuffer.length() - 1);
    } else {
      keyBuffer += ch;
    }
    if (viewer.getBooleanProperty("showKeyStrokes"))
      viewer
          .evalStringQuiet("!set echo _KEYSTROKES; set echo bottom left;echo "
              + Escape.escape("\0" + keyBuffer));
  }

  private void sendKeyBuffer() {
    String kb = keyBuffer;
    if (viewer.getBooleanProperty("showKeyStrokes"))
      viewer
          .evalStringQuiet("!set echo _KEYSTROKES; set echo bottom left;echo "
              + Escape.escape(keyBuffer));
    clearKeyBuffer();
    viewer.script(kb);
  }

  public void keyTyped(KeyEvent ke) {
    if (Logger.debugging)
      Logger.debug("ActionmManager keyTyped: " + ke.getKeyCode());
    ke.consume();
    if (viewer.getDisablePopupMenu())
      return;
    char ch = ke.getKeyChar();
    int modifiers = ke.getModifiers() & Binding.CTRL_ALT;
    
    
    if (modifiers != 0) {
      switch (ch) {
      case (char) 11:
      case 'k':
        boolean isON = !viewer.getBooleanProperty("allowKeyStrokes");
        switch (modifiers) {
        case Binding.CTRL:
          viewer.setBooleanProperty("allowKeyStrokes", isON);
          viewer.setBooleanProperty("showKeyStrokes", true);
          break;
        case Binding.CTRL_ALT:
        case Binding.ALT:
          viewer.setBooleanProperty("allowKeyStrokes", isON);
          viewer.setBooleanProperty("showKeyStrokes", false);
          break;
        }
        clearKeyBuffer();
        viewer.refresh(3, "showkey");
      }
      return;
    }
    if (!viewer.getBooleanProperty("allowKeyStrokes"))
      return;
    addKeyBuffer(ch);
  }

  public void keyPressed(KeyEvent ke) {
    actionManager.keyPressed(ke);
  }

  public void keyReleased(KeyEvent ke) {
    actionManager.keyReleased(ke);
  }

  void mouseEntered(long time, int x, int y) {
    actionManager.mouseEntered(time, x, y);
  }

  void mouseExited(long time, int x, int y) {
    actionManager.mouseExited(time, x, y);
  }

  void setMouseMode() {
    clearKeyBuffer();
    actionManager.setMouseMode();
  }

  void mouseClicked(long time, int x, int y, int modifiers, int clickCount) {
    clearKeyBuffer();
    
    
    actionManager.mouseClicked(time, x, y, modifiers, 1);
  }

  protected void mouseMoved(long time, int x, int y, int modifiers) {
    clearKeyBuffer();
    actionManager.mouseMoved(time, x, y, modifiers);
  }

  void mouseWheel(long time, int rotation, int modifiers) {
    clearKeyBuffer();
    actionManager.mouseWheel(time, rotation, modifiers);
  }

  void mousePressed(long time, int x, int y, int modifiers,
                    boolean isPopupTrigger) {
    clearKeyBuffer();
    actionManager.mousePressed(time, x, y, modifiers);
  }

  void mouseReleased(long time, int x, int y, int modifiers) {
    actionManager.mouseReleased(time, x, y, modifiers);
  }

  void mouseDragged(long time, int x, int y, int modifiers) {
    actionManager.mouseDragged(time, x, y, modifiers);
  }

}
