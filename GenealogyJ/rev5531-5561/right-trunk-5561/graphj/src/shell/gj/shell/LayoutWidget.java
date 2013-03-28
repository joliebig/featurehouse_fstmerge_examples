
package gj.shell;

import gj.layout.GraphLayout;
import gj.layout.LayoutException;
import gj.shell.swing.Action2;
import gj.shell.util.ReflectHelper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;


public class LayoutWidget extends JPanel {
  
  
  private GraphLayout[] layouts = new GraphLayout[0];
  
  
  private JComboBox comboLayouts;

  
  private PropertyWidget widgetProperties;
  
  
  private JButton buttonExecute;
  
  
  private Action 
    actionExecute = new ActionExecute(),
    actionSelect = new ActionSelect();
    
  
  private List<ActionListener> alisteners = new ArrayList<ActionListener>(); 

    
  public LayoutWidget() {

    
    setLayout(new BorderLayout());
    
    
    comboLayouts = new JComboBox();
    widgetProperties = new PropertyWidget();
    buttonExecute = new JButton();
    
    add(comboLayouts,BorderLayout.NORTH);
    add(widgetProperties, BorderLayout.CENTER);
    add(buttonExecute, BorderLayout.SOUTH);

    
    widgetProperties.addActionListener(actionExecute);
    buttonExecute.setAction(actionExecute);
    
    
    comboLayouts.setAction(actionSelect);
    
    
  }

  
  @Override
  public void setEnabled(boolean enabled) {
    actionExecute.setEnabled(enabled);
  }

  
  public JButton getDefaultButton() {
    return buttonExecute;
  }

  
  
  public GraphLayout[] getLayouts() {
    return layouts;
  }

  
  public void setLayouts(GraphLayout[] set) {
    layouts=set;
    comboLayouts.setModel(new DefaultComboBoxModel(layouts));
    comboLayouts.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, value!=null?ReflectHelper.getName(value.getClass()):"", index, isSelected, cellHasFocus);
      }
    });
    if (layouts.length>0) 
      comboLayouts.setSelectedItem(layouts[0]);
  }

  
  public void setSelectedLayout(GraphLayout set) {
    comboLayouts.setSelectedItem(set);
  }
  
  
  public GraphLayout getSelectedLayouts() {
    return (GraphLayout)comboLayouts.getSelectedItem();
  }
  
  
  public void addActionListener(ActionListener listener) {
    alisteners.add(listener);
  }
  
  
  protected class ActionExecute extends Action2 {
    protected ActionExecute() { 
      super("Execute"); 
      setEnabled(false); 
    }
    @Override
    public void execute() throws LayoutException {
      if (getSelectedLayouts()==null) 
        return;
      widgetProperties.commit();
      for (ActionListener listener : alisteners) 
        listener.actionPerformed(null);
      widgetProperties.refresh();
    }
  }
  
  
  protected class ActionSelect extends Action2 {
    protected ActionSelect() { 
      super("Select"); 
    }
    @Override
    public void execute() {
      
      Object layout = comboLayouts.getModel().getSelectedItem();
      if (layout==null) 
        return;
      for (ActionListener listener : alisteners) 
        listener.actionPerformed(null);
      
      widgetProperties.setInstance(layout);
    }
  }

  
}
