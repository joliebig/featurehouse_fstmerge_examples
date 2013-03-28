
package genj.nav;

import genj.app.PluginFactory;
import genj.app.Workbench;
import genj.gedcom.Context;
import genj.gedcom.Indi;
import genj.gedcom.PropertySex;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.Action2.Group;
import genj.view.ActionProvider;
import genj.view.SelectionSink;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NavigatorPluginFactory implements PluginFactory, ActionProvider {
  
  private final static Resources RES = Resources.get(NavigatorPluginFactory.class);
  
  private final static ImageIcon 
    PARENTS = new ImageIcon(NavigatorPluginFactory.class , "Parents.png" ),
    SIBLINGS = new ImageIcon(NavigatorPluginFactory.class, "Siblings.png"),
    HUSBAND = new ImageIcon(NavigatorPluginFactory.class , "Husband.png" ),
    WIFE = new ImageIcon(NavigatorPluginFactory.class    , "Wife.png"    ),
    CHILDREN = new ImageIcon(NavigatorPluginFactory.class, "Children.png");
  
  @Override
  public Object createPlugin(Workbench workbench) {
    return this;
  }


  @Override
  public void createActions(Context context, Purpose purpose, Group into) {
    
    if (purpose != Purpose.TOOLBAR)
      return;

    if (context.getEntities().size()!=1||!(context.getEntity() instanceof Indi)) {
      into.add(new Goto(PARENTS));
      into.add(new Goto(SIBLINGS));
      into.add(new Goto(HUSBAND));
      into.add(new Goto(CHILDREN));
      return;
    }
    
    Indi indi = (Indi)context.getEntity();
    
    into.add(new Parents(indi));
    into.add(new Siblings(indi));
    into.add(new Spouses(indi));
    into.add(new Children(indi));
    
  }
  
  private class Goto extends Action2 {
    protected Indi target;
    
    public Goto(ImageIcon img) {
      setImage(img);
      setEnabled(false);
    }
    
    public Goto(String key, List<Indi> targets, ImageIcon img, int max) {
      init(key,targets,img,max);
    }
      
    public Goto(String key, List<Indi> targets, List<Indi> moreTargets, ImageIcon img, int max) {
      targets = new ArrayList<Indi>(targets);
      targets.addAll(moreTargets);
      init(key,targets,img,max);
    }
    
    private void init(String key, List<Indi> targets, ImageIcon img, int max) {
      
      StringBuffer tip = new StringBuffer();
      tip.append("<html>");
      tip.append(RES.getString("goto", RES.getString(key)));
      tip.append("<b>");
      
      for (int i=0;i<targets.size();i++) {
        tip.append("<br/>");
        tip.append(targets.get(i));
        if (i==0)
          tip.append("</b>");
      }
      setTip(tip.toString());
      setImage(img);
      if (!targets.isEmpty())
        target = targets.get(0);
      else
        setEnabled(false);
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      if (target!=null)
        SelectionSink.Dispatcher.fireSelection(DialogHelper.getComponent(e), new Context(target), false);
    }
  }
  
  private class Parents extends Goto {
    public Parents(Indi indi) {
      super("parents", indi.getParents(),PARENTS, 2);
    }
  }

  private class Siblings extends Goto {
    public Siblings(Indi indi) {
      super("siblings", Arrays.asList(indi.getYoungerSiblings()), Arrays.asList(indi.getOlderSiblings()),SIBLINGS,-1);
    }
  }

  private class Spouses extends Goto {
    public Spouses(Indi indi) {
      super("spouses", Arrays.asList(indi.getPartners()), indi.getSex() == PropertySex.FEMALE ? HUSBAND : WIFE,-1);
    }
  }
  
  private class Children extends Goto {
    public Children(Indi indi) {
      super("children", Arrays.asList(indi.getChildren()),CHILDREN,-1);
    }
  }

}
