

package edu.rice.cs.util.docnavigation;
import edu.rice.cs.util.Pair;
import edu.rice.cs.util.swing.Utilities;

import java.util.List;
import java.util.*;
import java.awt.event.FocusListener;

public class AWTContainerNavigatorFactory<ItemT extends INavigatorItem> implements IDocumentNavigatorFactory<ItemT> {
  
  public AWTContainerNavigatorFactory() { }

  
    public IDocumentNavigator<ItemT> makeListNavigator() { return new JListSortNavigator<ItemT>(); }

  
    public IDocumentNavigator<ItemT> makeTreeNavigator(String path) { return new JTreeSortNavigator<ItemT>(path); }
    
  
    public IDocumentNavigator<ItemT> makeListNavigator(final IDocumentNavigator<ItemT> parent) {
      final IDocumentNavigator<ItemT> tbr = makeListNavigator();
      Utilities.invokeAndWait(new Runnable() { 
        public void run() { 
          migrateNavigatorItems(tbr, parent);
          migrateListeners(tbr, parent);
        }
      });
      return tbr;
    }
  
  
    public IDocumentNavigator<ItemT> makeTreeNavigator(String name, final IDocumentNavigator<ItemT> parent, 
                                                final List<Pair<String, INavigatorItemFilter<ItemT>>> l) {
      
      final IDocumentNavigator<ItemT> tbr = makeTreeNavigator(name);
      
      Utilities.invokeAndWait(new Runnable() { 
        public void run() { 
          
          for(Pair<String, INavigatorItemFilter<ItemT>> p: l) { tbr.addTopLevelGroup(p.getFirst(), p.getSecond()); }
          
          migrateNavigatorItems(tbr, parent);
          migrateListeners(tbr, parent);
        }
      });     
      return tbr;
    }
    
    
    
    
    private void migrateNavigatorItems(IDocumentNavigator<ItemT> child, IDocumentNavigator<ItemT> parent) {
      Enumeration<ItemT> enumerator =  parent.getDocuments();
      while (enumerator.hasMoreElements()) {
        ItemT navitem = enumerator.nextElement();
        child.addDocument(navitem);
      }
      parent.clear(); 
    }
    
    
    
    
    private void migrateListeners(IDocumentNavigator<ItemT> child, IDocumentNavigator<ItemT> parent) {
      for (INavigationListener<? super ItemT> nl: parent.getNavigatorListeners())  child.addNavigationListener(nl);
      for (FocusListener fl: parent.getFocusListeners())  child.addFocusListener(fl);
    }
}
