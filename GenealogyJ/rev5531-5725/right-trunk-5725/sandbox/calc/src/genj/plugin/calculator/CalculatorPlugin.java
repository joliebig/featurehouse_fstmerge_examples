

package genj.plugin.calculator;

import genj.app.ExtendMenubar;
import genj.plugin.ExtensionPoint;
import genj.plugin.Plugin;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.view.ExtendContextMenu;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class CalculatorPlugin implements Plugin {
  
  private final ImageIcon IMG = new ImageIcon(this, "/Calc.gif");
  
  private final Resources RESOURCES = Resources.get(this);
  
  
  public void extend(ExtensionPoint ep) {
    
    
    if (ep instanceof ExtendContextMenu) {
      
      ((ExtendContextMenu)ep).getContext().addAction(new Calc());
      }
    
    if (ep instanceof ExtendMenubar) {
      ExtendMenubar em = (ExtendMenubar)ep;
      em.addAction(ExtendMenubar.TOOLS_MENU, new Calc());
    }
    
  }

  
  private class Calc extends Action2 {
    Calc() {
      setText(RESOURCES.getString("action.calculator"));
      setImage(IMG);
    }
    protected void execute() {

    	if (!WindowManager.getInstance(getTarget()).show("cal"))
      WindowManager.getInstance(getTarget()).openWindow("cal", "Calculatrice", IMG, new Calculator());
      
    }
  } 
    

    
} 


class CalculatorPanel extends JPanel implements ActionListener {
  public CalculatorPanel() {
    setLayout(new BorderLayout());

    display = new JTextField("0");
    display.setEditable(false);
    add(display, "North");

    JPanel p = new JPanel();
    p.setLayout(new GridLayout(5, 4));
    String buttons = "CE()789/456*123-0.=+";
    for (int i = 0; i < buttons.length(); i++)
      addButton(p, buttons.substring(i, i + 1));
    add(p, "Center");
  }

  private void addButton(Container c, String s) {
    JButton b = new JButton(s);
    c.add(b);
    b.addActionListener(this);
  }

  public void actionPerformed(ActionEvent evt) {
    String s = evt.getActionCommand();
    if ('0' <= s.charAt(0) && s.charAt(0) <= '9' || s.equals(".")) {
      if (start)
        display.setText(s);
      else
        display.setText(display.getText() + s);
      start = false;
    } else if (s.equals("E")) {
        display.setText("0");
        start = true;
    } else if (s.equals("C")) {
        op = "=";
        calculate(0);
    start = true;
    } else if (false && start && s.equals("-")) {
          display.setText(s);
          start = false;
      } else {
        calculate(Double.parseDouble(display.getText()));
        op = s;
        start = true;
      
    }
  }

  public void calculate(double n) {
    if (op.equals("+"))
      arg += n;
    else if (op.equals("-"))
      arg -= n;
    else if (op.equals("*"))
      arg *= n;
    else if (op.equals("/"))
      arg /= n;
    else if (op.equals("="))
      arg = n;
    display.setText("" + arg);
  }

  private JTextField display;

  private double arg = 0;

  private String op = "=";

  private boolean start = true;
}

class CalculatorFrame extends JFrame {
  public CalculatorFrame() {
    setTitle("Calculator");
    setSize(200, 200);

    Container contentPane = getContentPane();
    contentPane.add(new CalculatorPanel());
  }
}
