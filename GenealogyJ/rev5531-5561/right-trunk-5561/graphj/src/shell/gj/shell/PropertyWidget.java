
package gj.shell;

import gj.shell.swing.GBLayout;
import gj.shell.util.ReflectHelper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

 
public class PropertyWidget extends JPanel {
  
  
  private final static Boolean[] BOOLEANS = new Boolean[] {
    Boolean.TRUE,Boolean.FALSE
  };
  
  
  private Object instance;
  
  
  private List<ReflectHelper.Property> properties;
  
  
  private Map<String,JComponent> components;
  
  
  private Dimension biggestPreferredSize = new Dimension(0,0);
  
  
  private boolean isIgnoreActionEvent = false;
  
  
  private ActionListener alistener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if (isIgnoreActionEvent) return;
      EventListener[] es = listenerList.getListeners(ActionListener.class);
      for (int i=0; i<es.length; i++) {
        ((ActionListener)es[i]).actionPerformed(e);
      }
      if (e.getSource() instanceof JTextField)
        ((JTextField)e.getSource()).selectAll();
    }
  };
  
  
  public PropertyWidget() {
  }
  
  
  public void commit() {

    
    if (instance==null) 
      return;
    
    
    for (int p=0; p<properties.size(); p++) 
      ReflectHelper.setValue(properties.get(p), getValue(components.get(properties.get(p).getName())));
    
    
  }
  
  
  public void refresh() {
    
    
    if (instance==null) 
      return;
    
    
    properties = ReflectHelper.getProperties(instance, true);
    
    
    for (int p=0; p<properties.size(); p++) {
      setValue(components.get(properties.get(p).getName()), ReflectHelper.getValue(properties.get(p)));
    }
    
    
  }
  
  
  public static boolean hasProperties(Object instance) {
    return ReflectHelper.getProperties(instance, true).size()!=0;
  }
  
  
  public PropertyWidget setInstance(Object instance) {
    
    
    this.instance = instance;
    properties = ReflectHelper.getProperties(instance, true);
  
    
    GBLayout layout = new GBLayout(this);
    
    
    if (properties.size()==0) {
      
      layout.add(new JLabel("No Properties"),0,0,1,1,false,false,true,true);
      
    } else {
  
      
      components = new HashMap<String,JComponent>(properties.size());

      for (int p=0; p<properties.size(); p++) {

        ReflectHelper.Property prop = properties.get(p);
        
        JComponent component = getComponent(ReflectHelper.getValue(prop));
        components.put(prop.getName(), component);
        
        layout.add(new JLabel(prop.getName()),0,p,1,1,false,false,true,false);
        layout.add(component                 ,1,p,1,1,true ,false,true,false);
       
      }
      
      layout.add(new JLabel(),0,properties.size(),2,1,true,true,true,true);

    }
    
    
    revalidate();
    repaint();
        
    
    return this;
  }

  
  private JComponent getComponent(Object prop) {
    
    
    if (prop instanceof Boolean) {
      JComboBox cb = new JComboBox(BOOLEANS);
      cb.setSelectedItem(prop);
      cb.addActionListener(alistener);
      return cb;
    }
    
    
    if (Enum.class.isAssignableFrom(prop.getClass())) {
      Class<?> c = prop.getClass();
      while (c.getSuperclass()!=Enum.class) c = c.getSuperclass();
      JComboBox cb = new JComboBox(c.getEnumConstants());
      cb.setSelectedItem(prop);
      cb.addActionListener(alistener);
      return cb;
    }
    
    
    final JTextField result = new JTextField(prop.toString());
    result.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        result.selectAll();
      }
      public void focusLost(FocusEvent e) {
        
      }
    });
    result.addActionListener(alistener);
    return result;
  }  
  
  
  private Object getValue(JComponent component) {
    if (component instanceof JComboBox)
      return ((JComboBox)component).getSelectedItem();
    return ((JTextField)component).getText();
  }
  
  
  private void setValue(JComponent component, Object value) {
    if (component instanceof JComboBox) {
      isIgnoreActionEvent=true;
      JComboBox cb = (JComboBox)component;
      cb.setSelectedItem(value);
      isIgnoreActionEvent=false;
    } else {
      if (value==null)
        value = "";
      ((JTextField)component).setText(value.toString());
    }
  }
  
  
  @Override
  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.width = Math.max(d.width,biggestPreferredSize.width);
    d.height = Math.max(d.height,biggestPreferredSize.height);
    biggestPreferredSize = d;
    return d;
  }
  
  
  
  public void addActionListener(ActionListener a) {
    listenerList.add(ActionListener.class,a);
  }
  
  
  public void removeActionListener(ActionListener a) {
    listenerList.remove(ActionListener.class,a);
  }

}
