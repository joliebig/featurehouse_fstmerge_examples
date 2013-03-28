
package genj.util.swing;

import genj.util.ChangeSupport;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;


public class ListSelectionWidget<T> extends JComponent {

  
  private JList lChoose;
  
  
  private List<T> choices = new ArrayList<T>();
  
  
  private Set<T> selection = null;
  
  private ChangeSupport changes = new ChangeSupport(this);

  
  public ListSelectionWidget() {

    
    lChoose = new JList();
    lChoose.setCellRenderer(new Renderer());
    lChoose.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lChoose.addMouseListener(new SelectionListener());

    setLayout(new BorderLayout());
    add(new JScrollPane(lChoose),"Center");

    
  }
  
  public void addChangeListener(ChangeListener listener) {
    changes.addChangeListener(listener);
  }
  
  public void removeChangeListener(ChangeListener listener) {
    changes.removeChangeListener(listener);
  }
  
  @Override
  public synchronized void addMouseListener(MouseListener l) {
    lChoose.addMouseListener(l);
  }

  @Override
  public synchronized void removeMouseListener(MouseListener l) {
    lChoose.removeMouseListener(l);
  }

  public T getChoice(Point point) {
    int i = lChoose.locationToIndex(point);
    return i<0||i>choices.size()-1 ? null : choices.get(i);
  }

  
  public Dimension getPreferredSize() {
    return new Dimension(64,64);
  }

  
  private void update() {
    lChoose.setListData(choices.toArray(new Object[choices.size()]));
    changes.fireChangeEvent();
  }
  
  
  public void addChoice(T choice) {
    choices.add(choice);
    update();
  }

  
  public void removeChoice(T choice) {
    choices.remove(choice);
    update();
  }

  
  public List<T> getChoices() {
    return Collections.unmodifiableList(choices);
  }

  
  public void setChoices(T[] set) {
    choices.clear();
    choices.addAll(Arrays.asList(set));
    update();
  }

  
  public void setChoices(Collection<T> c) {
    choices = new ArrayList<T>(c);
    update();
  }
  
  
  public void setCheckedChoices(Set<T> set) {
    selection = new HashSet<T>(set);
    
    for (T t : set)
      if (!choices.contains(t))
        choices.add(t);
    
    update();
  }
  
  
  public Set<T> getCheckedChoices() {
    if (selection==null) selection = new HashSet<T>();
    return Collections.unmodifiableSet(selection);
  }
  
  
  @SuppressWarnings("unchecked")
  public T getSelectedChoice() {
    return (T)lChoose.getSelectedValue();
  }
  
  public int getSelectedIndex() {
    return lChoose.getSelectedIndex();
  }

  
  
  public void swapChoices(int i, int j) {
    
    int selected = lChoose.getSelectedIndex();
  
    
    T o = choices.get(i);
    choices.set(i, choices.get(j));
    choices.set(j, o);

    
    update();

    if (selected==i)
      lChoose.setSelectedIndex(j);
    if (selected==j)
      lChoose.setSelectedIndex(i);
  }          

  
  public void down() {

    
    int row = lChoose.getSelectedIndex();
    if ((row==-1)||(row==choices.size()-1))
      return;

    
    T o = choices.get(row);
    choices.set(row, choices.get(row+1));
    choices.set(row+1, o);

    
    update();
    lChoose.setSelectedIndex(row+1);
  }
  
  
  protected String getText(T choice) {
    return choice.toString();
  }

  
  protected ImageIcon getIcon(T choice) {
    return null;
  }

  
  private class Renderer extends DefaultListCellRenderer{

    
    private JPanel        panel = new JPanel();
    private JCheckBox     check = new JCheckBox();

    
    private Renderer() {
      check.setOpaque(false);
      panel.setOpaque(true);
      panel.setLayout(new BorderLayout());
      panel.add(check,"West");
      panel.add(this,"Center");
    }

    
    @SuppressWarnings("unchecked")
    public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
      
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      setText( ListSelectionWidget.this.getText( (T)value) );
      setIcon( ListSelectionWidget.this.getIcon( (T)value) );
      
      if (selection==null) 
        return this;
      
      panel.setBackground(super.getBackground());
      check.setSelected( selection.contains(value) );
      return panel;
    }

  } 

  
  private class SelectionListener extends MouseAdapter {
    
    public void mousePressed(MouseEvent me) {
      
      if (selection==null) return;
      
      int pos = lChoose.locationToIndex(me.getPoint());
      if (pos==-1) return;
      
      T choice = choices.get(pos);
      if (!selection.remove(choice)) 
        selection.add(choice);
      
      lChoose.repaint(lChoose.getCellBounds(pos,pos));
      
      changes.fireChangeEvent();
    }
  } 

  public void addSelectionListener(ListSelectionListener listener) {
    lChoose.addListSelectionListener(listener);
  }
  
  public void removeSelectionListener(ListSelectionListener listener) {
    lChoose.removeListSelectionListener(listener);
  }
  
} 
