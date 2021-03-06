
package genj.nav;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.Indi;
import genj.gedcom.PropertySex;
import genj.util.GridBagHelper;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.view.View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import spin.Spin;


public class NavigatorView extends View {
  
  private static Resources resources = Resources.get(NavigatorView.class);

  private final static String 
    FATHER   = "father",
    MOTHER   = "mother",
    YSIBLING = "ysibling",
    OSIBLING = "osibling",
    PARTNER  = "partner",
    CHILD    = "child";

  private final static ImageIcon
    imgYSiblings = new ImageIcon(NavigatorView.class,"YSiblings"),
    imgOSiblings = new ImageIcon(NavigatorView.class,"OSiblings"),
    imgChildren  = new ImageIcon(NavigatorView.class,"Children"),
    imgFather    = new ImageIcon(NavigatorView.class,"Father"),
    imgMother    = new ImageIcon(NavigatorView.class,"Mother"),
    imgMPartner  = Indi.IMG_MALE,
    imgFPartner  = Indi.IMG_FEMALE;


  private GedcomListener callback = new GedcomListenerAdapter() {
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      if (current == entity) {
        setCurrentEntity(gedcom.getFirstEntity(Gedcom.INDI));
      } else {
        setCurrentEntity(current);
      }
    }
  };
  
  
  private JLabel labelCurrent, labelSelf;
  
  
  private Indi current;
  
  
  private Map key2jumps = new HashMap();
  
  
  private Map key2popup = new HashMap();
  
  
  private Gedcom gedcom;
  
  private Registry registry;
  
  
  public NavigatorView(String title, Context context, Registry registry) {
    
    
    this.gedcom = context.getGedcom();
    this.registry = registry;
    
    
    setLayout(new BorderLayout());

    labelCurrent = new JLabel();
    labelCurrent.setBorder(BorderFactory.createTitledBorder(Gedcom.getName(Gedcom.INDI,false)));
    add(labelCurrent,BorderLayout.NORTH);
    add(new JScrollPane(createPopupPanel()),BorderLayout.CENTER);
    
    
    Entity entity = context.getEntity();
    if (entity!=null) 
      setCurrentEntity(entity);
    








    

  }





























  public void addNotify() {
    
    super.addNotify();
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(callback));
  }
  
  
  public void removeNotify() {
    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(callback));
    
    if (current!=null)
      registry.put("entity", current.getId());
    
    
    super.removeNotify();
  }

  
  public Dimension getPreferredSize() {
    return new Dimension(140,200);
  }

  public void select(Context context, boolean isActionPerformed) {
    setCurrentEntity(context.getEntity());
  }
  
  
  public void setCurrentEntity(Entity e) {
    
    
    if (e==current || (e!=null&&!(e instanceof Indi)) ) 
      return;
    
    
    key2jumps.clear();
    
    
    current = (Indi)e;

    
    if (current == null) {
      
      setJump(FATHER  , null);
      setJump(MOTHER  , null);
      setJump(OSIBLING, null);
      setJumps(PARTNER , null);
      setJump(YSIBLING, null);
      setJumps(CHILD   , null);
      
      labelCurrent.setText("n/a");
      labelCurrent.setIcon(null);
    } else {
      
      setJump (FATHER  , current.getBiologicalFather());
      setJump (MOTHER  , current.getBiologicalMother());
      setJumps(OSIBLING, current.getOlderSiblings());
      setJumps(PARTNER , current.getPartners());
      setJumps(YSIBLING, current.getYoungerSiblings());
      setJumps(CHILD   , current.getChildren());
      
      labelCurrent.setText(current.toString());
      labelCurrent.setIcon(current.getImage(false));

      
      PopupWidget partner = getPopup(PARTNER);
      switch (current.getSex()) {
        case PropertySex.FEMALE:
          labelSelf.setIcon(imgFPartner);
          partner.setIcon(imgMPartner);
          break;
        case PropertySex.MALE:
          labelSelf.setIcon(imgMPartner);
          partner.setIcon(imgFPartner);
          break;
      }

    }
          
    
  }
  
  
  private PopupWidget getPopup(String key) {
    return (PopupWidget)key2popup.get(key);  
  }

  
  private void setJump(String key, Indi i) {
    setJumps(key, i==null ? new Indi[0] : new Indi[]{ i });
  }
  
  
  private void setJumps(String key, Indi[] is) {
    
    PopupWidget popup = getPopup(key);
    ArrayList jumps = new ArrayList();
    
    if (is==null||is.length==0) {
      popup.setEnabled(false);
    } else {
      popup.setEnabled(true);
      for (int i=0;i<is.length;i++) 
        jumps.add(new Jump(is[i]));
    }
    
    popup.setActions(jumps);
  }
    
  
  private JComponent createPopup(String key, ImageIcon i) {
    
    
    PopupWidget result = new PopupWidget();
    result.setIcon(i);
    result.setFocusPainted(false);
    result.setFireOnClick(true);
    result.setFocusable(false);
    result.setEnabled(false);

    result.setToolTipText(resources.getString("tip."+key));

    
    key2popup.put(key, result);
    
    
    return result;
  }

  
  private JPanel createPopupPanel() {    
    
    final String title = resources.getString("nav.navigate.title");
    final TitledBorder border = BorderFactory.createTitledBorder(title);
    final JPanel result = new PopupPanel();
    result.setBorder(border);
    GridBagHelper gh = new GridBagHelper(result);
    
    
    JComponent
      popFather   = createPopup(FATHER,   imgFather),
      popMother   = createPopup(MOTHER,   imgMother),
      popOSibling = createPopup(OSIBLING, imgOSiblings),
      popPartner  = createPopup(PARTNER,  imgMPartner),
      popYSibling = createPopup(YSIBLING, imgYSiblings),
      popChildren = createPopup(CHILD,    imgChildren); 

    labelSelf = new JLabel(Gedcom.getEntityImage(Gedcom.INDI),SwingConstants.CENTER);

    popPartner.setPreferredSize(popOSibling.getPreferredSize());
    popFather .setPreferredSize(popOSibling.getPreferredSize());
    popMother .setPreferredSize(popOSibling.getPreferredSize());
    labelSelf.setPreferredSize(popOSibling.getPreferredSize());
    
    gh.add(popFather  ,4,1,1,1);
    gh.add(popMother  ,5,1,1,1);
    gh.add(popOSibling,1,2,2,1,0,new Insets(12,0,12,12));
    gh.add(labelSelf  ,4,2,1,1);
    gh.add(popPartner ,5,2,1,1);
    gh.add(popYSibling,7,2,2,1,0,new Insets(12,12,12,0));
    gh.add(popChildren,4,3,2,1);

    
    return result;
  }
  
  
  private class PopupPanel extends JPanel {
    
    protected void paintChildren(Graphics g) {
    
      
      g.setColor(Color.lightGray);
      
      line(g,getPopup(MOTHER), getPopup(OSIBLING));
      line(g,getPopup(MOTHER), getPopup(YSIBLING));
      line(g,getPopup(MOTHER), labelSelf);
      line(g,getPopup(PARTNER), getPopup(CHILD));
          
      
      super.paintChildren(g);
    
      
    }
    
    
    private void line(Graphics g, JComponent c1, JComponent c2) {
      Rectangle
        a = c1.getBounds(),
        b = c2.getBounds();
      int y = (a.y+a.height+b.y)/2;
      int x = a.x;
      g.drawLine(x,a.y+a.height,x,y);
      x = b.x+b.width/2;
      g.drawLine(x,y,x,b.y);
      g.drawLine(a.x,y,x,y);

    }
  
  } 

  
  private class Jump extends Action2 {
    
    private Indi target;
    
    private Jump(Indi taRget) {
      
      target = taRget;
      
      setText(target.toString());
      setImage(target.getImage(false));
    }
    
    public void actionPerformed(ActionEvent event) {
      
      setCurrentEntity(target);
      
      View.fireSelection(NavigatorView.this, new Context(target), true);
    }
  } 

} 
