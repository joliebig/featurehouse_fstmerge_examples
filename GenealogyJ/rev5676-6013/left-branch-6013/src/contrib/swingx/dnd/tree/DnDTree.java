
package swingx.dnd.tree;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import swingx.dnd.ObjectTransferable;


public class DnDTree extends JTree implements Autoscroll {

    
    private static final int DEFAULT_AUTOSCROLL_MARGIN = 12;
    
    
    private int autoscrollMargin = DEFAULT_AUTOSCROLL_MARGIN;

    private DragGestureRecognizer dragGestureRecognizer; 
    private DragHandler dragHandler;
    private DropHandler dropHandler;
    
    
    public DnDTree() {
        this(getDefaultTreeModel());        
    }
    
    
    public DnDTree(TreeModel model) {
        super(model);
        
        
        
        setDragEnabled(true);
        setTransferHandler(new TransferHandler() {
            public int getSourceActions(JComponent c) {
                return DnDConstants.ACTION_COPY;
            }
            
            public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
                if (hasDnDModel()) {
                    TreePath[] paths = getSelectionPaths();
                    if (paths != null && paths.length > 0) {
                        Arrays.sort(paths, dragHandler);
                        
                        Object[] nodes = new Object[paths.length];
                        for (int p = 0; p < paths.length; p++) {
                            if (paths[p].getPathCount() > 1) {
                                nodes[p] = paths[p].getLastPathComponent();
                            }
                        }
                        
                        Transferable transferable = getDnDModel().createTransferable(nodes);
                        
                        try {
                            getDnDModel().drag(transferable, action);
                            clip.setContents(transferable, null);
                        } catch (Exception ex) {
                            
                        }
                    }
                }
            }
            
            public boolean importData(JComponent comp, Transferable t) {
                TreePath[] paths = getSelectionPaths();
                if (paths != null && paths.length == 1 && paths[0].getPathCount() > 0) {
                    if (hasDnDModel()) {
                        try {
                            getDnDModel().drop(t, paths[0].getLastPathComponent(), 0, DnDTreeModel.MOVE);
                            return true;
                        } catch (Exception ex) {
                            
                        }
                    }
                }
                return false;
            }
        });
    
        
        
        dragGestureRecognizer = new DragSource().createDefaultDragGestureRecognizer(this,
                                                            DnDConstants.ACTION_MOVE,
                                                            getDragSourceListener());
        
        new DropTarget(this, getDropTargetListener());
    }

    
    protected static TreeModel getDefaultTreeModel() {
        return new DefaultDnDTreeModel((MutableTreeNode)JTree.getDefaultTreeModel().getRoot());     
    }
    
    public boolean hasDnDModel() {
        return getModel() instanceof DnDTreeModel;
    }
    
    public DnDTreeModel getDnDModel() {
        if (getModel() instanceof DnDTreeModel) {
            return (DnDTreeModel)getModel();
        }
        throw new IllegalStateException("no DnDTreeModel");
    }

    
    public int getAutoscrollMargin() {
        return autoscrollMargin;
    }

    
    public void setAutoscrollMargin(int margin) {
        autoscrollMargin = margin;
    }

    public void autoscroll(Point point) {
        Dimension dimension = getParent().getSize();

        int row = getClosestRowForLocation(point.x, point.y);
        if (row != -1) {
            if (getY() + point.y < dimension.height/2) {
                row = Math.max(0, row - 1);
            } else {
                row = Math.min(row + 1, getRowCount() - 1);
            }
            scrollRowToVisible(row);
        }        
    }

    public Insets getAutoscrollInsets() {
        Rectangle bounds = getParent().getBounds();

        return new Insets(
            bounds.y - getY() + autoscrollMargin,
            bounds.x - getX() + autoscrollMargin,
            getHeight() - bounds.height - bounds.y + getY() + autoscrollMargin,
            getWidth()  - bounds.width  - bounds.x + getX() + autoscrollMargin);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        getDropTargetListener().paint(g);
    }

    protected DragHandler getDragSourceListener() {
        if (dragHandler == null) {
            dragHandler = new DragHandler();
        }
        return dragHandler;
    }

    protected DropHandler getDropTargetListener() {
        if (dropHandler == null) {
            dropHandler = new DropHandler();
        }
        return dropHandler;
    }

        
    private class DragHandler extends DragSourceAdapter implements DragGestureListener,
                                                                   Comparator {
        
        private Transferable transferable;
        
        
        public void dragGestureRecognized(DragGestureEvent dge) {
            
            if (hasDnDModel()) {
                TreePath[] paths = getSelectionPaths();
                if (paths != null && paths.length > 0) {
                    Arrays.sort(paths, this);
                    
                    boolean selectionHit = false;
                    Object[] nodes = new Object[paths.length];
                    for (int p = 0; p < paths.length; p++) {
                      
                      
                      
                      
                      
                        if (paths[p].getPathCount() > 0) {
                            nodes[p] = paths[p].getLastPathComponent();
        
                            Rectangle rect = getPathBounds(paths[p]);
                            if (rect.contains(dge.getDragOrigin())) {
                                selectionHit = true;
                            }
                        }
                    }
                    
                    if (selectionHit) {
                        transferable = getDnDModel().createTransferable(nodes);

                        dragGestureRecognizer.setSourceActions(getDnDModel().getDragActions(transferable));

                        dge.startDrag(null, 
                                      createDragImage(paths),
                                      new Point(),
                                      transferable, this);
                    }
                }
            }
        }

        
        public int compare(Object object1, Object object2) {
            TreePath path1 = (TreePath)object1;
            TreePath path2 = (TreePath)object2;
            
            int row1 = getRowForPath(path1); 
            int row2 = getRowForPath(path2);
            if (row1 < row2) {
                return -1;
            } else if (row2 < row1) {
                return 1;
            } else {
                return 0;
            }
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            
            if (dsde.getDropSuccess()) {
                try {
                    getDnDModel().drag(transferable, dsde.getDropAction());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    
                }
            }
            
            getDnDModel().releaseTransferable(transferable);
            transferable = null;
            
            dragGestureRecognizer.setSourceActions(DnDConstants.ACTION_MOVE);
        }
    }

        
    private class DropHandler extends DropTargetAdapter implements ActionListener, TreeModelListener {
            
        private Timer timer;
        
        private TreePath   parentPath;
        private int        childIndex = 0;
        private Rectangle  indicator;
        private List       insertions = new ArrayList();

        public DropHandler() {
            timer = new Timer(1500, this);
            timer.setRepeats(false);            
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            dragOver(dtde);
        }

        public void dragOver(DropTargetDragEvent dtde) {
            int     action   = dtde.getDropAction();
            boolean accepted = false;
            
            if (hasDnDModel()) {
                update(dtde.getLocation());

                if (parentPath != null) {
                    Transferable transferable = ObjectTransferable.getTigerTransferable(dtde);
                    if (transferable == null) {
                        accepted = true;
                    } else {
                        Object parent = parentPath.getLastPathComponent();
                        accepted = (getDnDModel().getDropActions(transferable, parent, childIndex) & action) != 0;
                    }                        
                }
            }

            if (accepted) {
                dtde.acceptDrag(action);            
            } else {
                dtde.rejectDrag();
            }
        }

        public void dragExit(DropTargetEvent dte) {
            clear();
        }

        public void drop(DropTargetDropEvent dtde) {
            int     action   = dtde.getDropAction();
            boolean complete = false;

            getModel().addTreeModelListener(this);
            try {
                Object parent = null;
                if (hasDnDModel() && parentPath != null) {
                    dtde.acceptDrop(action);
                    
                    parent = parentPath.getLastPathComponent();
                    Transferable transferable = dtde.getTransferable();

                    if ((getDnDModel().getDropActions(transferable, parent, childIndex) & action) != 0) {

                        getDnDModel().drop(transferable, parent, childIndex, action);
                        
                        complete = true;
                    }    
                }
    
                dtde.dropComplete(complete);
                
                if (!insertions.isEmpty()&&parent!=null) {
                    getSelectionModel().clearSelection();
                    for (int i = 0; i < insertions.size(); i++) {
                      TreePath path = (TreePath)insertions.get(i);
                      if (getModel().getIndexOfChild(parent, path.getLastPathComponent())>=0)
                        getSelectionModel().addSelectionPath(path);
                    }
                }
            } catch (Exception ex) {
                
            }
            getModel().removeTreeModelListener(this);

            clear();
        }

        private void update(Point point) {
            TreePath oldParentPath = parentPath;
            
            TreePath path = getClosestPathForLocation(point.x, point.y);
            if (path == null) {
                parentPath = null;
                childIndex = -1;
                indicator  = null;
            } else if (path.getPathCount() == 1) {
                parentPath = path;
                childIndex = 0;
                indicator  = null;
            } else {
                parentPath = path.getParentPath();
                childIndex = getModel().getIndexOfChild(parentPath.getLastPathComponent(), path.getLastPathComponent());
                indicator  = getPathBounds(path);
                                
                if (getModel().isLeaf(path.getLastPathComponent())  ||
                    (point.y < indicator.y + indicator.height*1/4)  ||
                    (point.y > indicator.y + indicator.height*3/4 && !isExpanded(path)) ) {

                    if (point.y > indicator.y + indicator.height/2) {
                        indicator.y = indicator.y + indicator.height;
                        childIndex++; 
                    }
                    indicator.width = getWidth() - indicator.x - getInsets().right;
                    indicator.y      -= 1;
                    indicator.height  = 2;
                } else {
                    parentPath = path; 
                    indicator  = null;
                    childIndex = 0;
                }
            }

            repaint();

            if (parentPath == null) {
                if (timer.isRunning()) {
                    timer.stop();
                }
            } else {
                if (!parentPath.equals(oldParentPath)) {
                    timer.start();
                }
            }            
        }
                
        private void clear() {
            if (timer.isRunning()) {
                timer.stop();
            }
        
            parentPath   = null;
            childIndex   = -1;
            indicator    = null;
            insertions.clear();

            repaint();
        }
        
        public TreePath getParentPath() {
            return parentPath;
        }
        
        public void paint(Graphics g) {
            if (indicator != null) {
                paintIndicator(g, indicator);
            }
        }

        private void paintIndicator(Graphics g, Rectangle rect) {
            g.setColor(getForeground());
            
            g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);

            g.drawLine(rect.x, rect.y - 2              , rect.x + 1, rect.y - 2);
            g.drawLine(rect.x, rect.y - 1              , rect.x + 2, rect.y - 1);
            g.drawLine(rect.x, rect.y + rect.height + 0, rect.x + 2, rect.y + rect.height + 0);
            g.drawLine(rect.x, rect.y + rect.height + 1, rect.x + 1, rect.y + rect.height + 1);
            
            g.drawLine(rect.x + rect.width - 2, rect.y - 2              , rect.x + rect.width - 1, rect.y - 2);
            g.drawLine(rect.x + rect.width - 3, rect.y - 1              , rect.x + rect.width - 1, rect.y - 1);
            g.drawLine(rect.x + rect.width - 3, rect.y + rect.height + 0, rect.x + rect.width - 1, rect.y + rect.height + 0);
            g.drawLine(rect.x + rect.width - 2, rect.y + rect.height + 1, rect.x + rect.width - 1, rect.y + rect.height + 1);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (parentPath != null) {
                expandPath(parentPath);
            }
        }
        
        public void treeNodesChanged(TreeModelEvent e) { }
        
        public void treeNodesInserted(TreeModelEvent e) {
            Object[] children = e.getChildren();
            for (int c = 0; c < children.length; c++) {
                insertions.add(e.getTreePath().pathByAddingChild(children[c]));
            }
        }
        
        public void treeNodesRemoved(TreeModelEvent e) {
            insertions.clear();
        }
        
        public void treeStructureChanged(TreeModelEvent e) {
            insertions.clear();
            insertions.add(e.getTreePath());
        }
    }

    
    protected Image createDragImage(TreePath[] selectionPaths) {
        return createImage(1, 1);
    }
}