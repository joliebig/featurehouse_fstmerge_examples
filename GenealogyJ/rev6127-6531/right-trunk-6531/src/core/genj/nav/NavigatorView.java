
package genj.nav;

import genj.edit.actions.CreateChild;
import genj.edit.actions.CreateParent;
import genj.edit.actions.CreateSibling;
import genj.edit.actions.CreateSpouse;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.Indi;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.NestedBlockLayout;
import genj.view.SelectionSink;
import genj.view.View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import spin.Spin;


public class NavigatorView extends View {

  private final static String INDENT = "   ";
  private final static Resources RES = Resources.get(NavigatorView.class);
  private final static Registry REG = Registry.get(NavigatorView.class);
  
  private final static NestedBlockLayout LAYOUT = new NestedBlockLayout("<col>"
      +"<col><grandparents/><grandparent/></col>"
      +"<col><parents/><parent/></col>"
      +"<col><siblings/><sibling/></col>"
      +"<line/>"
      +"<col><spouses/><spouse/></col>"
      +"<line/>"
      +"<col><children/><child/></col>"
      +"<col><grandchildren/><grandchild/></col>"
      +"</col>");
  
  private GedcomListener callback = (GedcomListener)Spin.over(new GedcomListenerAdapter() {
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      if (context!=null&&context.getEntity()==entity)
        setContext(new Context(gedcom), true);
    }
  });
  
  
  private Context context = new Context();
  
  private JPanel content = new JPanel(LAYOUT);

  
  public NavigatorView() {
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    content.setBackground(Color.WHITE);
  }

  
  @Override
  public void setContext(Context context, boolean isActionPerformed) {
    
    
    if (this.context.getGedcom()!=null) {
      this.context.getGedcom().removeGedcomListener(callback);
      this.context = new Context();
    }
    
    content.removeAll();

    
    if (context.getEntities().size()==1) {
      if (context.getEntity() instanceof Indi) {
        setIndi((Indi)context.getEntity());
        addLine("line");
        addLine("line");
      }
      if (context.getEntity() instanceof Fam)
        setFam((Fam)context.getEntity());
      
    }

    
    revalidate();
    repaint();
  }
  
  private void addLine(String key) {
    JComponent line = new Box.Filler(new Dimension(1,1), new Dimension(1,1), new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    Color c = content.getForeground();
    line.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
    line.setOpaque(true);
    content.add(key, line);
  }
  
  private void addExpander(String key, int count) {
    String label = RES.getString(key);
    NestedBlockLayout.Expander expander = new NestedBlockLayout.Expander(label,label+" ("+count+")");
    Registry r = new Registry(REG, key);
    expander.setCollapsed(r.get("folded", false));
    expander.addPropertyChangeListener("folded", r);
    content.add(key, expander);
  }
  
  private void setFam(Fam fam) {

    int count = 0;
    Indi husband = fam.getHusband();
    if (husband!=null) {
      content.add("parent"       , indi(husband));
      count++;
    }
    Indi wife = fam.getWife();
    if (wife!=null) {
      content.add("parent"       , indi(wife));
      count++;
    }
    if (husband==null||wife==null)
      content.add("parent"       , create(new CreateParent(fam)));

    Indi[] children = fam.getChildren();
    for (Indi child : children)
      content.add("child"       , indi(child));
    content.add("child"         , create(new CreateChild(fam, true)));
    
    addExpander("parents", count);
    addExpander("children", children.length);
  }    
  
  private void setIndi(Indi indi) {
  
    
    context = new Context(indi);
    context.getGedcom().addGedcomListener(callback);
    
    List<Indi> grandparents = getParents(indi.getParents());
    addExpander("grandparents", grandparents.size());
    for (Indi grandparent : grandparents)
      content.add("grandparent", indi(grandparent));

    List<Indi> parents = indi.getParents();
    addExpander("parents", parents.size());
    for (Indi parent : parents)
      content.add("parent", indi(parent));
    if (parents.size()<2)
      content.add("parent", create(new CreateParent(indi)));
    
    Indi[] siblings = indi.getSiblings(false);
    addExpander("siblings", siblings.length);
    for (Indi sibling : siblings)
      content.add("sibling", indi(sibling));
    content.add("sibling", create(new CreateSibling(indi, true)));

    Indi[] spouses = indi.getPartners();
    addExpander("spouses", spouses.length);
    for (Indi spouse : spouses)
      content.add("spouse", indi(spouse));
    if (spouses.length==0)
      content.add("spouse", create(new CreateSpouse(indi)));

    Indi[] children = indi.getChildren();
    addExpander("children", children.length);
    for (Indi child : children)
      content.add("child", indi(child));
    content.add("child", create(new CreateChild(indi, true)));
    
    List<Indi> grandchildren = getChildren(Arrays.asList(children));
    addExpander("grandchildren", grandchildren.size());
    for (Indi grandchild : grandchildren)
      content.add("grandchild", indi(grandchild));
    
  }
  
  private JLabel create(Action action) {
    JLabel result = new JLabel("["+RES.getString("create")+"]");
    Color c = result.getForeground();
    result.setForeground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
    result.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    result.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    result.putClientProperty(Action.class, action);
    result.addMouseListener(CLICK);
    result.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return result;
  }
  
  private List<Indi> getParents(List<Indi> parents) {
    List<Indi> result = new ArrayList<Indi>(4);
    for (Indi parent : parents)
      result.addAll(parent.getParents());
    return result;
  }
  
  private List<Indi> getChildren(List<Indi> children) {
    List<Indi> result = new ArrayList<Indi>(16);
    for (Indi child : children)
      result.addAll(Arrays.asList(child.getChildren()));
    return result;
  }
  
  private JLabel indi(Indi indi) {
    JLabel result = new JLabel(indi.toString(), indi.getImage(), SwingConstants.LEFT);
    result.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    result.putClientProperty(Indi.class, indi);
    result.addMouseListener(CLICK);
    result.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return result;
  }

  private final static MouseListener CLICK = new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
      
      JLabel label = (JLabel)e.getComponent();
      Indi target = (Indi)label.getClientProperty(Indi.class);
      if (target!=null)
        SelectionSink.Dispatcher.fireSelection(e, new Context(target));
      Action action = (Action)label.getClientProperty(Action.class);
      if (action!=null)
        action.actionPerformed(new ActionEvent(e.getSource(), 0, "", e.getModifiers()));

    }
  };

} 
