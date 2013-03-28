

package edu.rice.cs.util.docnavigation;



public interface IDocumentNavigatorAlgo<ItemT extends INavigatorItem, InType, ReturnType>
{
  
  public ReturnType forList(IDocumentNavigator<ItemT> navigator, InType input);

  
  public ReturnType forTree(IDocumentNavigator<ItemT> navigator, InType input);
}
