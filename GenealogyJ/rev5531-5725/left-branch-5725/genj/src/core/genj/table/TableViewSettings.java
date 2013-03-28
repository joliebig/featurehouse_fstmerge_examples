
package genj.table;

import genj.common.PathTreeWidget;
import genj.gedcom.Grammar;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.GridBagHelper;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.ListSelectionWidget;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class TableViewSettings extends JPanel {

  private TableView           view;
  private Grammar             grammar = Grammar.V55;
  private PathTreeWidget      pathTree;
  private ListSelectionWidget<TagPath> pathList;
  private Resources           resources = Resources.get(this);

  public TableViewSettings(TableView view) {
    
    this.view = view;

    
    GridBagHelper gh = new GridBagHelper(this);

    
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
    String tag = view.getMode().getTag();
    TagPath[] selectedPaths = view.getMode(tag).getPaths();
    TagPath[] usedPaths     = grammar.getAllPaths(tag, Property.class);
    pathTree.setPaths(usedPaths, selectedPaths);
    pathTree.addListener(plistener);

    
    pathList = new ListSelectionWidget<TagPath>() {
      protected ImageIcon getIcon(TagPath path) {
	      return grammar.getMeta(path).getImage();
      }
    };
    pathList.setChoices(selectedPaths);
    pathList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        TagPath path = pathList.getChoice(e.getPoint());
        if (path!=null&&e.getClickCount()==2)
          pathList.removeChoice(path);
      }
    });

    
    ButtonHelper bh = new ButtonHelper().setInsets(0);
    AbstractButton bUp   = bh.create(new Move(true));
    AbstractButton bDown = bh.create(new Move(false));
    
    
    gh.add(new JLabel(resources.getString("info.columns"))   ,0,0,3,1, GridBagHelper.FILL_HORIZONTAL);
    gh.add(pathTree                ,0,1,3,1,GridBagHelper.GROWFILL_BOTH);

    gh.add(new JLabel(resources.getString("info.order"))  ,0,2,3,1, GridBagHelper.FILL_HORIZONTAL);
    gh.add(pathList                                       ,0,3,3,1,GridBagHelper.GROWFILL_BOTH);
    gh.add(bUp                                            ,0,4,1,1,GridBagHelper.FILL_HORIZONTAL);
    gh.add(bDown                                          ,1,4,1,1,GridBagHelper.FILL_HORIZONTAL);

    
    
    if (view.getTable().getModel()!=null)
      grammar = view.getTable().getModel().getGedcom().getGrammar();

  }


  
  public void commit() {
    
    List<TagPath> choices = pathList.getChoices();
    view.getMode().setPaths(choices.toArray(new TagPath[choices.size()]));
    
  }

  
  private class Move extends Action2 {
    
    private boolean up;
    
    protected Move(boolean up) {
      this.up=up;
      if (up) setText(resources, "info.up");
      else setText(resources, "info.down");
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
      if (up)
        pathList.up();
      else 
        pathList.down();
    }
  } 
  
}
