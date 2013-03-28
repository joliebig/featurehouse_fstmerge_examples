package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;


public class ButtonTabComponent extends JPanel
{
   private final JTabbedPane _tabbedPane;
   private JLabel _label = new JLabel();
   private TabButton _button = new TabButton();

   public ButtonTabComponent(final JTabbedPane tabbedPane, String title, Icon icon)
   {
      setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      _tabbedPane = tabbedPane;
      setOpaque(false);

      _label.setText(title);
      _label.setIcon(icon);
      add(_label);
      
      _label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      
      add(_button);
      
      setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
   }

   JLabel getLabel()
   {
      return _label;
   }

   public JButton getButton()
   {
      return _button;
   }

   private class TabButton extends JButton 
   {
      public TabButton()
      {
         int size = 17;
         setPreferredSize(new Dimension(size, size));
         setToolTipText("close this tab");
         
         setUI(new BasicButtonUI());
         
         setContentAreaFilled(false);
         
         setFocusable(false);
         setBorder(BorderFactory.createEtchedBorder());
         setBorderPainted(false);
         
         
         addMouseListener(s_buttonMouseListener);
         setRolloverEnabled(true);
         
         
      }










      
      public void updateUI()
      {
      }

      
      protected void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g.create();
         
         if (getModel().isPressed())
         {
            g2.translate(1, 1);
         }
         g2.setStroke(new BasicStroke(2));
         g2.setColor(Color.BLACK);
         if (getModel().isRollover())
         {
            g2.setColor(Color.MAGENTA);
         }
         int delta = 6;
         g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
         g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
         g2.dispose();
      }
   }

   private final static MouseListener s_buttonMouseListener = new MouseAdapter()
   {
      public void mouseEntered(MouseEvent e)
      {
         Component component = e.getComponent();
         if (component instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) component;
            button.setBorderPainted(true);
         }
      }

      public void mouseExited(MouseEvent e)
      {
         Component component = e.getComponent();
         if (component instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) component;
            button.setBorderPainted(false);
         }
      }
   };
}

