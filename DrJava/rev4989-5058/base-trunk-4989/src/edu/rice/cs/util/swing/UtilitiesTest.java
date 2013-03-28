

package edu.rice.cs.util.swing;

import java.awt.EventQueue;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.StringOps;

import edu.rice.cs.drjava.ui.DrJavaErrorHandler;

import junit.framework.*;

public class UtilitiesTest extends TestCase {
  public void testClearEventQueue() {
    final int[] count = new int[] { 0 };
    final int N = 10;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        System.out.println("Runnable 0");
        ++count[0];
        try {
          Thread.sleep(1000);
        }
        catch(InterruptedException ie) { }
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            System.out.println("Runnable 2");
            ++count[0];
          }
        });          
      }
    });
    for(int i=1; i<N; ++i) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          System.out.println("Runnable 1");
          ++count[0];
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              System.out.println("Runnable 2");
              ++count[0];
            }
          });          
        }
      });
    }
    System.out.println("Before clearEventQueue");
    Utilities.clearEventQueue(true);
    System.out.println("After clearEventQueue");
    assertEquals(2*N, count[0]);
  }
}
