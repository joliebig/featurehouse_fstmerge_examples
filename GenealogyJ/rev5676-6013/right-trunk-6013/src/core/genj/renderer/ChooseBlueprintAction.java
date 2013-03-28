
package genj.renderer;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.DialogHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.NestedBlockLayout;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public abstract class ChooseBlueprintAction extends Action2 {
  
  private final static Resources RESOURCES = Resources.get(ChooseBlueprintAction.class);
  private final static ImageIcon IMAGE = new ImageIcon(ChooseBlueprintAction.class, "Blueprint.png");
  private final static BlueprintManager MGR = BlueprintManager.getInstance();
  
  private Entity recipient;
  private Blueprint current;
  private JList blueprints;
  private BlueprintEditor editor;
  
  protected ChooseBlueprintAction(Entity recipient, Blueprint current) {
    this.recipient = recipient;
    this.current = current;
    setText(RESOURCES.getString("blueprint.select"));
    setImage(IMAGE.getOverLayed(recipient.getImage(false)));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    
    editor = new BlueprintEditor(recipient);
    
    blueprints = new JList(BlueprintManager.getInstance().getBlueprints(recipient.getTag()).toArray());
    blueprints.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    final Action2 add = new Add();
    final Action2 del = new Del();
    blueprints.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        
        editor.commit();
        
        Blueprint selection = (Blueprint)blueprints.getSelectedValue();
        
        
        if (selection==null) {
          if (blueprints.getModel().getSize()>0)
            blueprints.setSelectedIndex(e.getFirstIndex());
          else {
            del.setEnabled(false);
            editor.set(null);
          }
          return;
        }
        editor.set(selection);
        del.setEnabled(!selection.isReadOnly());
        
      }
    });
    blueprints.setSelectedValue(current, true);
    
    JPanel content = new JPanel(new NestedBlockLayout(
        "<col><for gx=\"1\"/><row><col><row><list wy=\"1\" gx=\"1\" gy=\"1\"/></row><row><add/><del/></row></col><col><editor wy=\"1\" wx=\"1\"/></col></row></col>"
     ));
    
    ButtonHelper bh = new ButtonHelper();
    content.add(new JLabel(RESOURCES.getString("blueprint.select.for", Gedcom.getName(recipient.getTag(), true))));
    content.add(new JScrollPane(blueprints));
    content.add(bh.create(add));
    content.add(bh.create(del));
    content.add(editor);
    
    DialogHelper.openDialog(
        RESOURCES.getString("blueprint"), 
        DialogHelper.QUESTION_MESSAGE, 
        content, 
        Action2.okOnly(), 
        DialogHelper.getComponent(e));
    
    editor.commit();

    current = (Blueprint)blueprints.getSelectedValue();
    if (current!=null)
      commit(recipient, current);
  }

  private class Add extends Action2 {
    Add() {
      super(RESOURCES.getString("blueprint.add"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      
      
      Blueprint selection = (Blueprint)blueprints.getSelectedValue();
      
      String name = DialogHelper.openDialog(
        null,
        DialogHelper.QUESTION_MESSAGE,
        RESOURCES.getString("blueprint.add.confirm"),
        "",
        DialogHelper.getComponent(e)
      );
      if (name==null||name.length()==0) 
        return;
      
      String html = selection!=null ? selection.getHTML() : "";
      
      try {
        Blueprint blueprint = MGR.addBlueprint(new Blueprint(recipient.getTag(), name, html, false));
        blueprints.setListData(MGR.getBlueprints(recipient.getTag()).toArray());
        blueprints.setSelectedValue(blueprint, true);
      } catch (IOException ex) {
        Logger.getLogger("genj.renderer").log(Level.WARNING, "can't add blueprint "+name, ex);
      }
      
    }
  }
  
  private class Del extends Action2 {
    Del() {
      super(RESOURCES.getString("blueprint.del"));
      setEnabled(false);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      Blueprint selection = (Blueprint)blueprints.getSelectedValue();
      if (selection==null||selection.isReadOnly())
        return;
      if (0!=DialogHelper.openDialog(null,DialogHelper.WARNING_MESSAGE,RESOURCES.getString("blueprint.del.confirm", selection.getName()),Action2.okCancel(),DialogHelper.getComponent(e)))
        return;
      
      try {
        MGR.delBlueprint(selection);
      } catch (IOException ex) {
        Logger.getLogger("genj.renderer").log(Level.WARNING, "can't delete blueprint "+selection, ex);
      }
      blueprints.setListData(MGR.getBlueprints(recipient.getTag()).toArray());
      if (blueprints.getModel().getSize()>0)
        blueprints.setSelectedIndex(0);
    }
  };
  
  protected abstract void commit(Entity recipient, Blueprint blueprint);
  
}

