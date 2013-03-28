
package genj.edit;

import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.util.GridBagHelper;
import genj.util.Resources;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ChoosePropertyBean extends JComponent {

  private JRadioButton rbChoose,rbNew;
  private JTextField tfNew;
  private JList lChoose;
  private JScrollPane spInfo;
  private JTextPane tpInfo;
  private Property parent;
  private List listeners = new ArrayList();
  private Callback callback = new Callback();

  
  public ChoosePropertyBean(Property pArent, Resources resources) {
    
    
    parent = pArent;
    MetaProperty[] defs = parent.getNestedMetaProperties(MetaProperty.WHERE_NOT_HIDDEN | MetaProperty.WHERE_CARDINALITY_ALLOWS);
    Arrays.sort(defs, callback);
        
    
    GridBagHelper gh = new GridBagHelper(this);

    
    rbChoose = new JRadioButton(resources.getString("choose.known"),defs.length>0);
    rbChoose.setEnabled(defs.length>0);
    rbChoose.addItemListener(callback);
    rbChoose.setAlignmentX(0);
    gh.add(rbChoose,1,1,2,1, GridBagHelper.GROWFILL_HORIZONTAL);

    
    lChoose = new JList(defs);
    lChoose.setVisibleRowCount(4);
    lChoose.setEnabled(defs.length>0);
    lChoose.setCellRenderer(new MetaDefRenderer());
    lChoose.addListSelectionListener(callback);
    lChoose.addMouseListener(callback);
    JScrollPane sp = new JScrollPane(lChoose);
    
    sp.setMinimumSize(sp.getPreferredSize());
    gh.add(sp,1,2,1,1,GridBagHelper.GROWFILL_VERTICAL);

    
    tpInfo = new JTextPane();
    tpInfo.setText("");
    tpInfo.setEditable(false);
    spInfo = new JScrollPane(tpInfo);
    gh.add(spInfo,2,2,1,1,GridBagHelper.GROWFILL_BOTH);

    
    rbNew = new JRadioButton(resources.getString("choose.new"),defs.length==0);
    rbNew.addItemListener(callback);
    rbNew.setAlignmentX(0);
    gh.add(rbNew,1,3,2,1, GridBagHelper.GROWFILL_HORIZONTAL);

    ButtonGroup group = new ButtonGroup();
    group.add(rbChoose);
    group.add(rbNew);

    
    tfNew = new JTextField();
    tfNew.setEnabled(defs.length==0);
    tfNew.setAlignmentX(0);
    gh.add(tfNew,1,4,2,1, GridBagHelper.GROWFILL_HORIZONTAL);

    
    if (defs.length>0) {
      lChoose.setSelectedIndex(0);
    }
    
    
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
    
    
    String tag = tfNew.getText();
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
        setText(def.getTag()+" ("+def.getName()+")");
        setIcon(def.getImage());
      return this;
    }

  } 
  
  
  private class Callback extends MouseAdapter implements ItemListener, ListSelectionListener, Comparator  {
    
    
    public int compare(Object o1, Object o2) {
      MetaProperty m1 = (MetaProperty)o1, m2 = (MetaProperty)o2;
      return m1.getTag().compareTo(m2.getTag());
    }
    
    
    public void mouseClicked(MouseEvent event) {
      if (event.getClickCount()>1) {
        ActionEvent e = new ActionEvent(ChoosePropertyBean.this, 0, null);
        ActionListener[] as = (ActionListener[])listeners.toArray(new ActionListener[listeners.size()]);
        for (int i = 0; i < as.length; i++) {
          as[i].actionPerformed(e);
        }
      }
    }
    
    
    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == rbChoose) {
        lChoose.setEnabled(true);
        tfNew.setEnabled(false);
        lChoose.requestFocusInWindow();
      }
      if (e.getSource() == rbNew) {
        lChoose.clearSelection();
        lChoose.setEnabled(false);
        tfNew.setEnabled(true);
        tfNew.requestFocusInWindow();
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

