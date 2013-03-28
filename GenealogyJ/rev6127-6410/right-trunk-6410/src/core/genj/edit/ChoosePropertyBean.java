
package genj.edit;

import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.util.Resources;
import genj.util.swing.NestedBlockLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ChoosePropertyBean extends JComponent {
  
  private final static Resources RESOURCES = Resources.get(ChoosePropertyBean.class);

  private JRadioButton rbChoose,rbCustom;
  private JTextField tfCustom;
  private JList lChoose;
  private JTextPane tpInfo;
  private List<ActionListener> listeners = new CopyOnWriteArrayList<ActionListener>();
  private Callback callback = new Callback();

  
  public ChoosePropertyBean(Property parent) {
    
    
    MetaProperty[] defs = parent.getNestedMetaProperties(MetaProperty.WHERE_NOT_HIDDEN | MetaProperty.WHERE_CARDINALITY_ALLOWS);
    init(defs, true);
  }    
  
  
  public ChoosePropertyBean(MetaProperty[] defs) {
    init(defs, false);
  }
  
  private void init(MetaProperty[] defs, boolean allowCustom) {
    
    Arrays.sort(defs, callback);
    
    
    setLayout(new NestedBlockLayout("<col><label1/><row><tags/><info gx=\"1\" gy=\"1\"/></row><label2/><tag/></col>"));

    
    rbChoose = new JRadioButton(RESOURCES.getString("choose.known"),defs.length>0);
    rbChoose.setEnabled(defs.length>0);
    rbChoose.addItemListener(callback);
    rbChoose.setAlignmentX(0);
    
    if (allowCustom)
      add(rbChoose);
    else
      add(new JLabel(RESOURCES.getString("choose.known")));
    
    
    lChoose = new JList(defs);
    lChoose.setVisibleRowCount(4);
    lChoose.setEnabled(defs.length>0);
    lChoose.setCellRenderer(new MetaDefRenderer());
    lChoose.addListSelectionListener(callback);
    lChoose.addMouseListener(callback);
    add(new JScrollPane(lChoose));

    
    tpInfo = new JTextPane();
    tpInfo.setText("");
    tpInfo.setEditable(false);
    tpInfo.setPreferredSize(new Dimension(256,256));
    add(new JScrollPane(tpInfo));

    
    rbCustom = new JRadioButton(RESOURCES.getString("choose.new"),defs.length==0);
    rbCustom.addItemListener(callback);
    rbCustom.setAlignmentX(0);
    if (allowCustom)
      add(rbCustom);

    ButtonGroup group = new ButtonGroup();
    group.add(rbChoose);
    group.add(rbCustom);

    
    tfCustom = new JTextField();
    tfCustom.setEnabled(defs.length==0);
    tfCustom.setAlignmentX(0);
    if (allowCustom)
      add(tfCustom);

    
    if (defs.length>0) 
      lChoose.setSelectedIndex(0);
    
    
  }
  
  
  public void setSingleSelection(boolean set) {
    lChoose.setSelectionMode(set ? ListSelectionModel.SINGLE_SELECTION : ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  }

  
  public String[] getSelectedTags() {

    String[] result = null;

    
    if (rbChoose.isSelected() == true) {
      Object[] objs = lChoose.getSelectedValues();
      result = new String[objs.length];
      for (int i=0;i<objs.length;i++) {
        result[i] = ((MetaProperty)objs[i]).getTag();
      }
      return result;
    }
    
    
    String tag = tfCustom.getText();
    return tag!=null ? new String[] { tag } : new String[0];
  }

  
  public void addActionListener(ActionListener listener) {
    listeners.add(listener);
  }

  
  public void removeActionListener(ActionListener listener) {
    listeners.remove(listener);
  }

  
  class MetaDefRenderer extends DefaultListCellRenderer implements ListCellRenderer {

    
    public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
      super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
        MetaProperty def = (MetaProperty)value;
        setText(def.getName()+" ("+def.getTag()+")");
        setIcon(def.getImage());
      return this;
    }

  } 
  
  
  private class Callback extends MouseAdapter implements ItemListener, ListSelectionListener, Comparator<MetaProperty>  {
    
    
    public int compare(MetaProperty m1, MetaProperty m2) {
      return m1.getName().compareTo(m2.getName());
    }
    
    
    public void mouseClicked(MouseEvent event) {
      if (event.getClickCount()>1) {
        ActionEvent e = new ActionEvent(ChoosePropertyBean.this, 0, null);
        for (ActionListener al : listeners) 
          al.actionPerformed(e);
      }
    }
    
    
    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == rbChoose) {
        lChoose.setEnabled(true);
        tfCustom.setEnabled(false);
        lChoose.requestFocusInWindow();
      }
      if (e.getSource() == rbCustom) {
        lChoose.clearSelection();
        lChoose.setEnabled(false);
        tfCustom.setEnabled(true);
        tfCustom.requestFocusInWindow();
      }
    }

    
    public void valueChanged(ListSelectionEvent e) {
    
      
      Object[] selection = lChoose.getSelectedValues();
    
      
      if ((selection==null)||(selection.length==0)) {
        tpInfo.setText("");
        return;
      }
    
      
      MetaProperty meta = (MetaProperty)selection[selection.length-1];
      tpInfo.setText(meta.getInfo());
      if (!rbChoose.isSelected())
        rbChoose.doClick();
    
      
    }
    
  }

} 

