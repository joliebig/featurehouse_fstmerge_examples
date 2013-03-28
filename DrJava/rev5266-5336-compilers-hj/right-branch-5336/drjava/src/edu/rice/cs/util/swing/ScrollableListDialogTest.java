

package edu.rice.cs.util.swing;

import junit.framework.TestCase;

import java.util.Arrays;

import javax.swing.JOptionPane;


public class ScrollableListDialogTest extends TestCase {
  private static final String TITLE = "DIALOG TITLE";
  private static final String LEADER = "DIALOG LEADER";
  private static final java.util.List<String> DATA = Arrays.asList("hello", "there");
  
  
  public void testValidMessageTypes() {
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).setMessageType(JOptionPane.ERROR_MESSAGE).build();
    } catch (IllegalArgumentException e) {
      fail("JOptionPane.ERROR_MESSAGE is a valid message type");
    }
    
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).setMessageType(JOptionPane.INFORMATION_MESSAGE).build();
    } catch (IllegalArgumentException e) {
      fail("JOptionPane.INFORMATION_MESSAGE is a valid message type");
    }
    
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).setMessageType(JOptionPane.WARNING_MESSAGE).build();
    } catch (IllegalArgumentException e) {
      fail("JOptionPane.WARNING_MESSAGE is a valid message type");
    }
    
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).setMessageType(JOptionPane.QUESTION_MESSAGE).build();
    } catch (IllegalArgumentException e) {
      fail("JOptionPane.QUESTION_MESSAGE is a valid message type");
    }
    
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).setMessageType(JOptionPane.PLAIN_MESSAGE).build();
    } catch (IllegalArgumentException e) {
      fail("JOptionPane.PLAIN_MESSAGE is a valid message type");
    }
    
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).setMessageType(-128).build();
      fail("-128 is not a valid message type");
    } catch (IllegalArgumentException e) {
      
    }
    
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).setMessageType(42).build();
      fail("42 is not a valid message type");
    } catch (IllegalArgumentException e) {
      
    }
  }
  
  public void testNullData() {
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(DATA).build();
    } catch (IllegalArgumentException e) {
      fail("Data was non-null, so it should have been accepted.");
    }
    
    try {
      new  ScrollableListDialog.Builder<String>()
        .setOwner(null).setTitle(TITLE).setText(LEADER).setItems(null).build();
      fail("Data was null, so it shouldn't have been accepted.");
    } catch (IllegalArgumentException e) {
      
    }
  }
}
