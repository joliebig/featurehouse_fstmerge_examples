
package genj.util.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ColorsWidget extends JPanel {
  
  
  private JColorChooser chooser = new JColorChooser();

  
  private Model model = new Model();

  
  public ColorsWidget() {
    super(new BorderLayout());
    
    
    chooser.setPreviewPanel(new JPanel()); 

    
    final JList list = new JList(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(new Renderer());
    
    
    add(new JScrollPane(list), BorderLayout.CENTER);
    add(chooser, BorderLayout.SOUTH);
    
    
    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int i = list.getSelectedIndex();
        if (i>=0)
          chooser.setColor(model.getItemAt(i).color);
      }
    });
    chooser.getSelectionModel().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int i = list.getSelectedIndex();
        if (i>=0) 
          model.setColor(i, chooser.getColor());
      }
    });
    
    
  }
  
  
  public void removeAllColors() {
    model.clear();
  }
  
  
  public Color getColor(String key) {
    return model.getItem(key).color;
  }
  
  
  public void addColor(String key, String name, Color color) {
    model.add(new Item(key,name,color));
  }
  
  
  private class Model extends AbstractListModel {

    
    private List items = new ArrayList();

    void clear() {
      int size = items.size();
      for (int i=size-1;i>=0;i--) {
        items.remove(i);
      }
      fireIntervalRemoved(this, 0, size);
    }
    void add(Item item) {
      items.add(item);
      fireIntervalAdded(this, items.size()-1, items.size());
    }
    void setColor(int index, Color set) {
      getItemAt(index).color = set;
      fireContentsChanged(this, index, index);
    }
    Item getItem(String key) {
      for (int i = 0; i < items.size(); i++) {
        Item item = (Item)items.get(i);
        if (item.key.equals(key))
          return item;
      }
      throw new IllegalArgumentException("key for unknown color");
    }
    Item getItemAt(int index) {
      return (Item)getElementAt(index);
    }
    public Object getElementAt(int index) {
      return items.get(index);
    }
    
    public int getSize() {
      return items.size();
    }
    
  } 

  
  private class Item {
    
    String key;
    String name;
    Color color;
    
    Item(String k, String n, Color c) {
      key = k;
      name = n;
      color = c;
    }
  } 
  
  
  private class Renderer extends DefaultListCellRenderer implements Icon {
    private int dim = 8;
    private Color color;
    private Renderer() {
      setOpaque(true);
      setHorizontalTextPosition(JLabel.LEFT);
    }
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Item item = (Item)value;
      super.getListCellRendererComponent(list, item.name, index, isSelected, cellHasFocus);
      setIcon(this);
      color=item.color;
      return this;
    }
    public int getIconHeight() {
      return dim;
    }
    public int getIconWidth() {
      return dim;
    }
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(color);
      g.fillRect(x,y,dim,dim);
      g.setColor(getForeground());
      g.drawRect(x,y,dim,dim);
    }
  } 
  
} 
