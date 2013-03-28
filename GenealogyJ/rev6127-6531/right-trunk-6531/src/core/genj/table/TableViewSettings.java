
package genj.table;

import genj.common.PathTreeWidget;
import genj.gedcom.Grammar;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.GridBagHelper;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.util.swing.ListSelectionWidget;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class TableViewSettings extends JPanel {

  private PathTreeWidget      pathTree;
  private ListSelectionWidget<TagPath> pathList;
  private Resources           resources = Resources.get(this);

  public TableViewSettings(final TableView view) {
    
    final Grammar grammar = view.getGedcom()!=null ? view.getGedcom().getGrammar() : Grammar.V55;
    
    
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
    pathTree.setGrammar(grammar);
    pathTree.setPaths(usedPaths, selectedPaths);
    pathTree.addListener(plistener);

    
    final Move up = new Move(true);
    final Move dn = new Move(false);
    final Del del = new Del();
    
    
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
          pathTree.setSelected(path, false);
      }
    });
    pathList.addSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        
        int i = pathList.getSelectedIndex();
        up.setEnabled(i>0);
        dn.setEnabled(i>=0&&i<pathList.getChoices().size()-1);
        del.setEnabled(i>=0);
      }
    });
    pathList.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        
        List<TagPath> choices = pathList.getChoices();
        view.getMode().setPaths(choices.toArray(new TagPath[choices.size()]));
      }      
    });

    
    gh.add(new JLabel(resources.getString("info.columns")),0,0,4,1, GridBagHelper.FILL_HORIZONTAL);
    gh.add(pathTree                                       ,0,1,4,1,GridBagHelper.GROWFILL_BOTH);

    gh.add(new JLabel(resources.getString("info.order"))  ,0,2,4,1, GridBagHelper.FILL_HORIZONTAL);
    gh.add(pathList                                       ,0,3,4,1,GridBagHelper.GROWFILL_BOTH);
    gh.add(new JButton(up)                                ,0,4,1,1,GridBagHelper.FILL_HORIZONTAL);
    gh.add(new JButton(dn)                                ,1,4,1,1,GridBagHelper.FILL_HORIZONTAL);
    gh.add(new JButton(del)                               ,2,4,1,1,GridBagHelper.FILL_HORIZONTAL);

  }

  
  private class Move extends Action2 {
    
    private boolean up;
    
    protected Move(boolean up) {
      this.up=up;
      setEnabled(false);
      if (up) setText(resources, "info.up");
      else setText(resources, "info.down");
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
      int i = pathList.getSelectedIndex();
      if (up)
        pathList.swapChoices(i,i-1);
      else 
        pathList.swapChoices(i,i+1);
    }
  }
  
  
  private class Del extends Action2 {
    
    protected Del() {
      setEnabled(false);
      setText(resources, "info.del");
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
      pathTree.setSelected(pathList.getSelectedChoice(), false);
    }
  } 
}
