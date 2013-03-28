using System;
using WorldWind;
namespace jhuapl.util
{
 public class JHU_RootWidget : JHU_WidgetCollection, jhuapl.util.IWidget, jhuapl.util.IInteractive
 {
  jhuapl.util.IWidget m_parentWidget = null;
  jhuapl.util.IWidgetCollection m_ChildWidgets = new JHU_WidgetCollection();
  System.Windows.Forms.Control m_ParentControl;
  bool m_Initialized = false;
  public JHU_RootWidget(System.Windows.Forms.Control parentControl)
  {
   m_ParentControl = parentControl;
  }
  public void Initialize(DrawArgs drawArgs)
  {
  }
  public void Render(DrawArgs drawArgs)
  {
   if ((!m_visible) || (!m_enabled))
    return;
   for(int index = m_ChildWidgets.Count - 1; index >= 0; index--)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null)
    {
     if(currentWidget.ParentWidget == null || currentWidget.ParentWidget != this)
      currentWidget.ParentWidget = this;
     currentWidget.Render(drawArgs);
    }
   }
  }
  System.Drawing.Point m_location = new System.Drawing.Point(0,0);
  System.Drawing.Point m_ClientLocation = new System.Drawing.Point(0,120);
  public System.Drawing.Point AbsoluteLocation
  {
   get { return m_location; }
   set { m_location = value; }
  }
  public string Name
  {
   get { return "Main Frame"; }
   set { }
  }
  public jhuapl.util.IWidget ParentWidget
  {
   get { return m_parentWidget; }
   set { m_parentWidget = value; }
  }
  public jhuapl.util.IWidgetCollection ChildWidgets
  {
   get { return m_ChildWidgets; }
   set { m_ChildWidgets = value; }
  }
  bool m_enabled = true;
  bool m_visible = true;
  object m_tag = null;
  public System.Drawing.Point ClientLocation
  {
   get { return m_ClientLocation; }
   set { }
  }
  public System.Drawing.Size ClientSize
  {
   get
   {
    System.Drawing.Size mySize = m_ParentControl.Size;
    mySize.Height -= 120;
    return mySize;
   }
  }
  public System.Drawing.Size WidgetSize
  {
   get { return m_ParentControl.Size; }
   set { }
  }
  public bool Enabled
  {
   get { return m_enabled; }
   set { m_enabled = value; }
  }
  public bool Visible
  {
   get { return m_visible; }
   set { m_visible = value; }
  }
  protected bool m_countHeight = true;
  protected bool m_countWidth = true;
  public bool CountHeight
  {
   get { return m_countHeight; }
   set { m_countHeight = value; }
  }
  public bool CountWidth
  {
   get { return m_countWidth; }
   set { m_countWidth = value; }
  }
  public System.Drawing.Point Location
  {
   get { return m_location; }
   set { m_location = value; }
  }
  public object Tag
  {
   get { return m_tag; }
   set { m_tag = value; }
  }
  public bool IsInitialized
  {
   get { return m_Initialized;}
   set { m_Initialized = value; }
  }
  MouseClickAction m_leftClickAction;
  public MouseClickAction LeftClickAction
  {
   get { return m_leftClickAction; }
   set { m_leftClickAction = value; }
  }
  MouseClickAction m_rightClickAction;
  public MouseClickAction RightClickAction
  {
   get { return m_rightClickAction; }
   set { m_rightClickAction = value; }
  }
  public bool OnMouseDown(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnMouseDown(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  public bool OnMouseUp(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnMouseUp(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  public bool OnKeyDown(System.Windows.Forms.KeyEventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnKeyDown(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  public bool OnKeyUp(System.Windows.Forms.KeyEventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnKeyUp(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  public bool OnMouseEnter(EventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnMouseEnter(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  public bool OnMouseMove(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnMouseMove(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  public bool OnMouseLeave(EventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnMouseLeave(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  public bool OnMouseWheel(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   for(int index = 0; index < m_ChildWidgets.Count; index++)
   {
    jhuapl.util.IWidget currentWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
    if(currentWidget != null && currentWidget is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[index] as jhuapl.util.IInteractive;
     handled = currentInteractive.OnMouseWheel(e);
     if(handled)
      return handled;
    }
   }
   return handled;
  }
  new public void Add(jhuapl.util.IWidget widget)
  {
   m_ChildWidgets.Add(widget);
   widget.ParentWidget = this;
  }
  new public void Remove(jhuapl.util.IWidget widget)
  {
   m_ChildWidgets.Remove(widget);
  }
 }
}
