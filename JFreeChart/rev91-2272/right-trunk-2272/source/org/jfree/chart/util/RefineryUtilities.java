

package org.jfree.chart.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


public class RefineryUtilities {

    private RefineryUtilities() {
    }

    
    public static Point getCenterPoint ()
    {
      GraphicsEnvironment localGraphicsEnvironment
              = GraphicsEnvironment.getLocalGraphicsEnvironment();
      try {
          Method method = GraphicsEnvironment.class.getMethod("getCenterPoint",
                  (Class[]) null);
          return (Point) method.invoke(localGraphicsEnvironment,
                  (Object[]) null);
      }
      catch (Exception e) {
        
      }

      Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
      return new Point (s.width / 2, s.height / 2);
    }

  
    public static Rectangle getMaximumWindowBounds() {
      GraphicsEnvironment localGraphicsEnvironment
              = GraphicsEnvironment.getLocalGraphicsEnvironment();
      try {
          Method method = GraphicsEnvironment.class.getMethod(
                  "getMaximumWindowBounds", (Class[]) null);
          return (Rectangle) method.invoke(localGraphicsEnvironment,
                  (Object[]) null);
      }
      catch (Exception e) {
          
      }

      Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
      return new Rectangle (0, 0, s.width, s.height);
    }

    
    public static void centerFrameOnScreen(final Window frame) {
        positionFrameOnScreen(frame, 0.5, 0.5);
    }

    
    public static void positionFrameOnScreen(Window frame,
                                             double horizontalPercent,
                                             double verticalPercent) {

        Rectangle s = getMaximumWindowBounds();
        Dimension f = frame.getSize();
        int w = Math.max(s.width - f.width, 0);
        int h = Math.max(s.height - f.height, 0);
        int x = (int) (horizontalPercent * w) + s.x;
        int y = (int) (verticalPercent * h) + s.y;
        frame.setBounds(x, y, f.width, f.height);

    }

    
    public static void positionFrameRandomly(Window frame) {
        positionFrameOnScreen(frame, Math.random(), Math.random());
    }

    
    public static void centerDialogInParent(Dialog dialog) {
        positionDialogRelativeToParent(dialog, 0.5, 0.5);
    }

    
    public static void positionDialogRelativeToParent(Dialog dialog,
                                                      double horizontalPercent,
                                                      double verticalPercent) {
        Dimension d = dialog.getSize();
        Container parent = dialog.getParent();
        Dimension p = parent.getSize();

        int baseX = parent.getX() - d.width;
        int baseY = parent.getY() - d.height;
        int w = d.width + p.width;
        int h = d.height + p.height;
        int x = baseX + (int) (horizontalPercent * w);
        int y = baseY + (int) (verticalPercent * h);

        
        Rectangle s = getMaximumWindowBounds();
        x = Math.min(x, (s.width - d.width));
        x = Math.max(x, 0);
        y = Math.min(y, (s.height - d.height));
        y = Math.max(y, 0);

        dialog.setBounds(x + s.x, y + s.y, d.width, d.height);

    }

    
    public static JPanel createTablePanel(TableModel model) {

        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(model);
        for (int columnIndex = 0; columnIndex < model.getColumnCount();
                columnIndex++) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            Class c = model.getColumnClass(columnIndex);
            if (c.equals(Number.class)) {
                column.setCellRenderer(new NumberCellRenderer());
            }
        }
        panel.add(new JScrollPane(table));
        return panel;

    }

    
    public static JLabel createJLabel(String text, Font font) {

        JLabel result = new JLabel(text);
        result.setFont(font);
        return result;

    }

    
    public static JLabel createJLabel(String text, Font font, Color color) {

        JLabel result = new JLabel(text);
        result.setFont(font);
        result.setForeground(color);
        return result;

    }

    
    public static JButton createJButton(String label, Font font) {

        JButton result = new JButton(label);
        result.setFont(font);
        return result;

    }

}


