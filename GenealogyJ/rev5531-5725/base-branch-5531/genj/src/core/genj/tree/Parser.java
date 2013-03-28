
package genj.tree;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Indi;
import gj.awt.geom.Path;
import gj.layout.tree.Branch;
import gj.layout.tree.Orientation;
import gj.model.Node;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


 abstract class Parser {
  
  
  protected Model model;

  
  protected TreeMetrics metrics;
  
  
  protected Path shapeMarrs, shapeIndis, shapeFams, shapePlus, shapeMinus, shapeNext; 

  
  protected int[] padIndis, padMinusPlus; 
  
  private final static int MAX_GENERATION = 20;
  
  
  public static Parser getInstance(boolean ancestors, boolean families, Model model, TreeMetrics metrics) {
    if (ancestors) {
      if (families) return new AncestorsWithFams(model, metrics);
      return new AncestorsNoFams(model, metrics);
    } else {
      if (families) return new DescendantsWithFams(model, metrics);
      return new DescendantsNoFams(model, metrics);
    }
  }
  
  
  protected Parser(Model mOdel, TreeMetrics mEtrics) {
    
    
    model = mOdel;
    metrics = mEtrics;
    
    
    initEntityShapes();
    initFoldUnfoldShapes();
    initMarrShapes();
    
    
  }
   
  
  public final TreeNode parse(Entity root) {
    return (root instanceof Indi) ? parse((Indi)root) : parse((Fam )root);
  }
  
  
  public TreeNode align(TreeNode other) {
    return other;
  }
  
  
  protected abstract TreeNode parse(Indi indi);
  
  
  protected abstract TreeNode parse(Fam fam);
  
  
  private void initMarrShapes() {

    shapeMarrs = new Path();
    
    
    if (!model.isMarrSymbols()) { 
      shapeMarrs.append(new Rectangle2D.Double());
      return; 
    }

    
    int d = Math.min(metrics.wIndis/4, metrics.hIndis/4);
    
    
    Ellipse2D e = new Ellipse2D.Float(-d*0.3F,-d*0.3F,d*0.6F,d*0.6F);

    float 
      dx = model.isVertical() ? d*0.2F : d*0.0F,
      dy = model.isVertical() ? d*0.0F : d*0.2F;

    AffineTransform 
      at1 = AffineTransform.getTranslateInstance(-dx,-dy),
      at2 = AffineTransform.getTranslateInstance( dx, dy);

    shapeMarrs.append(e.getPathIterator(at1));
    shapeMarrs.append(e.getPathIterator(at2));
    
    
    
    Rectangle2D r = shapeMarrs.getBounds2D();
    if (model.isVertical()) {
      r.setRect(r.getMinX(), -metrics.hIndis/2-metrics.pad/2, r.getWidth(), metrics.hIndis+metrics.pad);
    } else {
      r.setRect(-metrics.wIndis/2-metrics.pad/2, r.getMinY(), metrics.wIndis+metrics.pad, r.getHeight());
    }
    shapeMarrs.setBounds2D(r);
   
    
  }

  
  private void initEntityShapes() {
    
    
    padIndis  = new int[] { 
      metrics.pad/2, 
      metrics.pad/2, 
      metrics.pad/2, 
      metrics.pad/2
    };
    
    
    shapeIndis = new Path().append(new Rectangle2D.Double(
      -metrics.wIndis/2,
      -metrics.hIndis/2,
       metrics.wIndis,
       metrics.hIndis
    ));
     
    
    shapeFams = new Path().append(new Rectangle2D.Double(
      -metrics.wFams/2,
      -metrics.hFams/2,
       metrics.wFams,
       metrics.hFams
    ));
     
    
  }
  
  
  private void initFoldUnfoldShapes() {
    
    
    padMinusPlus  = new int[]{  
      -padIndis[3], 
       padIndis[1], 
       padIndis[2], 
       padIndis[3]     
    };

    
    double d = 3;
    
    
    shapePlus = new Path();
    shapePlus.moveTo(new Point2D.Double( 0,-d*0.3));
    shapePlus.lineTo(new Point2D.Double( 0, d*0.3));
    shapePlus.moveTo(new Point2D.Double(-d*0.3, 0));
    shapePlus.lineTo(new Point2D.Double( d*0.3, 0));
    shapePlus.append(new Rectangle2D.Double(-d/2,-d/2,d,d));

    
    shapeMinus = new Path();
    shapeMinus.moveTo(new Point2D.Double(-d*0.3, 0));
    shapeMinus.lineTo(new Point2D.Double(+d*0.3, 0));
    shapeMinus.append(new Rectangle2D.Double(-d/2,-d/2,d,d));
    
    
    shapeNext = new Path();
    shapeNext .moveTo(new Point2D.Double(-d*0.3,-d*0.3));
    shapeNext .lineTo(new Point2D.Double(+d*0.3,     0));
    shapeNext .lineTo(new Point2D.Double(-d*0.3,+d*0.3));
    shapeNext .append(new Rectangle2D.Double(-d/2,-d/2,d,d));
    
    
  }
    
  
  protected TreeNode insertPlusMinus(Indi indi, TreeNode parent, boolean ancestors, boolean plus) {
    
    if (!model.isFoldSymbols()) return parent;
    
    TreeNode node = model.add(new TreeNode(model.new FoldUnfold(indi,ancestors), plus?shapePlus:shapeMinus, padMinusPlus));
    model.add(new TreeArc(parent, node, false));
    
    return node;
  }
  
  
  private static class AncestorsNoFams extends Parser {
    
    protected AncestorsNoFams(Model model, TreeMetrics metrics) {
      super(model, metrics);
    }
    
    protected TreeNode parse(Fam fam) {
      throw new IllegalArgumentException();
    }
    
    protected TreeNode parse(Indi indi) {
      return parse(indi, 0);
    }
    
    private TreeNode parse(Indi indi, int generation) {
      
      TreeNode node = model.add(new TreeNode(indi, shapeIndis, padIndis));
      
      Fam famc = indi.getFamilyWhereBiologicalChild();
      if (famc!=null) {
        
        if (generation>MAX_GENERATION||model.isHideAncestors(indi)) {
          insertPlusMinus(indi, node, true, true);
        } else {
          
          TreeNode minus = insertPlusMinus(indi, node, true, false);
          
          Indi wife = famc.getWife();
          Indi husb = famc.getHusband();
          if (wife!=null) model.add(new TreeArc(minus, parse(wife, generation+1), true));
          if (husb!=null) model.add(new TreeArc(minus, parse(husb, generation+1), true));
          
        }
      } 
      
      return node;
    }
  } 
   
  
  private static class AncestorsWithFams extends Parser {
    
    
    private int[] padFams;
      
    
    private int[] padHusband, padWife;
    
    
    private int offsetSpouse;
    
    
    protected AncestorsWithFams(Model model, TreeMetrics metrics) {
      super(model, metrics);
      
      
      padFams  = new int[]{  
         padIndis[0], 
         padIndis[1], 
         padIndis[2], 
        -(int)(metrics.pad*0.40),
      };

      padHusband = new int[]{
        padIndis[0],
        padIndis[1],
        0,
        padIndis[3]
      };

      padWife = new int[]{
        padIndis[0],
        0,
        padIndis[2],
        padIndis[3]
      };
      
      offsetSpouse = (model.isVertical() ? metrics.wIndis : metrics.hIndis) / 2;
      
      
    }
    
    protected TreeNode parse(Fam fam) {
      return parse(fam, 0);
    }
    
    private TreeNode parse(Fam fam, int generation) {

      
      TreeNode node = model.add(new TreeNode(fam, shapeFams, padFams));
      
      
      Indi wife = fam.getWife();
      Indi husb = fam.getHusband();

      
      TreeNode nWife = model.add(new TreeNode(wife, shapeIndis, padHusband));
      model.add(new TreeArc(node, parse(wife, nWife, hasParents(husb)?-offsetSpouse:0, generation+1), false)); 
      
      
      TreeNode nMarr = model.add(new TreeNode(null, shapeMarrs, null));
      model.add(new TreeArc(node, nMarr, false));
      
      
      TreeNode nHusb = model.add(new TreeNode(husb, shapeIndis, padWife));
      model.add(new TreeArc(node, parse(husb, nHusb, hasParents(wife)?+offsetSpouse:0, generation+1), false));
      
      
      return node;
    }
    
    
    protected TreeNode parse(Indi indi) {
      return parse(indi, model.add(new TreeNode(indi, shapeIndis, padIndis)), 0, 0);
    }
    
    
    private TreeNode parse(Indi indi, TreeNode nIndi, int align, int generation) {
      
      if (indi==null) return nIndi;
      
      Fam famc = indi.getFamilyWhereBiologicalChild();
      if (famc!=null) {
        
        if (generation>MAX_GENERATION||model.isHideAncestors(indi)) {
          insertPlusMinus(indi, nIndi, true, true);
        } else {
          
          
          TreeNode nMinus = insertPlusMinus(indi, nIndi, true, false);
          model.add(new TreeArc(nMinus, parse(famc, generation), true));
          
          
          nMinus.align = align;
        }
      }
      
      return nIndi;
    }
    
    
    private boolean hasParents(Indi indi) {
      if (indi==null) return false;
      if (model.isHideAncestors(indi)) return false;
      return indi.getFamiliesWhereChild()!=null;
    }
    
  } 
  
  
  private static class DescendantsNoFams extends Parser {
    
    protected DescendantsNoFams(Model model, TreeMetrics metrics) {
      super(model, metrics);
    }
    
    protected TreeNode parse(Indi indi) {
      return parse(indi, 0);
    }
    
    private TreeNode parse(Indi indi, int generation) {
      
      TreeNode node = model.add(new TreeNode(indi, shapeIndis, padIndis)); 
      
      Fam[] fams = indi.getFamiliesWhereSpouse();
      TreeNode pivot = node;
      

      List l = new ArrayList(fams.length);

      for (int f=0; f<fams.length; f++) {
        
        Indi[] children = fams[f].getChildren();
        for (int c=0; c<children.length; c++) {
          if (!l.contains(children[c])) {
              l.add(children[c]);
          
          if (node.getArcs().isEmpty()) {
            
            if (generation>MAX_GENERATION||model.isHideDescendants(indi)) {
              
              insertPlusMinus(indi, node, false, true);
              
              break;
            }
            
            pivot = insertPlusMinus(indi, node, false, false);
          }
          
          
          model.add(new TreeArc(pivot, parse(children[c], generation+1), true));       

          
          } 
        }
      }
      
      return node;
    }
    
    protected TreeNode parse(Fam fam) {
      throw new IllegalArgumentException();
    }
    
  } 
  
  
  private static class DescendantsWithFams extends Parser {
    
    
    private TreeNode origin;
  
    
    private int[] padFams;
    
    
    private int[] padHusband, padWife, padNext;
    
    
    private int offsetHusband;
      
    
    protected DescendantsWithFams(Model model, TreeMetrics metrics) {
      super(model, metrics);

      
      padFams  = new int[]{  
        -(int)(metrics.pad*0.4), 
         padIndis[1], 
         padIndis[2], 
         padIndis[3]     
      };
      
      padHusband = new int[]{
        padIndis[0],
        padIndis[1],
        0,
        padIndis[3]
      };

      padWife = new int[]{
        padIndis[0],
        0,
        padIndis[2],
        0
      };
      
      padNext = new int[] {
        padIndis[0],
        -padIndis[1],
        0,
        0
      };
      
      offsetHusband = model.isVertical() ? 
        - (metrics.wIndis + shapeMarrs.getBounds().width )/2 :
        - (metrics.hIndis + shapeMarrs.getBounds().height)/2;
        
      
    }
    
    
    public TreeNode align(TreeNode other) {
      other.getPosition().setLocation(origin.getPosition());    
      return other;
    }
  
    
    protected TreeNode parse(Indi indi) {
      
      TreeNode nPivot = model.add(new TreeNode(null, null, null));
      
      origin = parse(indi, nPivot, 0);
      
      return nPivot;
    }
    
    protected TreeNode parse(Fam fam) {
      
      
      TreeNode nFam = model.add(new TreeNode(fam, shapeFams, padIndis));
      
      
      Indi[] children = fam.getChildren();
      for (int c=0; c<children.length; c++) {
        
        parse(children[c], nFam, 0);       
         
      }

      
      origin = nFam;
      
      
      return nFam;
    }
        
    
    private TreeNode parse(Indi indi, TreeNode pivot, int generation) {

      
      Fam[] fams = indi.getFamiliesWhereSpouse();
      
      
      if (fams.length==0) {
        TreeNode nIndi = model.add(new TreeNode(indi,shapeIndis,padIndis));
        model.add(new TreeArc(pivot, nIndi, pivot.getShape()!=null));
        return nIndi;        
      }

      
      Fam fam = model.getFamily(indi, fams, false);
      
      
      TreeNode nIndi = model.add(new TreeNode(indi,shapeIndis,padHusband) {
        
        public int getLongitude(Node node, Branch[] children, Orientation o) {
          return super.getLongitude(node, children, o) + offsetHusband;
        }
      });
      model.add(new TreeArc(pivot, nIndi, pivot.getShape()!=null));
      
      
      TreeNode nMarr = model.add(new TreeNode(null, shapeMarrs, null));
      model.add(new TreeArc(pivot, nMarr, false));
      
      
      TreeNode nSpouse = model.add(new TreeNode(fam.getOtherSpouse(indi), shapeIndis, padWife));
      model.add(new TreeArc(pivot, nSpouse, false));
      
      
      if (fams.length>1&&model.isFoldSymbols()) {
        TreeNode nNext = model.add(new TreeNode(model.new NextFamily(indi,fams), shapeNext, padNext));
        model.add(new TreeArc(pivot, nNext, false));
      }
            
      
      TreeNode nFam = model.add(new TreeNode(fam, shapeFams, padFams));
      model.add(new TreeArc(nIndi, nFam, false));
      
      
      Indi[] children = fam.getChildren();
      for (int c=0; c<children.length; c++) {
        
        
        if (c==0) {
          if (generation>MAX_GENERATION||model.isHideDescendants(indi)) {
            insertPlusMinus(indi, nFam, false, true);
            break;
          }
          nFam = insertPlusMinus(indi, nFam, false, false);
        }

        
        parse(children[c], nFam, generation+1);
        
        
      }
      
      
      return nIndi;
    }
    
  } 
  

} 

