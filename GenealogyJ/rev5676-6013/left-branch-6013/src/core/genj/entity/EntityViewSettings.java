
package genj.entity;

import genj.renderer.BlueprintList;
import genj.renderer.BlueprintManager;
import genj.view.Settings;
import genj.view.ViewManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;



public class EntityViewSettings extends JTabbedPane implements Settings {
  
  
  private EntityView entityView; 
  
  
  private BlueprintList blueprintList;
  
  
  private JCheckBox 
    checkAntialiasing = new JCheckBox(EntityView.resources.getString("antialiasing" ));

  
  public void init(ViewManager manager) {
    
    
    Box main = new Box(BoxLayout.Y_AXIS);

    checkAntialiasing.setToolTipText(EntityView.resources.getString("antialiasing.tip"));
    main.add(checkAntialiasing);
    
    
    blueprintList = new BlueprintList(BlueprintManager.getInstance());
    
    
    add(EntityView.resources.getString("page.main")      , main);
    add(EntityView.resources.getString("page.blueprints"), blueprintList);
    
    
  }
  
  
  
  public void setView(JComponent view) {

    
    entityView = (EntityView)view;
    
    
    blueprintList.setGedcom(entityView.gedcom);    
    
  }
  
  
  private Integer wrap(int type) {
    return new Integer(type);
  }
  
  
  private int unwrap(Object type) {
    return ((Integer)type).intValue();
  }

  
  public void apply() {
    entityView.setAntialiasing(checkAntialiasing.isSelected());
    entityView.setBlueprints(blueprintList.getSelection());
  }

  
  public void reset() {
    checkAntialiasing.setSelected(entityView.isAntialiasing());
    blueprintList.setSelection(entityView.getBlueprints());
  }

  
  public JComponent getEditor() {
    return this;
  }

} 
