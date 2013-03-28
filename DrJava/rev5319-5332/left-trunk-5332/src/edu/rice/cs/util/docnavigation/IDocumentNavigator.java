

package edu.rice.cs.util.docnavigation;

import java.io.File;
import java.util.*;
import java.awt.Container;
import java.awt.event.FocusListener;

 
public interface IDocumentNavigator<ItemT extends INavigatorItem> extends IAWTContainerNavigatorActor {
  
  public Container asContainer();
  
  
  public void addDocument(ItemT doc);
  
  
  public void addDocument(ItemT doc, String path);
  
  
  public ItemT getCurrent();
  
  
  public Object getModelLock();
  
  
  public ItemT removeDocument(ItemT doc);
  
  
  public void refreshDocument(ItemT doc, String path);
  
  
  public void selectDocument(ItemT doc);
  
  
  public ItemT getNext(ItemT doc);
  
  
  public ItemT getPrevious(ItemT doc);
  
  
  public ItemT getFirst();
  
  
  public ItemT getLast();
  
  
  public ArrayList<ItemT> getDocuments();
  
  
  public ArrayList<ItemT> getDocumentsInBin(String binName);
  
  
  public boolean contains(ItemT doc);
  
  
  public int getDocumentCount();
  
  
  public boolean isEmpty();
  
  
  public void clear();
  
  
  public void addNavigationListener(INavigationListener<? super ItemT> listener);
  
  
  public void removeNavigationListener(INavigationListener<? super ItemT> listener);
  
  
  public void addFocusListener(FocusListener e);
  
  
  public void removeFocusListener(FocusListener e);
  
  
  public FocusListener[] getFocusListeners();
  
  
  public Collection<INavigationListener<? super ItemT>> getNavigatorListeners();
  
  
  public boolean selectDocumentAt(int x, int y);
  
  
  public boolean isSelectedAt(int x, int y);
  
  
  public <InType, ReturnType> ReturnType execute(IDocumentNavigatorAlgo<ItemT, InType, ReturnType> algo, InType input);
  
  
  public int getSelectionCount();
  
  
  public boolean isGroupSelected();
  
  
  public int getGroupSelectedCount();
  
  
  public java.util.List<File> getSelectedFolders();
  
  
  public boolean isDocumentSelected();
  
  
  public int getDocumentSelectedCount();
  
  
  public java.util.List<ItemT> getSelectedDocuments();
  
  
  public boolean isRootSelected();
  
  
  public boolean isSelectedInGroup(ItemT i);
  
  
  public void addTopLevelGroup(String name, INavigatorItemFilter<? super ItemT> f);
  
  
  public boolean isTopLevelGroupSelected();
  
  
  public java.util.Set<String> getNamesOfSelectedTopLevelGroup() throws GroupNotSelectedException;
  
  
  public void requestSelectionUpdate(ItemT i);
  
  
  public void repaint();
  
  
  public void setNextChangeModelInitiated(boolean b);
  
  
  public boolean isNextChangeModelInitiated();
  
  
  public static final String MODEL_INITIATED_PROPERTY_NAME = "ModelInitiated";
  

}
