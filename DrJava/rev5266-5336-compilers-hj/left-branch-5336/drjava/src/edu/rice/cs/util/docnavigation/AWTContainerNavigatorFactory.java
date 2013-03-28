

package edu.rice.cs.util.docnavigation;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.swing.Utilities;

import java.util.List;
import java.util.*;
import java.awt.event.FocusListener;

public class AWTContainerNavigatorFactory<ItemT extends INavigatorItem> implements IDocumentNavigatorFactory<ItemT> {
  
  public AWTContainerNavigatorFactory() { }
  
  
  public IDocumentNavigator<ItemT> makeListNavigator() { return new JListSortNavigator<ItemT>(); }
  
  
  public IDocumentNavigator<ItemT> makeTreeNavigator(String path) { return new JTreeSortNavigator<ItemT>(path); }
  
  
  public IDocumentNavigator<ItemT> makeListNavigator(final IDocumentNavigator<ItemT> parent) {
    final IDocumentNavigator<ItemT> child = makeListNavigator();
    Utilities.invokeLater(new Runnable() { 
      public void run() {

        migrateNavigatorItems(child, parent);
        migrateListeners(child, parent);
      }

    });
    return child;
  }
  
  
  public IDocumentNavigator<ItemT> makeTreeNavigator(String name, final IDocumentNavigator<ItemT> parent, 
                                                     final List<Pair<String, INavigatorItemFilter<ItemT>>> l) {
    
    final IDocumentNavigator<ItemT> child = makeTreeNavigator(name);
    Utilities.invokeLater(new Runnable() { 
      public void run() { 

        for(Pair<String, INavigatorItemFilter<ItemT>> p: l) { child.addTopLevelGroup(p.first(), p.second()); }
        migrateNavigatorItems(child, parent);
        migrateListeners(child, parent);
      }

    });     
    return child;
  }
  
  
  
  
  private void migrateNavigatorItems(IDocumentNavigator<ItemT> child, IDocumentNavigator<ItemT> parent) {
    ArrayList<ItemT> docs =  parent.getDocuments();
    for (ItemT item: docs) child.addDocument(item);

    parent.clear(); 
  }
  
  
  
  
  private void migrateListeners(IDocumentNavigator<ItemT> child, IDocumentNavigator<ItemT> parent) {
    for (INavigationListener<? super ItemT> nl: parent.getNavigatorListeners())  child.addNavigationListener(nl);
    for (FocusListener fl: parent.getFocusListeners())  child.addFocusListener(fl);
  }
}
