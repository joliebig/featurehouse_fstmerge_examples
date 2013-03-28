using System;
namespace WorldWind.Widgets
{
 public class WidgetCollection : IWidgetCollection
 {
  System.Collections.ArrayList m_ChildWidgets = new System.Collections.ArrayList();
  public WidgetCollection()
  {
  }
  public void BringToFront(int index)
  {
   IWidget currentWidget = m_ChildWidgets[index] as IWidget;
   if(currentWidget != null)
   {
    m_ChildWidgets.RemoveAt(index);
    m_ChildWidgets.Insert(0, currentWidget);
   }
  }
  public void BringToFront(IWidget widget)
  {
   int foundIndex = -1;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    IWidget currentWidget = m_ChildWidgets[index] as IWidget;
    if(currentWidget != null)
    {
     if(currentWidget == widget)
     {
      foundIndex = index;
      break;
     }
    }
   }
   if(foundIndex > 0)
   {
    BringToFront(foundIndex);
   }
  }
  public void Add(IWidget widget)
  {
   m_ChildWidgets.Add(widget);
  }
  public void Clear()
  {
   m_ChildWidgets.Clear();
  }
  public void Insert(IWidget widget, int index)
  {
   if(index <= m_ChildWidgets.Count)
   {
    m_ChildWidgets.Insert(index, widget);
   }
  }
  public IWidget RemoveAt(int index)
  {
   if(index < m_ChildWidgets.Count)
   {
    IWidget oldWidget = m_ChildWidgets[index] as IWidget;
    m_ChildWidgets.RemoveAt(index);
    return oldWidget;
   }
   else
   {
    return null;
   }
  }
  public int Count
  {
   get
   {
    return m_ChildWidgets.Count;
   }
  }
  public IWidget this[int index]
  {
   get
   {
    return m_ChildWidgets[index] as IWidget;
   }
   set
   {
    m_ChildWidgets[index] = value;
   }
  }
 }
}
