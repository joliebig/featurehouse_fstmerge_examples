
package gj.shell;

import gj.geom.ShapeHelper;
import gj.io.GraphReader;
import gj.io.GraphWriter;
import gj.layout.GraphLayout;
import gj.layout.LayoutContext;
import gj.layout.LayoutException;
import gj.shell.factory.AbstractGraphFactory;
import gj.shell.model.EditableGraph;
import gj.shell.model.EditableVertex;
import gj.shell.swing.Action2;
import gj.shell.swing.SwingHelper;
import gj.shell.util.Properties;
import gj.shell.util.ReflectHelper;
import gj.util.DefaultLayoutContext;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Shell {

  
  public final static Shape[] shapes = new Shape[] {
    new Rectangle2D.Double(-16,-16,32,32),
    ShapeHelper.createShape(23D,19D,1,1,new double[]{
      0,6,4,1,18,11,1,29,1,1,34,30,1,47,27,1,31,44,1,20,24,1,6,38,1,2,21,1,14,20,1,6,4
    }),
    ShapeHelper.createShape(15D,15D,1,1,new double[] {
      0,10,0,
      1,20,0,
       2,30,0,30,10, 
      1,30,20,
       2,20,20,20,30, 
      1,10,30,
       3,20,20,10,10,0,20, 
      1,0,10,
       3,-10,0,0,-10,10,0 
    }),
    new Ellipse2D.Double(-20,-10,40,20)
  };
  
  
  private JFrame frame;
  
  
  private EditableGraphWidget graphWidget;

  
  private LayoutWidget layoutWidget;
  
  
  private EditableGraph graph;
  
  
  private JTextArea logWidget;
  
  
  private boolean isDebug = false; 
  
  
  private boolean isAnimation = true;
  
  
  private Animation animation;
  
  
  private Properties properties = new Properties(Shell.class, "shell.properties");
  
  
  private AbstractGraphFactory[] factories = (AbstractGraphFactory[])properties.get("factory", new AbstractGraphFactory[0]);
  
  
  private GraphLayout[] layouts = (GraphLayout[])properties.get("layout", new GraphLayout[0]);
  
  private Logger logger = Logger.getLogger("genj.shell");
  
  
  public static void main(String[] args) {
    
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Throwable t) {
    }
    
    new Shell(args.length>0?args[0]:null, args.length>1?"-maximized".equals(args[1])?true:false:false);
  }
  
  
  private Shell(String preload, boolean maximized) {
    
    
    graphWidget = new EditableGraphWidget() {
      @Override
      protected void layoutConfiguredNotify(GraphLayout layout) {
        new ActionExecuteLayout().trigger();
      }
    };
    
    layoutWidget = new LayoutWidget();
    layoutWidget.setLayouts(layouts);
    layoutWidget.setBorder(BorderFactory.createEtchedBorder());
    layoutWidget.addActionListener(new ActionExecuteLayout());
    
    logWidget = new JTextArea(3, 0);
    logWidget.setEditable(false);
    logger.setLevel(Level.ALL);
    logger.addHandler(new Handler() {
      @Override
      public void publish(LogRecord record) {
        String msg = record.getMessage();
        Object[] parms = record.getParameters();
        if (parms!=null&&parms.length==0)
          msg = MessageFormat.format(msg, parms);
        logWidget.append(String.format("%-8s%s\n",record.getLevel(), msg));
      }
      @Override
      public void close() throws SecurityException { }
      @Override
      public void flush() { }
    });
    
    
    frame = new JFrame("GraphJ - Shell") {
      @Override
      public void dispose() {
        super.dispose();
        System.exit(0);
      }
    };
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    Container container = frame.getContentPane();
    container.setLayout(new BorderLayout());
    container.add(new JScrollPane(graphWidget), BorderLayout.CENTER);
    container.add(layoutWidget, BorderLayout.EAST  );
    container.add(new JScrollPane(logWidget), BorderLayout.SOUTH );
    
    
    frame.getRootPane().setDefaultButton(layoutWidget.getDefaultButton());
    
    
    frame.setJMenuBar(createMenu());
    
    
    frame.setBounds(128,128,480,480);
    if (maximized)
    	frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setVisible(true);
    
    
    if (preload!=null) { 
      SwingUtilities.invokeLater(new ActionLoadGraph(new File(preload)));
    } else {
      setGraph(factories[0]);
    }
    
    
  }
  
  
  private JMenuBar createMenu() {

    
    JMenu mNew = new JMenu("New");
    for (int i=0;i<factories.length;i++) {
      mNew.add(new ActionNewGraph(factories[i]));
    }
    
    JMenu mGraph = new JMenu("Graph");
    mGraph.add(mNew);
    mGraph.add(new ActionLoadGraph());
    mGraph.add(new ActionSaveGraph());
    mGraph.add(new ActionCloseGraph());

    
    JMenu mLayout = new JMenu("Layout");
    for (int i=0;i<layouts.length;i++) {
      mLayout.add(new ActionSelectLayout(layouts[i]));
    }
    
    
    JMenu mOptions = new JMenu("Options");
    mOptions.add(SwingHelper.getCheckBoxMenuItem(new ActionToggleAntialias()));
    mOptions.add(SwingHelper.getCheckBoxMenuItem(new ActionToggleAnimation()));
    mOptions.add(SwingHelper.getCheckBoxMenuItem(new ActionDebug()));
    
    
    JMenuBar result = new JMenuBar();
    result.add(mGraph);
    result.add(mLayout);
    result.add(mOptions);

    
    return result;
  }
  
  
  private void setGraph(EditableGraph set) {
    
    graph = set;
    
    graphWidget.setGraph2D(graph);
    layoutWidget.setEnabled(graph!=null);
  }
  
  
  private void setGraph(AbstractGraphFactory factory) {
    
    if (PropertyWidget.hasProperties(factory)) {
      PropertyWidget content = new PropertyWidget().setInstance(factory);
      int rc = SwingHelper.showDialog(graphWidget, "Graph Properties", content, SwingHelper.DLG_OK_CANCEL);
      if (SwingHelper.OPTION_OK!=rc) 
        return;
      content.commit();
    }
    
    factory.setNodeShape(shapes[0]);
    setGraph(factory.create(createGraphBounds()));
    
  }
  
  
  private Rectangle createGraphBounds() {
    return new Rectangle(0,0,graphWidget.getWidth()-32,graphWidget.getHeight()-32);
  }
  
  
   class ActionDebug extends Action2 {
    ActionDebug() {
      super.setName("Debug");
    }
    @Override
    protected void execute() throws Exception {
      isDebug = !isDebug;
    }
    @Override
    public boolean isSelected() {
      return isDebug;
    }
  }
  
  
   class ActionExecuteLayout extends Action2 {
    
    
    private Animation animation;
    private Collection<Shape> debugShapes;
    private Shape shape;

    
     ActionExecuteLayout() {
      super.setAsync(ASYNC_SAME_INSTANCE);
    }
    
    
    @Override
    protected boolean preExecute() throws LayoutException {
      
      if (graph==null) 
        return false;
      
      cancel(true);
      
      if (isAnimation) {
        animation = new Animation(graph, 1000);
        animation.beforeLayout();
      }
      
      for (EditableVertex v : graph.getVertices()) 
        graph.setShape(v, v.getOriginalShape());
      
      GraphLayout layout = layoutWidget.getSelectedLayouts();
      debugShapes = isDebug ? new ArrayList<Shape>() : null;
      LayoutContext context = new DefaultLayoutContext(debugShapes, logger, createGraphBounds());
      logger.info("Starting graph layout "+layout.getClass().getSimpleName());
      shape = layout.apply(graph, context);
      logger.info("Finished graph layout "+layout.getClass().getSimpleName());
      
      if (isAnimation)
        animation.afterLayout();
      
      return isAnimation;
    }
    
    @Override
    protected void execute() throws LayoutException {
      try {
        while (true) {
          if (Thread.currentThread().isInterrupted()) break;
          boolean done = animation.animate();
          graphWidget.setGraph2D(graph);
          if (done) break;
          Thread.sleep(1000/60);
          shape = null;
        }
      } catch (InterruptedException e) {
        
      }
    }
    
    
    @Override
    protected void postExecute() throws Exception {
      if (graph!=null)
        graphWidget.setGraph2D(graph, shape!=null?shape.getBounds():null);
      graphWidget.setDebugShapes(debugShapes);
      graphWidget.setCurrentLayout(layoutWidget.getSelectedLayouts());
    }
    
  } 
  
  
   class ActionSelectLayout extends Action2 {
    private GraphLayout layout;
     ActionSelectLayout(GraphLayout set) {
      super(ReflectHelper.getName(set.getClass()));
      layout=set;
    }
    @Override
    protected void execute() { 
      layoutWidget.setSelectedLayout(layout); 
    }
  }

  
   class ActionLoadGraph extends Action2  {
    private File preset;
     ActionLoadGraph(File file) { 
      preset=file;
    }
     ActionLoadGraph() { super("Load"); }
    @Override
    protected void execute() throws IOException { 
      File file = preset;
      if (file==null) {
        JFileChooser fc = new JFileChooser(new File("./save"));
        if (JFileChooser.APPROVE_OPTION!=fc.showOpenDialog(frame)) return;
        file = fc.getSelectedFile();
      }
      setGraph(new GraphReader(new FileInputStream(file)).read());
    }
    
  } 

  
   class ActionSaveGraph extends Action2 {
     ActionSaveGraph() { 
      setName("Save"); 
    }
    @Override
    protected void execute() throws IOException { 
      JFileChooser fc = new JFileChooser(new File("./save"));
      if (JFileChooser.APPROVE_OPTION!=fc.showSaveDialog(frame)) return;
      new GraphWriter(new FileOutputStream(fc.getSelectedFile())).write(graph);
    }
  }

  
   class ActionCloseGraph extends Action2 {
     ActionCloseGraph() { super("Close"); }
    @Override
    protected void execute() { frame.dispose(); }
  }
  
  
   class ActionNewGraph extends Action2 {
    private AbstractGraphFactory factory;
     ActionNewGraph(AbstractGraphFactory factory) { 
      super(factory.toString());
      this.factory = factory;
    }
    @Override
    protected void execute() {
      setGraph(factory);
    }
  }

  
   class ActionToggleAntialias extends Action2 {
     ActionToggleAntialias() { super("Antialiasing"); }
    @Override
    public boolean isSelected() { return graphWidget.isAntialiasing(); }
    @Override
    protected void execute() {
      graphWidget.setAntialiasing(!graphWidget.isAntialiasing());
    }    
  }

  
   class ActionToggleAnimation extends Action2 {
     ActionToggleAnimation() { super("Animation"); }
    @Override
    public boolean isSelected() { return isAnimation; }
    @Override
    protected void execute() {
      isAnimation = !isAnimation;
    }    
  }

}
