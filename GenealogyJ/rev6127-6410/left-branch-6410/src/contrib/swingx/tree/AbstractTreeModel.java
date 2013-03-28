
package swingx.tree;

import java.util.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


public abstract class AbstractTreeModel implements TreeModel {

    protected EventListenerList listenerList = new EventListenerList();

    
    protected abstract Object getParent(Object node);

    
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    
    public void nodeChanged(Object node) {
        if(listenerList != null && node != null) {
            Object parent = getParent(node);

            if(parent != null) {
                int anIndex = this.getIndexOfChild(parent, node);
                if (anIndex != -1) {
                    int[] cIndexs = new int[1];

                    cIndexs[0] = anIndex;
                    nodesChanged(parent, cIndexs);
                }
            }
            else if (node == getRoot()) {
                nodesChanged(node, null);
            }
        }
    }

    
    public void nodesWereInserted(Object node, int[] childIndices) {
        if(listenerList != null && node != null && childIndices != null
           && childIndices.length > 0) {
            int      cCount = childIndices.length;
            Object[] newChildren = new Object[cCount];

            for(int counter = 0; counter < cCount; counter++) {
                newChildren[counter] = getChild(node, childIndices[counter]);
            }
            fireTreeNodesInserted(this, getPathToRoot(node), childIndices,
                                  newChildren);
        }
    }

    
    public void nodesWereRemoved(Object node, int[] childIndices,
                                 Object[] removedChildren) {
        if(node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices,
                                 removedChildren);
        }
    }

    
    public void nodesChanged(Object node, int[] childIndices) {
        if(node != null) {
            if (childIndices != null) {
                int cCount = childIndices.length;

                if(cCount > 0) {
                    Object[] cChildren = new Object[cCount];

                    for(int counter = 0; counter < cCount; counter++) {
                        cChildren[counter] = getChild(node, childIndices[counter]);
                    }
                    fireTreeNodesChanged(this, getPathToRoot(node),
                                         childIndices, cChildren);
                }
            }
            else if (node == getRoot()) {
                fireTreeNodesChanged(this, getPathToRoot(node), null, null);
            }
        }
    }

    
    public void rootExchanged() {
        fireTreeStructureChanged(this, getPathToRoot(getRoot()), null, null);
    }

    
    public void nodeStructureChanged(Object node) {
        fireTreeStructureChanged(this, getPathToRoot(node), null, null);
    }

    
    public Object[] getPathToRoot(Object aNode) {
        return getPathToRoot(aNode, 0);
    }

    
    protected Object[] getPathToRoot(Object aNode, int depth) {
        Object[] retNodes;
    
    
    

        
        if(aNode == null) {
          
          
          
          
          
          
          
         
            return null;
        }
        else {
            depth++;
            if(aNode == getRoot())
                retNodes = new Object[depth];
            else
                retNodes = getPathToRoot(getParent(aNode), depth);
            
            if (retNodes==null)
              return null;
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }


    
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    
    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        
        
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }
        }
    }

    
    protected void fireTreeNodesInserted(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        
        
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }
        }
    }

    
    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        
        
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                
                if (e == null)
                    e = new TreeModelEvent(source, path,
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }
        }
    }

    
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices,
                                            Object[] children) {
        
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        
        
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                
                if (e == null)
                  
                  
                  
                  
                    e = new TreeModelEvent(source, path==null ? null : new TreePath(path),
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }

    
    public EventListener[] getListeners(Class listenerType) { 
    return listenerList.getListeners(listenerType); 
    }

    
    public TreeModelListener[] getTreeModelListeners() {
        return (TreeModelListener[])listenerList.getListeners(
                TreeModelListener.class);
    }
}