
package genj.tree;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomListenerAdapter;
import genj.gedcom.GedcomMetaListener;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import gj.layout.LayoutException;
import gj.layout.tree.TreeLayout;
import gj.model.Node;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import spin.Spin;


 class Model {
  
  
  private Callback callback = new Callback();
  
  
  private List listeners = new ArrayList(3);

  
  private Collection arcs = new ArrayList(100);

  
  private Map entities2nodes = new HashMap(100);
  private Collection nodes = new ArrayList(100);

  
  private Rectangle bounds = new Rectangle();
  
  
  private GridCache cache = null;
  
  
  private boolean isVertical = true;
  
  
  private boolean isFamilies = true;
  
  
  private boolean isBendArcs = true;
  
  
  private boolean isMarrSymbols = true;
  
  
  private boolean isFoldSymbols = true;
  
  
  private Set hideAncestors = new HashSet();

  
  private Set hideDescendants = new HashSet();
  
  
  private Map indi2fam = new HashMap();

  
  private Gedcom gedcom;
  
  
  private Entity root;

  
  private TreeMetrics metrics = new TreeMetrics( 60, 30, 30, 15, 10 );
  
  
  private LinkedList bookmarks = new LinkedList();
  
  
  public Model(Gedcom ged) {
    gedcom = ged;
  }
  
    public Gedcom getGedcom() {
    return gedcom;
  }
  
  
  public void setRoot(Entity entity) {
    
    if (!(entity instanceof Indi||entity instanceof Fam)) 
      return;
    
    if (root==entity) return;
    
    root = entity;
    
    update();
    
  }

  
  public Entity getRoot() {
    return root;
  }
    
  
  public boolean isVertical() {
    return isVertical;
  }
  
  
  public void setVertical(boolean set) {
    if (isVertical==set) return;
    isVertical = set;
    update();
  }
  
  
  public boolean isBendArcs() {
    return isBendArcs;
  }
  
  
  public void setBendArcs(boolean set) {
    if (isBendArcs==set) return;
    isBendArcs = set;
    update();
  }
  
  
  public boolean isFamilies() {
    return isFamilies;
  } 
  
  
  public void setFamilies(boolean set) {
    if (isFamilies==set) return;
    isFamilies = set;
    update();
  } 
  
  
  public boolean isMarrSymbols() {
    return isMarrSymbols;
  }

  
  public void setMarrSymbols(boolean set) {
    if (isMarrSymbols==set) return;
    isMarrSymbols = set;
    update();
  }

  
  public void setFoldSymbols(boolean set) {
    if (isFoldSymbols==set) return;
    isFoldSymbols = set;
    update();
  }

  
  public boolean isFoldSymbols() {
    return isFoldSymbols;
  }

    public TreeMetrics getMetrics() {
    return metrics;
  } 
  
  
  public void setMetrics(TreeMetrics set) {
    if (metrics.equals(set)) return;
    metrics = set;
    update();
  } 
  
  
  public void addListener(ModelListener l) {
    listeners.add(l);
    
    
    if (listeners.size()==1)
      gedcom.addGedcomListener((GedcomListener)Spin.over((GedcomListener)callback));
  }
  
  
  public void removeListener(ModelListener l) {
    listeners.remove(l);
    
    
    if (listeners.isEmpty())
      gedcom.removeGedcomListener((GedcomListener)Spin.over((GedcomListener)callback));
 }
  
  
  public Collection getNodesIn(Rectangle range) {
    if (cache==null) return new HashSet();
    return cache.get(range);
  }

  
  public Collection getArcsIn(Rectangle range) {
    return arcs;
  }

  
  public TreeNode getNodeAt(int x, int y) {
    
    if (cache==null) return null;
    
    int
      w = Math.max(metrics.wIndis, metrics.wFams),
      h = Math.max(metrics.hIndis, metrics.hFams);
    Rectangle range = new Rectangle(x-w/2, y-h/2, w, h);
    
    Iterator it = cache.get(range).iterator();
    while (it.hasNext()) {
      TreeNode node = (TreeNode)it.next();
      Shape shape = node.getShape();
      if (shape!=null&&shape.getBounds2D().contains(x-node.pos.x,y-node.pos.y))
        return node;
    }
    
    
    return null;
  }
   
  
  public Object getContentAt(int x, int y) {
    TreeNode node = getNodeAt(x, y);
    return node!=null ? node.getContent() : null;
  }

  
  public Entity getEntityAt(int x, int y) {
    Object content = getContentAt(x, y);
    return content instanceof Entity ? (Entity)content : null;
  }
  
  
  public TreeNode getNode(Entity e) {
    return (TreeNode)entities2nodes.get(e);
  }
  
  
  public Rectangle getBounds() {
    return bounds;
  }

  
  public void addBookmark(Bookmark b) {
    bookmarks.addFirst(b);
    if (bookmarks.size()>16) bookmarks.removeLast();
  }
  
  
  public List getBookmarks() {
    return Collections.unmodifiableList(bookmarks);
  }
  
  
  public void setBookmarks(List set) {
    bookmarks.clear();
    bookmarks.addAll(set);
  }
  
  
  public Collection getHideAncestorsIDs() {
    return getIds(hideAncestors);
  }
  
  
  public void setHideAncestorsIDs(Collection ids) {
    hideAncestors.clear();
    hideAncestors.addAll(getEntities(ids));
  }

  
  public Collection getHideDescendantsIDs() {
    return getIds(hideDescendants);
  }
  
  
  public void setHideDescendantsIDs(Collection ids) {
    hideDescendants.clear();
    hideDescendants.addAll(getEntities(ids));
  }

    
  private Collection getIds(Collection entities) {
    List result = new ArrayList();
    Iterator es = entities.iterator();
    while (es.hasNext()) {
      Entity e = (Entity)es.next();
      result.add(e.getId());
    }
    return result;    
  }
  
    
  private Collection getEntities(Collection ids) {
    List result = new ArrayList();
    for (Iterator it = ids.iterator(); it.hasNext(); ) {
      Entity e = gedcom.getEntity(it.next().toString());
      if (e!=null) result.add(e);
    }
    return result;
  }
  
  
   boolean isHideDescendants(Indi indi) {
    return hideDescendants.contains(indi);
  }
  
  
   boolean isHideAncestors(Indi indi) {
    return hideAncestors.contains(indi);
  }
  
  
   Fam getFamily(Indi indi, Fam fams[], boolean next) {
    
    if (fams.length>0) {
      
      Fam fam = (Fam)indi2fam.get(indi);
      if (fam==null) fam = fams[0];
      for (int f=0;f<fams.length;f++) {
        if (fams[f]==fam) 
          return fams[(f+(next?1:0))%fams.length];
      }
      
      indi2fam.remove(indi);
    }
    
    return fams[0];
  }
  
     TreeNode add(TreeNode node) {
    
    Object content = node.getContent();
    if (content instanceof Entity) {
      entities2nodes.put(content, node);
    }
    nodes.add(node);
    return node;
  }
  
     TreeArc add(TreeArc arc) {
    arcs.add(arc);
    return arc;
  }
  
  
   Set getEntities() {
    return entities2nodes.keySet();
  }

    private void update() {
    
    
    arcs.clear();
    nodes.clear();
    entities2nodes.clear();
    bounds.setFrame(0,0,0,0);
    
    
    if (root==null) return;

    
    try {
      
      boolean isFams = isFamilies || root instanceof Fam;
      
      Parser descendants = Parser.getInstance(false, isFams, this, metrics);
      bounds.add(layout(descendants.parse(root), true));
      
      bounds.add(layout(descendants.align(Parser.getInstance(true, isFams, this, metrics).parse(root)), false));
    } catch (LayoutException e) {
      e.printStackTrace();
      root = null;
      update();
      return;
    }
    
    
    cache = new GridCache(
      bounds, 3*metrics.calcMax()
    );
    Iterator it = nodes.iterator();
    while (it.hasNext()) {
      TreeNode n = (TreeNode)it.next();
      if (n.shape!=null) cache.put(n, n.shape.getBounds(), n.pos);
    }
    
    
    fireStructureChanged();
    
  }

  
  private Rectangle layout(TreeNode root, boolean isTopDown) throws LayoutException {
    
    
    double theta = 0;
    if (!isTopDown) theta += 180;
    if (!isVertical) theta -= 90;
    
    
    TreeLayout layout = new TreeLayout();
    layout.setBendArcs(isBendArcs);
    layout.setDebug(false);
    layout.setIgnoreUnreachables(true);
    layout.setBalanceChildren(false);
    layout.setRoot(root);
    layout.setOrientation(theta);
    
    
    return layout.layout(root, nodes.size()).getBounds();
  }
  
  
  
  private void fireStructureChanged() {
    for (int l=listeners.size()-1; l>=0; l--) {
      ((ModelListener)listeners.get(l)).structureChanged(this);
    }
  }
  
  
  private void fireNodesChanged(Collection nodes) {
    for (int l=listeners.size()-1; l>=0; l--) {
      ((ModelListener)listeners.get(l)).nodesChanged(this, nodes);
    }
  }
  
  
   class NextFamily implements Runnable {
    
    private Indi indi;
    
    private Fam fam;
    
    protected NextFamily(Indi individual, Fam[] fams) {
      indi = individual;
      fam = getFamily(indi, fams, true);
    }
    
    public void run() {
      indi2fam.put(indi, fam);
      update();
    }
  } 
  
  
   class FoldUnfold implements Runnable {
    
    private Indi indi;
    
    private Set set;
    
    protected FoldUnfold(Indi individual, boolean ancestors) {
      indi = individual;
      set = ancestors ? hideAncestors : hideDescendants; 
    }
    
    public void run() {
      if (!set.remove(indi)) set.add(indi);
      update();
    }
  } 

  
  private class Callback extends GedcomListenerAdapter implements GedcomMetaListener {
    
    private Set repaint = new HashSet();
    private boolean update = false;
    private Entity added;
    
    public void gedcomWriteLockAcquired(Gedcom gedcom) {
      added = null;
      repaint.clear();
    }
    
    public void gedcomWriteLockReleased(Gedcom gedcom) {
        
      
      if (root==null) {
        if (added==null||!gedcom.contains(added))
          added = gedcom.getFirstEntity(Gedcom.INDI);
        root = added;
        update();
        return;
      }
      
      
      if (update) {
        update();
        return;
      }

      
      if (!repaint.isEmpty()) 
        fireNodesChanged(repaint);
   
    }
    
    public void gedcomEntityAdded(Gedcom gedcom, Entity added) {
      if (added instanceof Fam || added instanceof Indi) {
        if ( !(this.added instanceof Indi) || added instanceof Indi)
          this.added = added;
      }
    }
  
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      
      if (entity == root) 
        root = null;
      
      
      ListIterator it = bookmarks.listIterator();
      while (it.hasNext()) {
        Bookmark b = (Bookmark)it.next();
        if (entity == b.getEntity()) it.remove();
      }
      
      
      indi2fam.keySet().remove(entity);
      
    }
  
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      gedcomPropertyChanged(gedcom, added);
    }
  
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
      
      if (property instanceof PropertyXRef) {
        update = true;
        return;
      }
      
      Node node = getNode(property.getEntity());
      if (node!=null) 
        fireNodesChanged(Collections.singletonList(node));
    }
  
    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
      
      if (deleted instanceof PropertyXRef)
        update = true;
      
      if (root!=null)
        repaint.add(getNode(property.getEntity()));
    }
  } 
} 
