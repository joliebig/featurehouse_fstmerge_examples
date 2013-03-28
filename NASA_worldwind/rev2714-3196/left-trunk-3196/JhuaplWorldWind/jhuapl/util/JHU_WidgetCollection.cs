using System;
namespace jhuapl.util
{
 public class JHU_WidgetCollection : jhuapl.util.IWidgetCollection
 {
  System.Collections.ArrayList m_ChildWidgets = new System.Collections.ArrayList();
  public JHU_WidgetCollection()
  {
  }
  public void BringToFront(int index)
  {
   jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
   if(currentWidget != null)
   {
    m_ChildWidgets.RemoveAt(index);
    m_ChildWidgets.Insert(0, currentWidget);
   }
  }
  public void BringToFront(jhuapl.util.IWidget widget)
  {
   int foundIndex = -1;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
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
  public void Add(jhuapl.util.IWidget widget)
  {
   m_ChildWidgets.Add(widget);
  }
  public void Clear()
  {
   m_ChildWidgets.Clear();
  }
  public void Insert(jhuapl.util.IWidget widget, int index)
  {
   if(index <= m_ChildWidgets.Count)
   {
    m_ChildWidgets.Insert(index, widget);
   }
  }
  public jhuapl.util.IWidget RemoveAt(int index)
  {
   if(index < m_ChildWidgets.Count)
   {
    jhuapl.util.IWidget oldWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    m_ChildWidgets.RemoveAt(index);
    return oldWidget;
   }
   else
   {
    return null;
   }
  }
  public void Remove(jhuapl.util.IWidget widget)
  {
   int foundIndex = -1;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null)
    {
     if(currentWidget == widget)
     {
      foundIndex = index;
      break;
     }
    }
   }
   if(foundIndex >= 0)
   {
    m_ChildWidgets.RemoveAt(foundIndex);
   }
  }
  public int Count
  {
   get
   {
    return m_ChildWidgets.Count;
   }
  }
  public jhuapl.util.IWidget this[int index]
  {
   get
   {
    return m_ChildWidgets[index] as jhuapl.util.IWidget;
   }
   set
   {
    m_ChildWidgets[index] = value;
   }
  }
 }
}
