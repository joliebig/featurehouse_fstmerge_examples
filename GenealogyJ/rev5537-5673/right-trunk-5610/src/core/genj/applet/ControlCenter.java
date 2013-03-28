
package genj.applet;

import genj.gedcom.Gedcom;
import genj.util.GridBagHelper;
import genj.util.MnemonicAndText;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.LinkWidget;
import genj.view.ViewFactory;
import genj.view.ViewManager;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class ControlCenter extends JPanel {

  
  private ViewManager viewManager;
  
  
  private Gedcom gedcom;

  
  public ControlCenter(ViewManager vmanager, Gedcom ged) {
    
    
    viewManager = vmanager;
    gedcom = ged;
    
    
    GridBagHelper gh = new GridBagHelper(this);
    gh.add(getHeaderPanel() ,1,1);
    gh.add(getLinkPanel()   ,1,2);

    
    setBackground(Color.white);

    
  }

  
  private JPanel getHeaderPanel() {
    
    JPanel p = new JPanel(new GridLayout(2,1));
    p.setOpaque(false);
    p.add(new JLabel(gedcom.getOrigin().getFileName(), SwingConstants.CENTER));
    
    WordBuffer words = new WordBuffer();
    words.append(gedcom.getEntities(Gedcom.INDI).size()+" "+Gedcom.getName(Gedcom.INDI, true));
    words.append(gedcom.getEntities(Gedcom.FAM ).size()+" "+Gedcom.getName(Gedcom.FAM , true));
    
    p.add(new JLabel(words.toString(), SwingConstants.CENTER));
    
    return p;
  }
  

  
  private JPanel getLinkPanel() {

    
    ViewFactory[] vfactories = viewManager.getFactories();

    
    JPanel p = new JPanel(new GridLayout(vfactories.length, 1));
    p.setOpaque(false);
    
    for (int v=0; v<vfactories.length; v++) {
      p.add(new LinkWidget(new ActionView(vfactories[v])));
    }
    
    
    return p;
  }
  

  
  private class ActionView extends Action2 {
    
    private ViewFactory factory;
    
    private ActionView(ViewFactory vfactory) {
      factory = vfactory;
      setText(new MnemonicAndText(vfactory.getTitle(false)).getText());
      setImage(vfactory.getImage());
    }
    
    protected void execute() {
      viewManager.openView(gedcom, factory);
    }
  } 
  
  

} 