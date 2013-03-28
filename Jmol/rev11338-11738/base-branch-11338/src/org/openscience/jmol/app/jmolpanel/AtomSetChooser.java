

package org.openscience.jmol.app.jmolpanel;

import org.jmol.api.*;

import java.beans.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.vecmath.Point3f;

import java.awt.*;
import java.awt.event.*;

import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;

import org.jmol.i18n.GT;
import org.jmol.util.Logger;
import org.openscience.jmol.app.jmolpanel.JmolPanel;


public class AtomSetChooser extends JFrame
implements TreeSelectionListener, PropertyChangeListener,
ActionListener, ChangeListener, Runnable {
  
  private Thread animThread = null;
  
  private JTextArea propertiesTextArea;
  private JTree tree;
  private DefaultTreeModel treeModel;
  private JmolViewer viewer;
  private JCheckBox repeatCheckBox;
  private JSlider selectSlider;
  private JLabel infoLabel;
  private JSlider fpsSlider;
  private JSlider amplitudeSlider;
  private JSlider periodSlider;
  private JSlider scaleSlider;
  private JSlider radiusSlider;
  
  private JFileChooser saveChooser;

  
  
  
  static final String REWIND="rewind";
  static final String PREVIOUS="prev";
  static final String PLAY="play";
  static final String PAUSE="pause";
  static final String NEXT="next";
  static final String FF="ff";
  static final String SAVE="save";
  
  
  static final String COLLECTION = "collection";
  
  static final String VECTOR = "vector";
  

  
  private int indexes[];
  private int currentIndex=-1;
  
  
  private static final int FPS_MAX = 30;
  
  private static final float AMPLITUDE_PRECISION = 0.01f;
  
  private static final float AMPLITUDE_MAX = 1;
  
  private static final float AMPLITUDE_VALUE = 0.5f;

  
  private static final float PERIOD_PRECISION = 0.001f;
  
  private static final float PERIOD_MAX = 1; 
  
  private static final float PERIOD_VALUE = 0.5f;

  
  private static final int RADIUS_MAX = 19;
  
  private static final int RADIUS_VALUE = 3;

  
  private static final float SCALE_PRECISION = 0.01f;
  
  private static final float SCALE_MAX = 2.0f;
  
  private static final float SCALE_VALUE = 1.0f;

 
  
  public AtomSetChooser(JmolViewer viewer, JFrame frame) {
 
    super(GT._("AtomSetChooser"));
    this.viewer = viewer;
    
    
    treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(GT._("No AtomSets")));
    
    layoutWindow(getContentPane());
    pack();
    setLocationRelativeTo(frame);
    
  }
  
  private void layoutWindow(Container container) {
    
    container.setLayout(new BorderLayout());
    
    
    
    
    
    JPanel treePanel = new JPanel();
    treePanel.setLayout(new BorderLayout());
    tree = new JTree(treeModel);
    tree.setVisibleRowCount(5);
    
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(this);
    tree.setEnabled(false);
    treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
    
    JPanel propertiesPanel = new JPanel();
    propertiesPanel.setLayout(new BorderLayout());
    propertiesPanel.setBorder(new TitledBorder(GT._("Properties")));
    propertiesTextArea = new JTextArea();
    propertiesTextArea.setEditable(false);
    propertiesPanel.add(new JScrollPane(propertiesTextArea), BorderLayout.CENTER);
    
    
    JPanel astPanel = new JPanel();
    astPanel.setLayout(new BorderLayout());
    astPanel.setBorder(new TitledBorder(GT._("Atom Set Collection")));
    
    JSplitPane splitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT, treePanel, propertiesPanel); 
    astPanel.add(splitPane, BorderLayout.CENTER);
    splitPane.setResizeWeight(0.5);

    container.add(astPanel, BorderLayout.CENTER);
    
    
    
    
    JPanel controllerPanel = new JPanel();
    controllerPanel.setLayout(new BoxLayout(controllerPanel, BoxLayout.Y_AXIS));
    container.add(controllerPanel, BorderLayout.SOUTH);
    
    
    
    
    JPanel collectionPanel = new JPanel();
    collectionPanel.setLayout(new BoxLayout(collectionPanel, BoxLayout.Y_AXIS));
    collectionPanel.setBorder(new TitledBorder(GT._("Collection")));
    controllerPanel.add(collectionPanel);
    
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BorderLayout());
    infoPanel.setBorder(new TitledBorder(GT._("Info")));
    infoLabel = new JLabel(" ");
    infoPanel.add(infoLabel, BorderLayout.SOUTH);
    collectionPanel.add(infoPanel);
    
    JPanel cpsPanel = new JPanel();
    cpsPanel.setLayout(new BorderLayout());
    cpsPanel.setBorder(new TitledBorder(GT._("Select")));
    selectSlider = new JSlider(0, 0, 0);
    selectSlider.addChangeListener(this);
    selectSlider.setMajorTickSpacing(5);
    selectSlider.setMinorTickSpacing(1);
    selectSlider.setPaintTicks(true);
    selectSlider.setSnapToTicks(true);
    selectSlider.setEnabled(false);
    cpsPanel.add(selectSlider, BorderLayout.SOUTH);
    collectionPanel.add(cpsPanel);
    
    JPanel row = new JPanel();
    collectionPanel.add(row);
    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
    
    repeatCheckBox = new JCheckBox(GT._("Repeat"), false);
    JPanel vcrpanel = createVCRController(COLLECTION);
    vcrpanel.add(repeatCheckBox); 
    
    row.add(vcrpanel);
    
    JPanel fpsPanel = new JPanel();
    row.add(fpsPanel);
    int fps = viewer.getAnimationFps();
    if (fps > FPS_MAX)
      fps = FPS_MAX;
    fpsPanel.setLayout(new BorderLayout());
    fpsPanel.setBorder(new TitledBorder(GT._("FPS")));
    fpsSlider = new JSlider(0, FPS_MAX, fps);
    fpsSlider.setMajorTickSpacing(5);
    fpsSlider.setMinorTickSpacing(1);
    fpsSlider.setPaintTicks(true);
    fpsSlider.setSnapToTicks(true);
    fpsSlider.addChangeListener(this);
    fpsPanel.add(fpsSlider, BorderLayout.SOUTH);

    
    
    
    JPanel vectorPanel = new JPanel();
    controllerPanel.add(vectorPanel);
    
    vectorPanel.setLayout(new BoxLayout(vectorPanel, BoxLayout.Y_AXIS));
    vectorPanel.setBorder(new TitledBorder(GT._("Vector")));
    
    JPanel row1 = new JPanel();
    row1.setLayout(new BoxLayout(row1,BoxLayout.X_AXIS));
    
    JPanel radiusPanel = new JPanel();
    radiusPanel.setLayout(new BorderLayout());
    radiusPanel.setBorder(new TitledBorder(GT._("Radius")));
    radiusSlider = new JSlider(0, RADIUS_MAX, RADIUS_VALUE);
    radiusSlider.setMajorTickSpacing(5);
    radiusSlider.setMinorTickSpacing(1);
    radiusSlider.setPaintTicks(true);
    radiusSlider.setSnapToTicks(true);
    radiusSlider.addChangeListener(this);
    viewer.evalStringQuiet("vector "+ RADIUS_VALUE);
    radiusPanel.add(radiusSlider);
    row1.add(radiusPanel);
    
    JPanel scalePanel = new JPanel();
    scalePanel.setLayout(new BorderLayout());
    scalePanel.setBorder(new TitledBorder(GT._("Scale")));
    scaleSlider = new JSlider(0, (int)(SCALE_MAX/SCALE_PRECISION),
        (int) (SCALE_VALUE/SCALE_PRECISION));
    scaleSlider.addChangeListener(this);
    viewer.evalStringQuiet("vector scale " + SCALE_VALUE);
    scalePanel.add(scaleSlider);
    row1.add(scalePanel);
    vectorPanel.add(row1);
    
    JPanel row2 = new JPanel();
    row2.setLayout(new BoxLayout(row2,BoxLayout.X_AXIS));
    
    JPanel amplitudePanel = new JPanel();
    amplitudePanel.setLayout(new BorderLayout());
    amplitudePanel.setBorder(new TitledBorder(GT._("Amplitude")));
    amplitudeSlider = new JSlider(0, (int) (AMPLITUDE_MAX/AMPLITUDE_PRECISION),
        (int)(AMPLITUDE_VALUE/AMPLITUDE_PRECISION));
    viewer.evalStringQuiet("vibration scale " + AMPLITUDE_VALUE);
    amplitudeSlider.addChangeListener(this);
    amplitudePanel.add(amplitudeSlider);
    row2.add(amplitudePanel);
    
    JPanel periodPanel = new JPanel();
    periodPanel.setLayout(new BorderLayout());
    periodPanel.setBorder(new TitledBorder(GT._("Period")));
    periodSlider = new JSlider(0,
        (int)(PERIOD_MAX/PERIOD_PRECISION),
        (int)(PERIOD_VALUE/PERIOD_PRECISION));
    viewer.evalStringQuiet("vibration " + PERIOD_VALUE);
    periodSlider.addChangeListener(this);
    periodPanel.add(periodSlider);
    row2.add(periodPanel);
    vectorPanel.add(row2);
    
    vectorPanel.add(createVCRController(VECTOR));
  }
  
  
  private JPanel createVCRController(String section) {
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
    controlPanel.setBorder(new TitledBorder(GT._("Controller")));
    Insets inset = new Insets(1,1,1,1);


    String buttons[] = {REWIND,PREVIOUS,PLAY,PAUSE,NEXT,FF};
    String insert = null;
    if (section.equals(COLLECTION)) {
        insert = GT._("atom set");
    } else if (section.equals(VECTOR)) {
        insert = GT._("vector");
    }
    String tooltips[] = {
        GT._("Go to first {0} in the collection", insert),
        GT._("Go to previous {0} in the collection", insert),
        GT._("Play the whole collection of {0}'s", insert),
        GT._("Pause playing"),
        GT._("Go to next {0} in the collection", insert),
        GT._("Jump to last {0} in the collection", insert)
    };
    for (int i=buttons.length, idx=0; --i>=0; idx++) {
      String action = buttons[idx];
      
      JButton btn = new JButton(
          JmolResourceHandler.getIconX("AtomSetChooser."+action+"Image"));
      btn.setToolTipText(tooltips[idx]);
      btn.setMargin(inset);
      btn.setActionCommand(section+"."+action);
      btn.addActionListener(this);
      controlPanel.add(btn);
    }
    controlPanel.add(Box.createHorizontalGlue());
    return controlPanel;
  }
  
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
    tree.getLastSelectedPathComponent();
    if (node == null) {
      return;
    }
    try {
      int index = 0; 
      if (node.isLeaf()) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        setIndexes(parent); 
        index = parent.getIndex(node); 
      } else { 
        setIndexes(node);
      }
      showAtomSetIndex(index, true);
    }
    catch (Exception exception) {
 
    }
  }
  
  
  protected void showAtomSetIndex(int index, boolean bSetSelectSlider) {
    if (bSetSelectSlider) {
      selectSlider.setValue(index); 
      return;
    }
    try {
      currentIndex = index;
      int atomSetIndex = indexes[index];
      viewer.evalStringQuiet("frame " + viewer.getModelNumber(atomSetIndex));
      infoLabel.setText(viewer.getModelName(atomSetIndex));
      showProperties(viewer.getModelProperties(atomSetIndex));
      showAuxiliaryInfo(viewer.getModelAuxiliaryInfo(atomSetIndex));
    } catch (Exception e) {
      
    }
  }
  
  
  protected void setIndexes(DefaultMutableTreeNode node) {
    int atomSetCount = node.getLeafCount();
    indexes = new int[atomSetCount];
    Enumeration e = node.depthFirstEnumeration();
    int idx=0;
    while (e.hasMoreElements()) {
      node = (DefaultMutableTreeNode) e.nextElement();
      if (node.isLeaf())
        indexes[idx++]= ((AtomSet) node).getAtomSetIndex();
    }
    
    selectSlider.setEnabled(atomSetCount>0);
    selectSlider.setMaximum(atomSetCount-1);
  }
  
  public void actionPerformed (ActionEvent e) {
    String cmd = e.getActionCommand();
    String parts[]=cmd.split("\\.");
    try {
      if (parts.length==2) {
        String section = parts[0];
        cmd = parts[1];
        if (COLLECTION.equals(section)) {
         if (REWIND.equals(cmd)) {
            animThread = null;
            showAtomSetIndex(0, true);
          } else if (PREVIOUS.equals(cmd)) {
            showAtomSetIndex(currentIndex-1, true);
          } else if (PLAY.equals(cmd)) {
            if (animThread == null) {
              animThread = new Thread(this,"AtomSetChooserAnimationThread");
              animThread.start();
            }
          } else if (PAUSE.equals(cmd)) {
             animThread = null;
          } else if (NEXT.equals(cmd)) {
            showAtomSetIndex(currentIndex+1, true);
          } else if (FF.equals(cmd)) {
            animThread = null;
            showAtomSetIndex(indexes.length-1, true);
          } else if (SAVE.equals(cmd)) {
            saveXYZCollection();
          }
        } else if (VECTOR.equals(section)) {
          if (REWIND.equals(cmd)) {
            findFrequency(0,1);
          } else if (PREVIOUS.equals(cmd)) {
            findFrequency(currentIndex-1,-1);
          } else if (PLAY.equals(cmd)) {
            viewer.evalStringQuiet("vibration on");
          } else if (PAUSE.equals(cmd)) {
            viewer.evalStringQuiet("vibration off");
          } else if (NEXT.equals(cmd)) {
            findFrequency(currentIndex+1,1);
          } else if (FF.equals(cmd)) {
            findFrequency(indexes.length-1,-1);
          } else if (SAVE.equals(cmd)) {
            Logger.warn("Not implemented");
            
            
          }
        }
      }
    } catch (Exception exception) {
      
    }
  }
  
  
  public void saveXYZCollection() {
    int nidx = indexes.length;
    if (nidx==0) {
      Logger.warn("No collection selected.");
      return;
    }

    if (saveChooser == null)
      saveChooser = new JFileChooser();
    int retval = saveChooser.showSaveDialog(this);
    if (retval == 0) {
      File file = saveChooser.getSelectedFile();
      String fname = file.getAbsolutePath();
      try {
        PrintWriter f = new PrintWriter(new FileOutputStream(fname));
        for (int idx = 0; idx < nidx; idx++ ) {
          int modelIndex = indexes[idx];
          StringBuffer str = new StringBuffer(viewer.getModelName(modelIndex)).append("\n");
          int natoms=0;
          int atomCount = viewer.getAtomCount();
          for (int i = 0; i < atomCount;  i++) {
            if (viewer.getAtomModelIndex(i)==modelIndex) {
              natoms++;
              Point3f p = viewer.getAtomPoint3f(i);
              
              str.append(viewer.getAtomName(i)).append("\t");
              str.append(p.x).append("\t").append(p.y).append("\t").append(p.z).append("\n");
              
            }
          }
          f.println(natoms);
          f.print(str);
        }
        f.close();
      } catch (FileNotFoundException e) {
        
      }
    }
  }
  
  
  public void findFrequency(int index, int increment) {
    int maxIndex = indexes.length;
    boolean foundFrequency = false;
    
    
    while (index >= 0 && index < maxIndex 
        && !(foundFrequency=viewer.modelHasVibrationVectors(indexes[index]))) {
      index+=increment;
    }
    
    if (foundFrequency) {
      showAtomSetIndex(index, true);      
    }
  }
  
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    int value = ((JSlider) src).getValue();
    if (src == selectSlider) {
      showAtomSetIndex(value, false);
    } else if (src == fpsSlider) {
      if (value == 0)
        fpsSlider.setValue(1); 
      else
        viewer.evalStringQuiet("animation fps " + value);
    } else if (src == radiusSlider) {
      if (value == 0)
        radiusSlider.setValue(1); 
      else
        viewer.evalStringQuiet("vector " + value);
    } else if (src == scaleSlider) {
      viewer.evalStringQuiet("vector scale " + (value * SCALE_PRECISION));
    } else if (src == amplitudeSlider) {
      viewer
          .evalStringQuiet("vibration scale " + (value * AMPLITUDE_PRECISION));
    } else if (src == periodSlider) {
      viewer.evalStringQuiet("vibration " + (value * PERIOD_PRECISION));
    }
  }
  
  
  protected void showProperties(Properties properties) {
    boolean needLF = false;
    propertiesTextArea.setText("");
    if (properties != null) {
      Enumeration e = properties.propertyNames();
      while (e.hasMoreElements()) {
        String propertyName = (String)e.nextElement();
        if (propertyName.startsWith("."))
          continue; 
        propertiesTextArea.append((needLF?"\n ":" ") 
            + propertyName + "=" + properties.getProperty(propertyName));
        needLF = true;
      }
    }
  }
  
  
  protected void showAuxiliaryInfo(Hashtable auxiliaryInfo) {
    String separator = " ";
    
    if (auxiliaryInfo != null) {
      Enumeration e = auxiliaryInfo.keys();
      while (e.hasMoreElements()) {
        String keyName = (String) e.nextElement();
        if (keyName.startsWith("."))
          continue; 
        
        
        propertiesTextArea.append(separator + keyName + "="
            + auxiliaryInfo.get(keyName));
        separator = "\n ";
      }
    }
  }
  
  
  private void createTreeModel() {
    String key=null;
    String separator=null;
    String name = viewer.getModelSetName();
    DefaultMutableTreeNode root =
      new DefaultMutableTreeNode(name == null ? "zapped" : name);
    
    
    Properties modelSetProperties = (name == null ? null : viewer.getModelSetProperties());
    if (modelSetProperties != null) {
      key = modelSetProperties.getProperty("PATH_KEY");
      separator = modelSetProperties.getProperty("PATH_SEPARATOR");
    }
    if (key == null || separator == null) {
      
      if (name != null)
        for (int atomSetIndex = 0, count = viewer.getModelCount();
            atomSetIndex < count; ++atomSetIndex) {
          root.add(new AtomSet(atomSetIndex,
          viewer.getModelName(atomSetIndex)));
        }
    } else {
      for (int atomSetIndex = 0, count = viewer.getModelCount();
      atomSetIndex < count; ++atomSetIndex) {
        DefaultMutableTreeNode current = root;
        String path = viewer.getModelProperty(atomSetIndex,key);
        
        if (path != null) {
          DefaultMutableTreeNode child = null;
          String[] folders = path.split(separator);
          for (int i=0, nFolders=folders.length; --nFolders>=0; i++) {
            boolean found = false; 
            String lookForFolder = folders[i];
            for (int childIndex = current.getChildCount(); --childIndex>=0;) {
              child = (DefaultMutableTreeNode) current.getChildAt(childIndex);
              found = lookForFolder.equals(child.toString());
              if (found) break;
            }
            if (found) {
              current = child; 
            } else {
              
              DefaultMutableTreeNode newFolder = 
                new DefaultMutableTreeNode(lookForFolder);
              current.add(newFolder);
              current = newFolder; 
            }
          }
        }
        
        current.add(new AtomSet(atomSetIndex,
            viewer.getModelName(atomSetIndex)));
      }
    }
    treeModel.setRoot(root);
    treeModel.reload(); 

    
    tree.setEnabled(root.getChildCount()>0);
    
    indexes = null;
    currentIndex = -1;
    selectSlider.setEnabled(false);  
  }
  
  
  private static class AtomSet extends DefaultMutableTreeNode {
    
    private int atomSetIndex;
    
    private String atomSetName;
    
    public AtomSet(int atomSetIndex, String atomSetName) {
      this.atomSetIndex = atomSetIndex;
      this.atomSetName = atomSetName;
    }
    
    public int getAtomSetIndex() {
      return atomSetIndex;
    }
    
    public String toString() {
      return atomSetName;
    }
    
  }
  
  
  
  
  
  
  public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
    String eventName = propertyChangeEvent.getPropertyName();
    if (eventName.equals(JmolPanel.chemFileProperty)) {
      createTreeModel(); 
    }
  }

  
  public void run() {
    Thread myThread = Thread.currentThread();
    myThread.setPriority(Thread.MIN_PRIORITY);
    while (animThread == myThread) {
      
      
      if (currentIndex < 0) {
        animThread = null; 
      } else {
        ++currentIndex;
        if (currentIndex == indexes.length) {
          if (repeatCheckBox.isSelected())
            currentIndex = 0;  
          else {
            currentIndex--;    
            animThread = null; 
          }
        }
        showAtomSetIndex(currentIndex, true); 
        try {
          
          
          
          int fps = viewer.getAnimationFps();
          Thread.sleep((int) (1000.0/(fps==0?1:fps)));
        } catch (InterruptedException e) {
          Logger.error(null, e);
        }
      }
    }
  }
  
}
