
package genj.option;

import genj.util.swing.ImageIcon;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import swingx.tree.AbstractTreeModel;


public class OptionsWidget extends JPanel {

  
  public final static ImageIcon IMAGE = new ImageIcon(OptionsWidget.class, "images/Options");

  
  private JTree tree;

  
  private Model model = new Model();

  
  private int widthOf1stColumn = 32;

  
  private String title;

  
  private DefaultTreeCellRenderer defaultRenderer;
  
  
  public OptionsWidget(String title) {
    this(title, null);
  }

  
  public OptionsWidget(String title, List options) {

    this.title = title;

    
    tree = new JTree(model) {
      public boolean isPathEditable(TreePath path) {
        return path.getLastPathComponent() instanceof Option;
      }
    };
    tree.setShowsRootHandles(false);
    tree.setRootVisible(false);
    tree.setCellRenderer(new Cell());
    tree.setCellEditor(new Cell());
    tree.setEditable(true);
    tree.setInvokesStopCellEditing(true);

    ToolTipManager.sharedInstance().registerComponent(tree);

    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(tree));

    
    if (options!=null)
      setOptions(options);

    
  }

  
  public void removeNotify() {
    
    stopEditing();
    
    super.removeNotify();
  }

  
  public void stopEditing() {
    tree.stopEditing();
  }

  
  public void setOptions(List set) {

    
    stopEditing();

    
    tree.clearSelection();

    
    ListIterator it = set.listIterator();
    while (it.hasNext()) {
      Option option = (Option)it.next();
      if (option.getUI(this)==null)
        it.remove();
    }

    
    FontRenderContext ctx = new FontRenderContext(null,false,false);
    Font font = tree.getFont();
    widthOf1stColumn = 0;
    for (int i = 0; i < set.size(); i++) {
      Option option = (Option)set.get(i);
      widthOf1stColumn = Math.max(widthOf1stColumn, 4+(int)Math.ceil(font.getStringBounds(option.getName(), ctx).getWidth()));
    }

    
    model.setOptions(set);

    
    for (int i=0;i<tree.getRowCount();i++)
      tree.expandRow(i);

    
    doLayout();
  }

    
  public void setUI(TreeUI ui) {
    
    super.setUI(ui);
    
    defaultRenderer = new DefaultTreeCellRenderer();
  }

  
  private class Cell extends AbstractCellEditor implements TreeCellRenderer, TreeCellEditor {

    
    private OptionUI optionUi;

    
    private JPanel panel = new JPanel() {
      
      protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        if (ks.getKeyCode()==KeyEvent.VK_ENTER)
          stopCellEditing();
        if (ks.getKeyCode()==KeyEvent.VK_ESCAPE)
          cancelCellEditing();
        return true;
      }
    };

    
    private JLabel labelForName = new JLabel();

    
    private JLabel labelForValue = new JLabel();

    
    private Cell() {
      panel.setOpaque(false);
      panel.setLayout(new BorderLayout());
      panel.add(labelForName, BorderLayout.WEST);
    }

    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      
      if (defaultRenderer!=null) {
        if (selected) {
          labelForName.setForeground(defaultRenderer.getTextSelectionColor());
          labelForName.setBackground(defaultRenderer.getBackgroundSelectionColor());
          labelForValue.setForeground(defaultRenderer.getTextSelectionColor());
          labelForValue.setBackground(defaultRenderer.getBackgroundSelectionColor());
        } else {
          labelForName.setForeground(defaultRenderer.getTextNonSelectionColor());
          labelForName.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
          labelForValue.setForeground(defaultRenderer.getTextNonSelectionColor());
          labelForValue.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
        }
      }
      
      if (value instanceof Option)
        return assemblePanel((Option)value, false);
      

      
      if (panel.getComponentCount()>1)
        panel.remove(1);
      labelForName.setText(value.toString());
      return panel;
    }

    
    private JPanel assemblePanel(Option option, boolean forceUI) {
      
      if (panel.getComponentCount()>1)
        panel.remove(1);
      
      optionUi = option.getUI(OptionsWidget.this);
      
      labelForName.setText(option.getName());
      labelForName.setPreferredSize(new Dimension(widthOf1stColumn,16));
      
      JComponent compForValue;
      String text = optionUi.getTextRepresentation();
      if (text!=null&&!forceUI) {
        labelForValue.setText(text);
        compForValue = labelForValue;
      } else {
        compForValue = optionUi.getComponentRepresentation();
      }
      panel.add(compForValue, BorderLayout.CENTER);
      panel.setToolTipText(option.getToolTip());

      
      return panel;
    }

    
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
      return assemblePanel((Option)value, true);
    }

    
    public Object getCellEditorValue() {
      return null;
    }

    
    public void cancelCellEditing() {
      optionUi = null;
      super.cancelCellEditing();
    }

    
    public boolean stopCellEditing() {
      if (optionUi!=null)
        optionUi.endRepresentation();
      return super.stopCellEditing();
    }

  } 

  
  private class Model extends AbstractTreeModel {

    
    private List categories = new ArrayList();
    private Map cat2options = new HashMap();

    
    protected Object getParent(Object node) {
      throw new IllegalArgumentException();
    }

    private List getCategory(String cat) {
      if (cat==null)
        cat = title;
      List result = (List)cat2options.get(cat);
      if (result==null) {
        result = new ArrayList();
        cat2options.put(cat, result);
        categories.add(cat);
      }
      return result;
    }

    
    private void setOptions(List set) {

      
      cat2options.clear();
      categories.clear();

      for (int i = 0; i < set.size(); i++) {
        Option option = (Option)set.get(i);
        getCategory(option.getCategory()).add(option);
      }

      
      fireTreeStructureChanged(this, new Object[] {this}, null, null);
    }

    
    public Object getRoot() {
      return this;
    }

    
    public int getChildCount(Object parent) {
      if (parent==this)
        return categories.size();
      return getCategory((String)parent).size();
    }

    
    public boolean isLeaf(Object node) {
      return node instanceof Option;
    }

    
    public Object getChild(Object parent, int index) {
      if (parent==this)
        return categories.get(index);
      return getCategory((String)parent).get(index);
    }

    
    public int getIndexOfChild(Object parent, Object child) {
      throw new IllegalArgumentException();
    }

  } 

} 
