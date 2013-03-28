
package genj.util.swing;

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


public class ListSelectionWidget<T> extends JComponent {

  
  private JList lChoose;
  
  
  private List<T> choices = new ArrayList<T>();
  
  
  private Set<T> selection = null;

  
  public ListSelectionWidget() {

    
    lChoose = new JList();
    lChoose.setCellRenderer(new Renderer());
    lChoose.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lChoose.addMouseListener(new SelectionListener());

    setLayout(new BorderLayout());
    add(new JScrollPane(lChoose),"Center");

    
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
  
  
  public void setSelection(Set<T> set) {
    selection = new HashSet<T>(set);
    
    for (T t : set)
      if (!choices.contains(t))
        choices.add(t);
    
    update();
  }
  
  
  public Set<T> getSelection() {
    if (selection==null) selection = new HashSet<T>();
    return Collections.unmodifiableSet(selection);
  }

  
  public void up() {

    
    int row = lChoose.getSelectedIndex();
    if ((row==-1)||(row==0)) {
      return;
    }

    
    T o = choices.get(row);
    choices.set(row, choices.get(row-1));
    choices.set(row-1, o);

    
    update();
    lChoose.setSelectedIndex(row-1);
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
      panel.setOpaque(false);
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
    }
  } 
  
} 
