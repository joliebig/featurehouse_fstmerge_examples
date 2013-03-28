
package genj.renderer;

import genj.gedcom.Gedcom;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.HeadlessLabel;
import genj.window.WindowManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import swingx.tree.AbstractTreeModel;

public class BlueprintList extends JSplitPane {
  
  
  private Map selection = new HashMap();
  
  
  private BlueprintEditor editor;

  
  private JTree treeBlueprints;
  
  
  private Add add = new Add();
  private Del del = new Del();
  
  
  private final static Resources resources = Resources.get(BlueprintEditor.class);
 
  
  private Gedcom gedcom; 
  
  
  private Model model = new Model();
  
    public BlueprintList(BlueprintManager bpMgr) {
    
    
    editor = new BlueprintEditor(bpMgr);
    
    
    treeBlueprints = new JTree(model);
    treeBlueprints.setRootVisible(false);
    treeBlueprints.setShowsRootHandles(true);
    treeBlueprints.setCellRenderer(new Renderer());
    treeBlueprints.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    treeBlueprints.getSelectionModel().addTreeSelectionListener(new SelectionListener());
    
    
    Box left = new Box(BoxLayout.Y_AXIS);
    JScrollPane scroll = new JScrollPane(treeBlueprints);
    scroll.setAlignmentX(0);
    left.add(scroll);
    
    ButtonHelper bh = new ButtonHelper().setContainer(left);
    bh.create(add);
    bh.create(del);
    
    
    setLeftComponent(left);
    setRightComponent(editor);
    
  }
  
    public void setGedcom(Gedcom geDcom) {
    gedcom = geDcom;
  }
  
    public Map getSelection() {
    editor.commit();
    return selection;
  }
  
  
  public void setSelection(Map selEction) {
    selection = selEction;
    treeBlueprints.repaint();
  }
  
  
  private class Add extends Action2 {
        private Add() {
      super.setText(resources, "blueprint.add");
      super.setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent event) {
      
      TreePath path = treeBlueprints.getSelectionPath();
      if (path==null) 
        return;
      Object node = path.getLastPathComponent();
      
      String name = WindowManager.getInstance().openDialog(
        null,
        null,
        WindowManager.QUESTION_MESSAGE,
        resources.getString("blueprint.add.confirm"),
        "",
        BlueprintList.this
      );
      if (name==null||name.length()==0) 
        return;
      
      String html = node instanceof Blueprint ? ((Blueprint)node).getHTML() : "";
      
      try {
        Blueprint blueprint = BlueprintManager.getInstance().addBlueprint(new Blueprint(
          node instanceof Blueprint ? ((Blueprint)node).getTag() : (String)node,
          name, html, false
        ));
        
        model.fireStructureChanged();
        
        treeBlueprints.setSelectionPath(new TreePath(model.getPathToRoot(blueprint)));
        editor.setHTMLVisible(true);
      } catch (IOException e) {
        
      }
      
    }
  } 

  
  private class Del extends Action2 {
    
    private Del() {
      super.setText(resources, "blueprint.del");
      super.setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent event) {
      
      TreePath path = treeBlueprints.getSelectionPath();
      if (path==null) 
        return;
      Object node = path.getLastPathComponent();
      if (!(node instanceof Blueprint)) 
        return;
      
      Blueprint blueprint = (Blueprint)node;
      int rc = WindowManager.getInstance().openDialog(null,null,WindowManager.QUESTION_MESSAGE,resources.getString("blueprint.del.confirm", blueprint.getName()),Action2.okCancel(),BlueprintList.this); 
      if (rc!=0) 
        return;
      
      selection.remove(blueprint.getTag());
      
      try {
        BlueprintManager.getInstance().delBlueprint(blueprint);
      } catch (IOException e) {
        
      }
      
      model.fireStructureChanged();
      
    }
  } 

  
  private class Renderer implements TreeCellRenderer {

    
    private HeadlessLabel label = new HeadlessLabel();
    
    
    private Color cSelection = new DefaultTreeCellRenderer().getBackgroundSelectionColor();
    
    
    private JRadioButton button = new JRadioButton();

    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      
      if (value instanceof Blueprint) {
        Blueprint bp = (Blueprint)value;
        button.setOpaque(selected);
        button.setBackground(cSelection);
        button.setText(bp.getName());
        button.setSelected(selection.get(bp.getTag())==bp);
        
        return button; 
      }
      
      
      if (value instanceof String) {
        String tag = (String)value;
        label.setOpaque(selected);
        label.setBackground(cSelection);
        label.setText(Gedcom.getName(tag, true));
        label.setIcon(Gedcom.getEntityImage(tag));
      }
      
      
      return label;
    }
    
  } 

  
  private class SelectionListener implements TreeSelectionListener  {

    
    public void valueChanged(TreeSelectionEvent e) {
      
      
      editor.commit();
      
      
      if (e.getNewLeadSelectionPath()!=null) {

        
        Object node = e.getNewLeadSelectionPath().getLastPathComponent();
        
        
        if (node instanceof Blueprint) {
          Blueprint bp = (Blueprint)node;
          
          selection.put(bp.getTag(), bp);
          
          treeBlueprints.repaint();
          
          add.setEnabled(true);
          del.setEnabled(!bp.isReadOnly());
          
          editor.set(gedcom, bp, !bp.isReadOnly());
          return;
        }
      
        
        add.setEnabled(true);
        del.setEnabled(false);

      } else {

        add.setEnabled(false);
        del.setEnabled(false);
        
      }
            
      
      editor.set(null, null, false);

      
    }
    
  } 

  
  private class Model extends AbstractTreeModel {
     
    
    protected void fireStructureChanged() {
      fireTreeStructureChanged(this, new Object[] { this }, null, null);
    }

        
    public boolean isLeaf(Object node) {
      return node instanceof Blueprint;
    }
    
    
    public int getIndexOfChild(Object parent, Object child) {

      
      if (child instanceof Blueprint) {
        Blueprint bp = (Blueprint)child;
        return BlueprintManager.getInstance().getBlueprints(bp.getTag()).indexOf(bp);
      }
  
      
      String tag = (String)child;
      for (int i=0;i<Gedcom.ENTITIES.length;i++)
        if (Gedcom.ENTITIES[i].equals(tag))
          return i;
          
      
      throw new IllegalArgumentException();
    }

        
    protected Object getParent(Object node) {
      
      if (node==this)
        return null;
      
      if (node instanceof Blueprint)
        return ((Blueprint)node).getTag();
      
      return this;
    }

    
    public Object getChild(Object parent, int index) {
      
      if (parent==this)
        return Gedcom.ENTITIES[index];
      
      String tag = (String)parent;
      return BlueprintManager.getInstance().getBlueprints(tag).get(index);
    }

    
    public int getChildCount(Object parent) {
      
      if (parent==this)
        return Gedcom.ENTITIES.length;
      
      if (parent instanceof Blueprint)
        return 0;
      
      String tag = (String)parent;
      return BlueprintManager.getInstance().getBlueprints(tag).size();
    }

    
    public Object getRoot() {
      return this;
    }

  } 
  
} 
