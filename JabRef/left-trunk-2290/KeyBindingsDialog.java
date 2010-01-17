

package net.sf.jabref;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;


class KeyBindingsDialog
    extends JDialog {
  KeystrokeTable table;
  KeystrokeTableModel tableModel;
  
  JTextField keyTF = new JTextField();
  JButton ok, cancel, grabB, defB;
  HashMap<String, String> bindHM, defBinds;
  boolean clickedSave = false;
  int selectedRow = -1;
  boolean getAction() {
    return clickedSave;
  }

  HashMap<String, String> getNewKeyBindings() {
    return bindHM;
  }

  public KeyBindingsDialog(HashMap<String, String> name2binding, HashMap<String, String> defBinds) {
    super();
    this.defBinds = defBinds;
    setTitle(Globals.lang("Key bindings"));
    setModal(true); 
    getContentPane().setLayout(new BorderLayout());
    bindHM = name2binding;
    setList();
    
    JScrollPane listScroller = new JScrollPane(table);
    listScroller.setPreferredSize(new Dimension(250, 400));
    getContentPane().add(listScroller, BorderLayout.CENTER);

    Box buttonBox = new Box(BoxLayout.X_AXIS);
    ok = new JButton(Globals.lang("Ok"));
    cancel = new JButton(Globals.lang("Cancel"));
    grabB = new JButton(Globals.lang("Grab"));
    defB = new JButton(Globals.lang("Default"));
    grabB.addKeyListener(new JBM_CustomKeyBindingsListener());
    
    buttonBox.add(grabB);
    buttonBox.add(defB);
    buttonBox.add(ok);
    buttonBox.add(cancel);

    getContentPane().add(buttonBox, BorderLayout.SOUTH);
    
    setButtons();
    keyTF.setEditable(false);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        clickedSave = false;
        dispose();
      }
    });
  }

  void setTop() {
    Box topBox = new Box(BoxLayout.X_AXIS);

    topBox.add(new JLabel(Globals.lang("Binding") + ":", JLabel.RIGHT));
    topBox.add(keyTF);
    getContentPane().add(topBox, BorderLayout.NORTH);

  }

  
  
  
  public class JBM_CustomKeyBindingsListener
      extends KeyAdapter {
    public void keyPressed(KeyEvent evt) {
      
      int selRow = table.getSelectedRow();
      if (selRow < 0)
        return;
      
      
      
      
      

      String code = KeyEvent.getKeyText(evt.getKeyCode());
      String mod = KeyEvent.getKeyModifiersText(evt.getModifiers());
      

      if (mod.equals("")) {
        int kc = evt.getKeyCode();
        if ( (kc < KeyEvent.VK_F1) && (kc > KeyEvent.VK_F12) &&
            (kc != KeyEvent.VK_ESCAPE) && (kc != KeyEvent.VK_DELETE)) {
          return; 
        }
      }
      
      
      
      if ( 
          code.equals("Tab")
          || code.equals("Backspace")
          || code.equals("Enter")
          
          || code.equals("Space")
          || code.equals("Ctrl")
          || code.equals("Shift")
          || code.equals("Alt")) {
        return;
      }
      
      String newKey;
      if (!mod.equals("")) {
        newKey = mod.toLowerCase().replaceAll("\\+"," ") + " " + code;
      }
      else {
        newKey = code;
      }
      keyTF.setText(newKey);
      
      String selectedFunction = (String) table.getOriginalName(selRow);
      table.setValueAt(newKey, selRow, 1);
      table.revalidate();
      table.repaint();
      
      
      
      
      bindHM.put(selectedFunction, newKey);
      
    }
  }

  
  
  
  class MyListSelectionListener
      implements ListSelectionListener {
    
    public void valueChanged(ListSelectionEvent evt) {
      
      
      if (!evt.getValueIsAdjusting()) {
        JList list = (JList) evt.getSource();

        
        Object[] selected = list.getSelectedValues();

        
        for (int i = 0; i < selected.length; i++) {
          Object sel = selected[i];
          keyTF.setText( bindHM.get(sel));
        }
      }
    }
  }

  

  
  void setList() {

    Iterator<String> it = bindHM.keySet().iterator();
    String[][] tableData = new String[bindHM.size()][3];
    int i=0;
    while (it.hasNext()) {
      String s = it.next();
      tableData[i][2] = s;
      tableData[i][1] = bindHM.get(s);
      tableData[i][0] = Globals.lang(s);
      i++;
      
   }
   TreeMap<String, String[]> sorted = new TreeMap<String, String[]>();
   for (i=0; i<tableData.length; i++)
     sorted.put(tableData[i][0], tableData[i]);

    tableModel = new KeystrokeTableModel(sorted);
    table = new KeystrokeTable(tableModel);
    
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(GUIGlobals.KEYBIND_COL_0);
    cm.getColumn(1).setPreferredWidth(GUIGlobals.KEYBIND_COL_1);
    table.setRowSelectionInterval(0, 0); 
  }

  class KeystrokeTable extends JTable {
    public KeystrokeTable(KeystrokeTableModel model) { super(model); }
     public boolean isCellEditable(int row, int col) { return false; }
     public String getOriginalName(int row) { return ((KeystrokeTableModel)getModel()).data[row][2]; }
   }

    class KeystrokeTableModel extends AbstractTableModel {
      String[][] data;
      
      public KeystrokeTableModel(TreeMap<String, String[]> sorted) {
        data = new String[sorted.size()][3];
        Iterator<String> i = sorted.keySet().iterator();
        int row = 0;
        while (i.hasNext()) {
          data[row++] = sorted.get(i.next());
        }
        
        
      }
      public boolean isCellEditable(int row, int col) { return false; }
      public String getColumnName(int col) {
        return (col==0 ? Globals.lang("Action") : Globals.lang("Shortcut"));
      }
      public int getColumnCount() {
        return 2;
      }

      public int getRowCount() {
        return data.length;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        
        return data[rowIndex][columnIndex];
        
        
      }
      public void setValueAt(Object o, int row, int col) {
        data[row][col] = (String)o;
      }
    }

  
  void setButtons() {
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        dispose();
        clickedSave = true;
        
      }
    });
    cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
        clickedSave = false;
        
      }
    });
    defB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
      }
    });

  }

  String setToDefault(String name) {
    String defKey = defBinds.get(name);
    bindHM.put(name, defKey);
    return defKey;
  }

  
}
