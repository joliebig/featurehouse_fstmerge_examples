
package genj.view;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

  
public class ViewContext extends Context implements Comparable<ViewContext> {
  
  private List<Action2> actions = new ArrayList<Action2>();
  private ImageIcon img = null;
  private String txt = null;
  
  
  public ViewContext(String text, Context context) {
    super(context);
    setText(text);
  }
  
  
  public ViewContext(String text, ImageIcon img, Context context) {
    super(context);
    setText(text);
    setImage(img);
  }
  
  
  public ViewContext(Context context) {
    super(context);
  }
  
  
  public ViewContext(Gedcom gedcom, List<Entity> entities, List<Property> properties) {
    super(gedcom, entities, properties);
  }
  
  
  public ViewContext(Gedcom ged) {
    super(ged);
  }
  
  
  public ViewContext(Property prop) {
    super(prop);
  }
  
  
  public ViewContext(Entity entity) {
    super(entity);
  }
  
  
  public ViewContext addAction(Action2 action) {
    actions.add(action);
    return this;
  }
  
  
  public ViewContext addActions(Action2.Group group) {
    actions.add(group);
    return this;
  }
  
  
  public List<Action2> getActions() {
    return Collections.unmodifiableList(actions);
  }
  
  
  public String getText() {

    if (txt!=null)
      return txt;

    List<? extends Property> ps = getProperties();
    List<? extends Entity> es = getEntities();
    if (ps.size()==1) 
      txt = Gedcom.getName(ps.get(0).getTag()) + "/" + ps.get(0).getEntity();
    else if (!ps.isEmpty())
      txt = Property.getPropertyNames(ps, 5);
    else  if (es.size()==1)
      txt = es.get(0).toString();
    else if (!es.isEmpty())
      txt = Entity.getPropertyNames(es, 5);
    else txt = getGedcom().getName();

    return txt;
  }

  
  public ViewContext setText(String text) {
    txt = text;
    return this;
  }

  
  public ImageIcon getImage() {
    
    if (img!=null)
      return img;
    
    if (getProperties().size()==1)
      img = getProperties().get(0).getImage(false);
    else if (getEntities().size()==1)
      img = getEntities().get(0).getImage(false);
    else img = Gedcom.getImage();
    return img;
  }

  
  public ViewContext setImage(ImageIcon set) {
    img = set;
    return this;
  }

  
  public int compareTo(ViewContext that) {
    if (this.txt==null)
      return -1;
    if (that.txt==null)
      return 1;
    return this.txt.compareTo(that.txt);
  }
  
} 
