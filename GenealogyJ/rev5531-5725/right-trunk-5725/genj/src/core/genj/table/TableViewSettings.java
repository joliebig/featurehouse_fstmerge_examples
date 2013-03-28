
package genj.table;

import genj.common.PathTreeWidget;
import genj.gedcom.Gedcom;
import genj.gedcom.Grammar;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.GridBagHelper;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.ListSelectionWidget;
import genj.view.Settings;
import genj.view.ViewManager;

import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class TableViewSettings extends JPanel implements Settings {

  
  private JComboBox           cTypes;
  private PathTreeWidget      pathTree;
  private ListSelectionWidget pathList;
  private TableView           table;
  private Resources           resources = Resources.get(this);

  
  public void init(ViewManager manager) {

    
    GridBagHelper gh = new GridBagHelper(this);

    
    cTypes = new JComboBox();

    for (int i=0;i<Gedcom.ENTITIES.length;i++) {
      cTypes.addItem(Gedcom.getName(Gedcom.ENTITIES[i],true));
    }
    cTypes.addActionListener(new ActionChooseEntity());

    
    pathTree = new PathTreeWidget();

    PathTreeWidget.Listener plistener = new PathTreeWidget.Listener() {
      
      
      public void handleSelection(TagPath path, boolean on) {
        if (!on) {
          pathList.removeChoice(path);
        } else {
          pathList.addChoice(path);
        }
      }
      
    };
    pathTree.addListener(plistener);

    
    pathList = new ListSelectionWidget() {
      protected ImageIcon getIcon(Object choice) {
	      TagPath path = (TagPath)choice;
	      return Grammar.V55.getMeta(path).getImage();
      }
    };

    
    ButtonHelper bh = new ButtonHelper().setInsets(0);
    AbstractButton bUp   = bh.create(new ActionUpDown(true));
    AbstractButton bDown = bh.create(new ActionUpDown(false));
    
    
    gh.add(new JLabel(resources.getString("info.entities"))  ,0,1,1,1);
    gh.add(cTypes                  ,1,1,2,1,GridBagHelper.GROWFILL_HORIZONTAL);

    gh.add(new JLabel(resources.getString("info.columns"))   ,0,2,1,1);
    gh.add(pathTree                ,1,2,2,2,GridBagHelper.GROWFILL_BOTH);

    gh.add(new JLabel(resources.getString("info.order"))  ,0,4,1,1);
    gh.add(bUp                                            ,0,5,1,1,GridBagHelper.FILL_HORIZONTAL);
    gh.add(bDown                                          ,0,6,1,1,GridBagHelper.FILL_HORIZONTAL);
    gh.add(pathList                                       ,1,4,2,4,GridBagHelper.GROWFILL_BOTH);

    
  }
  
  
  public void setView(JComponent view) {
    
    table = (TableView)view;
    
    cTypes.setSelectedItem(Gedcom.getName(table.getMode().getTag(), true));
    
  }


  
  public void apply() {
    
    String tag = Gedcom.ENTITIES[cTypes.getSelectedIndex()];
    List choices = pathList.getChoices();
    TagPath[] paths = (TagPath[])choices.toArray(new TagPath[choices.size()]);
    table.getMode(tag).setPaths(paths);
    
  }

  
  public void reset() {

    
    String tag = Gedcom.ENTITIES[cTypes.getSelectedIndex()];
    
    TagPath[] selectedPaths = table.getMode(tag).getPaths();
    TagPath[] usedPaths     = table.gedcom.getGrammar().getAllPaths(tag, Property.class);

    pathTree.setPaths(usedPaths, selectedPaths);
    pathList.setChoices(selectedPaths);

    
  }
  
  
  public JComponent getEditor() {
    return this;
  }

  
  private class ActionChooseEntity extends Action2 {
    
    
    public void execute() {
      if (table==null) return;
      table.setMode(table.getMode(Gedcom.ENTITIES[cTypes.getSelectedIndex()]));
      reset();
    }
  } 
  
  
  private class ActionUpDown extends Action2 {
    
    private boolean up;
    
    protected ActionUpDown(boolean up) {
      this.up=up;
      if (up) setText(resources, "info.up");
      else setText(resources, "info.down");
    }
    
    public void execute() {
      if (up)
        pathList.up();
      else 
        pathList.down();
    }
  } 
  
}
