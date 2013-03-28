
package org.jmol.util;

import java.util.Vector;

final public class CommandHistory {

  
  public final static String ERROR_FLAG = "#??";
  public final static String NOHISTORYLINE_FLAG = "#--";
  public final static String NOHISTORYATALL_FLAG = "#----";
  final static int DEFAULT_MAX_SIZE = 100;
  
  
  private Vector commandList = null;
  private int maxSize = DEFAULT_MAX_SIZE;

  
  private int nextCommand;

  
  private int cursorPos;


  
  public CommandHistory() {
    reset(DEFAULT_MAX_SIZE);
  }
  
  
  public CommandHistory(int maxSize) {
    reset(maxSize);
  }

  
  public void clear() {
    reset(maxSize);
  }

  
  public void reset(int maxSize) {
    this.maxSize = maxSize; 
    commandList = new Vector();
    nextCommand = 0;
    commandList.addElement("");
    cursorPos = 0;
  }

  
  public void setMaxSize(int maxSize) {
    if (maxSize == this.maxSize)
      return;
    if (maxSize < 2)
      maxSize = 2;
    while (nextCommand > maxSize) {
      commandList.removeElementAt(0);
      nextCommand--;
    }
    if (nextCommand > maxSize)
      nextCommand= maxSize - 1;
    cursorPos = nextCommand;
    this.maxSize = maxSize;
  }

  
  public String getCommandUp() {
    if (cursorPos <= 0)
      return null;
    cursorPos--;
    String str = getCommand();
    if (str.endsWith(ERROR_FLAG))
      removeCommand(cursorPos--);
    if (cursorPos < 0)
      cursorPos = 0;
    return str;
  }

  
  public String getCommandDown() {
    if (cursorPos >= nextCommand)
      return null;
    cursorPos++;
    
    return getCommand();
  }

  
  private String getCommand() {
    return (String)commandList.get(cursorPos);
  }

  
  public void addCommand(String strCommand) {
    if (!isOn && !strCommand.endsWith(ERROR_FLAG))
      return;
    if (strCommand.endsWith(NOHISTORYATALL_FLAG))
      return;
    int i;
    
    
    while ((i = strCommand.indexOf("\n")) >= 0) {
      String str = strCommand.substring(0, i);
      if (str.length() > 0)
        addCommandLine(str);
      strCommand = strCommand.substring(i + 1);
    }
    if (strCommand.length() > 0)
      addCommandLine(strCommand);
  }

  boolean isOn = true;

  
  public String getSetHistory(int n) {
    isOn = (n == -2 ? isOn : true);
    switch (n) {
    case 0:
      isOn = false;
      clear();
      return "";
    case Integer.MIN_VALUE:
    case -2:
      clear();
      return "";
    case -1:
      return getCommandUp();
    case 1:
      return getCommandDown();
    default:
      if (n < 0) {
        setMaxSize(-2 - n);
        return "";
      }
      n = Math.max(nextCommand - n, 0);
    }
    String str = "";
    for (int i = n; i < nextCommand; i++)
      str += commandList.get(i) + "\n";
    return str;
  }

  public String removeCommand() {
    return removeCommand(nextCommand - 1);
  }

  public String removeCommand(int n) {
    if (n < 0 || n >= nextCommand)
      return "";
    String str = (String) commandList.get(n);
    commandList.removeElementAt(n);
    nextCommand--;
    return str; 
  }
  
  
  private void addCommandLine(String command) {
    if (command == null || command.length() == 0)
      return;
    if (command.endsWith(NOHISTORYLINE_FLAG))
      return;
    if (nextCommand >= maxSize) {
      commandList.removeElementAt(0);
      nextCommand = maxSize - 1;
    }
    commandList.insertElementAt(command, nextCommand);
    nextCommand++;
    cursorPos = nextCommand;
    commandList.insertElementAt("", nextCommand);
    
    
  }
  
}
