using System;
using System.Drawing;
using System.Windows.Forms;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
namespace jhuapl.util
{
 public class JHU_PanelWidget : JHU_WidgetCollection, jhuapl.util.IWidget, jhuapl.util.IInteractive
 {
  protected string m_name = "";
  protected System.Drawing.Point m_location = new System.Drawing.Point(0,0);
  protected System.Drawing.Point m_clientLocation = new System.Drawing.Point(0,23);
  protected System.Drawing.Size m_size = new System.Drawing.Size(200, 300);
  protected System.Drawing.Size m_clientSize = new System.Drawing.Size(300,177);
  protected bool m_visible = true;
  protected bool m_enabled = true;
  protected bool m_countHeight = false;
  protected bool m_countWidth = false;
  protected jhuapl.util.IWidget m_parentWidget = null;
  protected IWidgetCollection m_ChildWidgets = new JHU_WidgetCollection();
  protected object m_tag = null;
  protected bool m_isInitialized = false;
  protected MouseClickAction m_leftClickAction = null;
  protected MouseClickAction m_rightClickAction = null;
  protected System.Drawing.Color m_BackgroundColor = System.Drawing.Color.FromArgb(
   170,
   40,
   40,
   40);
  protected System.Drawing.Color m_BorderColor = System.Drawing.Color.GhostWhite;
  protected System.Drawing.Color m_HeaderColor = System.Drawing.Color.FromArgb(
   170,
   System.Drawing.Color.DarkKhaki.R,
   System.Drawing.Color.DarkKhaki.G,
   System.Drawing.Color.DarkKhaki.B);
  protected System.Drawing.Color m_TextColor = System.Drawing.Color.GhostWhite;
  protected int m_headerHeight = 23;
  protected int m_currHeaderHeight = 0;
  protected int m_leftPadding = 2;
  protected int m_rightPadding = 1;
  protected int m_topPadding = 2;
  protected int m_bottomPadding = 1;
  protected bool m_renderBody = true;
  protected Microsoft.DirectX.Direct3D.Font m_TextFont;
  protected Microsoft.DirectX.Direct3D.Font m_TitleFont;
  protected Microsoft.DirectX.Direct3D.Font m_wingdingsFont;
  protected Microsoft.DirectX.Direct3D.Font m_worldwinddingsFont;
  protected Vector2[] m_OutlineVertsHeader = new Vector2[5];
  protected Vector2[] m_OutlineVerts = new Vector2[5];
  protected System.Drawing.Point m_LastMousePosition = new System.Drawing.Point(0,0);
  protected DateTime m_LastClickTime;
  public string Text = "";
  public bool HeaderEnabled = true;
  public Microsoft.DirectX.Direct3D.Font TextFont
  {
   get { return m_TextFont; }
   set { m_TextFont = value; }
  }
  public System.Drawing.Color HeaderColor
  {
   get { return m_HeaderColor; }
   set { m_HeaderColor = value; }
  }
  public int HeaderHeight
  {
   get { return m_headerHeight; }
   set { m_headerHeight = value; }
  }
  public System.Drawing.Color BorderColor
  {
   get { return m_BorderColor; }
   set { m_BorderColor = value; }
  }
  public System.Drawing.Color BackgroundColor
  {
   get { return m_BackgroundColor; }
   set { m_BackgroundColor = value; }
  }
  public int Top
  {
   get
   {
    if (HeaderEnabled)
     return this.AbsoluteLocation.Y;
    else
     return this.AbsoluteLocation.Y + this.m_currHeaderHeight;
   }
  }
  public int Bottom
  {
   get
   {
    if (m_renderBody)
     return this.AbsoluteLocation.Y + this.m_size.Height;
    else
     return this.AbsoluteLocation.Y + this.m_currHeaderHeight;
   }
  }
  public int Left
  {
   get
   {
    return this.AbsoluteLocation.X;
   }
  }
  public int Right
  {
   get
   {
    return this.AbsoluteLocation.X + this.m_size.Width;
   }
  }
  public System.Drawing.Point BodyLocation
  {
   get
   {
    System.Drawing.Point bodyLocation;
    bodyLocation = this.AbsoluteLocation;
    if (this.HeaderEnabled)
     bodyLocation.Y += m_currHeaderHeight;
    return bodyLocation;
   }
  }
  public JHU_PanelWidget(string name)
  {
   m_name = name;
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
  public void Dispose()
  {
   if(m_ChildWidgets != null)
   {
    for(int i = 0; i < m_ChildWidgets.Count; i++)
    {
    }
    m_ChildWidgets.Clear();
   }
   m_isInitialized = false;
  }
  protected void getChildrenSize(out int childrenHeight, out int childrenWidth)
  {
   childrenHeight = 0;
   childrenWidth = 0;
   int biggestHeight = 0;
   int biggestWidth = 0;
   for(int i = 0; i < m_ChildWidgets.Count; i++)
   {
    if (m_ChildWidgets[i].CountHeight)
     childrenHeight += m_ChildWidgets[i].WidgetSize.Height;
    if (m_ChildWidgets[i].CountWidth)
     childrenWidth += m_ChildWidgets[i].WidgetSize.Width;
    if (m_ChildWidgets[i].WidgetSize.Height > biggestHeight)
     biggestHeight = m_ChildWidgets[i].WidgetSize.Height;
    if (m_ChildWidgets[i].WidgetSize.Width > biggestWidth)
     biggestWidth = m_ChildWidgets[i].WidgetSize.Width;
   }
   if (biggestHeight > childrenHeight)
    childrenHeight = biggestHeight;
   if (biggestWidth > childrenWidth)
    childrenWidth = biggestWidth;
  }
  public string Name
  {
   get { return m_name; }
   set { m_name = value; }
  }
  public System.Drawing.Point Location
  {
   get { return m_location; }
   set { m_location = value; }
  }
  public System.Drawing.Point AbsoluteLocation
  {
   get
   {
    if(m_parentWidget != null)
    {
     return new System.Drawing.Point(
      m_location.X + m_parentWidget.ClientLocation.X,
      m_location.Y + m_parentWidget.ClientLocation.Y);
    }
    else
    {
     return m_location;
    }
   }
  }
  public System.Drawing.Point ClientLocation
  {
   get
   {
    m_clientLocation = this.BodyLocation;
    return m_clientLocation;
   }
  }
  public System.Drawing.Size WidgetSize
  {
   get { return m_size; }
   set { m_size = value; }
  }
  public System.Drawing.Size ClientSize
  {
   get
   {
    m_clientSize = m_size;
    m_clientSize.Height -= m_currHeaderHeight;
    return m_clientSize;
   }
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
  public jhuapl.util.IWidget ParentWidget
  {
   get { return m_parentWidget; }
   set { m_parentWidget = value; }
  }
  public IWidgetCollection ChildWidgets
  {
   get { return m_ChildWidgets; }
   set { m_ChildWidgets = value; }
  }
  public object Tag
  {
   get { return m_tag; }
   set { m_tag = value; }
  }
  public void Initialize(DrawArgs drawArgs)
  {
   if(!m_enabled)
    return;
   if (m_TitleFont == null)
   {
    System.Drawing.Font localHeaderFont = new System.Drawing.Font("Arial", 12.0f, FontStyle.Italic | FontStyle.Bold);
    m_TitleFont = new Microsoft.DirectX.Direct3D.Font(drawArgs.device, localHeaderFont);
    System.Drawing.Font wingdings = new System.Drawing.Font("Wingdings", 12.0f);
    m_wingdingsFont = new Microsoft.DirectX.Direct3D.Font(drawArgs.device, wingdings);
    System.Drawing.Font worldwinddings = new System.Drawing.Font("World Wind dings", 12.0f);
    m_worldwinddingsFont = new Microsoft.DirectX.Direct3D.Font(drawArgs.device, worldwinddings);
   }
   m_TextFont = drawArgs.defaultDrawingFont;
   m_isInitialized = true;
  }
  public void Render(DrawArgs drawArgs)
  {
   if ((!m_visible) || (!m_enabled))
    return;
   if (!m_isInitialized)
   {
    Initialize(drawArgs);
   }
   int widgetTop = this.Top;
   int widgetBottom = this.Bottom;
   int widgetLeft = this.Left;
   int widgetRight = this.Right;
   m_currHeaderHeight = 0;
   if(HeaderEnabled)
   {
    m_currHeaderHeight = m_headerHeight;
    JHU_Utilities.DrawBox(
     this.AbsoluteLocation.X,
     this.AbsoluteLocation.Y,
     m_size.Width,
     m_currHeaderHeight,
     0.0f,
     m_HeaderColor.ToArgb(),
     drawArgs.device);
    Rectangle nameBounds = m_TitleFont.MeasureString(
     null,
     m_name,
     DrawTextFormat.None,
     0);
    int widthLeft = m_size.Width;
    m_TitleFont.DrawText(
     null,
     m_name,
     new System.Drawing.Rectangle(this.AbsoluteLocation.X + 2, this.AbsoluteLocation.Y + 2, widthLeft, m_currHeaderHeight),
     DrawTextFormat.None,
     m_TextColor.ToArgb());
    if (!m_renderBody)
    {
     widthLeft -= nameBounds.Width + 10;
     if (widthLeft > 20)
     {
      m_TextFont.DrawText(
       null,
       Text,
       new System.Drawing.Rectangle(this.AbsoluteLocation.X + 10 + nameBounds.Width, this.AbsoluteLocation.Y + 3, widthLeft, m_currHeaderHeight),
       DrawTextFormat.None,
       m_TextColor.ToArgb());
     }
    }
    m_OutlineVertsHeader[0].X = AbsoluteLocation.X;
    m_OutlineVertsHeader[0].Y = AbsoluteLocation.Y;
    m_OutlineVertsHeader[1].X = AbsoluteLocation.X + m_size.Width;
    m_OutlineVertsHeader[1].Y = AbsoluteLocation.Y;
    m_OutlineVertsHeader[2].X = AbsoluteLocation.X + m_size.Width;
    m_OutlineVertsHeader[2].Y = AbsoluteLocation.Y + m_currHeaderHeight;
    m_OutlineVertsHeader[3].X = AbsoluteLocation.X;
    m_OutlineVertsHeader[3].Y = AbsoluteLocation.Y + m_currHeaderHeight;
    m_OutlineVertsHeader[4].X = AbsoluteLocation.X;
    m_OutlineVertsHeader[4].Y = AbsoluteLocation.Y;
    JHU_Utilities.DrawLine(m_OutlineVertsHeader, m_BorderColor.ToArgb(), drawArgs.device);
   }
   if (m_renderBody)
   {
    JHU_Utilities.DrawBox(
     this.AbsoluteLocation.X,
     this.AbsoluteLocation.Y + m_currHeaderHeight,
     m_size.Width,
     m_size.Height - m_currHeaderHeight,
     0.0f,
     m_BackgroundColor.ToArgb(),
     drawArgs.device);
    int childrenHeight = 0;
    int childrenWidth = 0;
    int bodyHeight = m_size.Height - m_currHeaderHeight;
    int bodyWidth = m_size.Width;
    getChildrenSize(out childrenHeight, out childrenWidth);
    int bodyLeft = this.BodyLocation.X;
    int bodyRight = this.BodyLocation.X + this.ClientSize.Width;
    int bodyTop = this.BodyLocation.Y;
    int bodyBottom = this.BodyLocation.Y + this.ClientSize.Height;
    int childLeft = 0;
    int childRight = 0;
    int childTop = 0;
    int childBottom = 0;
    for(int index = m_ChildWidgets.Count - 1; index >= 0; index--)
    {
     jhuapl.util.IWidget currentChildWidget = m_ChildWidgets[index] as jhuapl.util.IWidget;
     if(currentChildWidget != null)
     {
      if(currentChildWidget.ParentWidget == null || currentChildWidget.ParentWidget != this)
      {
       currentChildWidget.ParentWidget = this;
      }
      System.Drawing.Point childLocation = currentChildWidget.AbsoluteLocation;
      childLeft = childLocation.X;
      childRight = childLocation.X + currentChildWidget.WidgetSize.Width;
      childTop = childLocation.Y;
      childBottom = childLocation.Y + currentChildWidget.WidgetSize.Height;
      if ( ( ( (childLeft >= bodyLeft) && (childLeft <= bodyRight) ) ||
       ( (childRight >= bodyLeft) && (childRight <= bodyRight) ) ||
       ( (childLeft <= bodyLeft) && (childRight >= bodyRight) ) )
       &&
       ( ( (childTop >= bodyTop) && (childTop <= bodyBottom) ) ||
       ( (childBottom >= bodyTop) && (childBottom <= bodyBottom) ) ||
       ( (childTop <= bodyTop) && (childBottom >= bodyBottom) ) )
       )
      {
       currentChildWidget.Visible = true;
       currentChildWidget.Render(drawArgs);
      }
      else
       currentChildWidget.Visible = false;
     }
    }
    m_OutlineVerts[0].X = AbsoluteLocation.X;
    m_OutlineVerts[0].Y = AbsoluteLocation.Y + m_currHeaderHeight;
    m_OutlineVerts[1].X = AbsoluteLocation.X + m_size.Width;
    m_OutlineVerts[1].Y = AbsoluteLocation.Y + m_currHeaderHeight;
    m_OutlineVerts[2].X = AbsoluteLocation.X + m_size.Width;
    m_OutlineVerts[2].Y = AbsoluteLocation.Y + m_size.Height;
    m_OutlineVerts[3].X = AbsoluteLocation.X;
    m_OutlineVerts[3].Y = AbsoluteLocation.Y + m_size.Height;
    m_OutlineVerts[4].X = AbsoluteLocation.X;
    m_OutlineVerts[4].Y = AbsoluteLocation.Y + m_currHeaderHeight;
    JHU_Utilities.DrawLine(m_OutlineVerts, m_BorderColor.ToArgb(), drawArgs.device);
   }
  }
  public MouseClickAction LeftClickAction
  {
   get { return m_leftClickAction; }
   set { m_leftClickAction = value; }
  }
  public MouseClickAction RightClickAction
  {
   get { return m_rightClickAction; }
   set { m_rightClickAction = value; }
  }
  public bool OnMouseDown(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   bool inClientArea = false;
   if ((!m_visible) || (!m_enabled))
    return false;
   int widgetTop = this.Top;
   int widgetBottom = this.Bottom;
   int widgetLeft = this.Left;
   int widgetRight = this.Right;
   if(e.X >= widgetLeft &&
    e.X <= widgetRight &&
    e.Y >= widgetTop &&
    e.Y <= widgetBottom)
   {
    if (m_parentWidget != null)
     m_parentWidget.ChildWidgets.BringToFront(this);
    inClientArea = true;
   }
   if(e.Button == System.Windows.Forms.MouseButtons.Left)
   {
    if (HeaderEnabled &&
     e.X >= m_location.X &&
     e.X <= AbsoluteLocation.X + m_size.Width &&
     e.Y >= AbsoluteLocation.Y &&
     e.Y <= AbsoluteLocation.Y + m_currHeaderHeight)
    {
     if (DateTime.Now > m_LastClickTime.AddSeconds(0.5))
     {
      handled = true;
     }
     else
     {
      m_renderBody = !m_renderBody;
     }
     m_LastClickTime = DateTime.Now;
    }
   }
   m_LastMousePosition = new System.Drawing.Point(e.X, e.Y);
   if(!handled && inClientArea && m_renderBody)
   {
    for(int i = 0; i < m_ChildWidgets.Count; i++)
    {
     if(!handled)
     {
      if(m_ChildWidgets[i] is jhuapl.util.IInteractive)
      {
       jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[i] as jhuapl.util.IInteractive;
       handled = currentInteractive.OnMouseDown(e);
      }
     }
    }
   }
   if(inClientArea)
   {
    handled = true;
   }
   return handled;
  }
  public bool OnMouseUp(System.Windows.Forms.MouseEventArgs e)
  {
   if ((!m_visible) || (!m_enabled))
    return false;
   int widgetTop = this.Top;
   int widgetBottom = this.Bottom;
   int widgetLeft = this.Left;
   int widgetRight = this.Right;
   if(e.X >= widgetLeft &&
    e.X <= widgetRight &&
    e.Y >= widgetTop &&
    e.Y <= widgetBottom)
   {
    for(int i = 0; i < m_ChildWidgets.Count; i++)
    {
     if(m_ChildWidgets[i] is jhuapl.util.IInteractive)
     {
      jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[i] as jhuapl.util.IInteractive;
      if (currentInteractive.OnMouseUp(e))
       return true;
     }
    }
   }
   return false;
  }
  public bool OnMouseMove(System.Windows.Forms.MouseEventArgs e)
  {
   if ((!m_visible) || (!m_enabled))
    return false;
   int deltaX = e.X - m_LastMousePosition.X;
   int deltaY = e.Y - m_LastMousePosition.Y;
   m_LastMousePosition = new System.Drawing.Point(e.X, e.Y);
   int widgetTop = this.Top;
   int widgetBottom = this.Bottom;
   int widgetLeft = this.Left;
   int widgetRight = this.Right;
   if(e.X >= widgetLeft &&
    e.X <= widgetRight &&
    e.Y >= widgetTop &&
    e.Y <= widgetBottom)
   {
    for(int i = 0; i < m_ChildWidgets.Count; i++)
    {
     if(m_ChildWidgets[i] is jhuapl.util.IInteractive)
     {
      jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[i] as jhuapl.util.IInteractive;
      if (currentInteractive.OnMouseMove(e))
       return true;
     }
    }
   }
   return false;
  }
  public bool OnMouseWheel(System.Windows.Forms.MouseEventArgs e)
  {
   if ((!m_visible) || (!m_enabled))
    return false;
   int widgetTop = this.Top;
   int widgetBottom = this.Bottom;
   int widgetLeft = this.Left;
   int widgetRight = this.Right;
   if(e.X >= widgetLeft &&
    e.X <= widgetRight &&
    e.Y >= widgetTop &&
    e.Y <= widgetBottom)
   {
    for(int i = 0; i < m_ChildWidgets.Count; i++)
    {
     if(m_ChildWidgets[i] is jhuapl.util.IInteractive)
     {
      jhuapl.util.IInteractive currentInteractive = m_ChildWidgets[i] as jhuapl.util.IInteractive;
      if (currentInteractive.OnMouseWheel(e))
       return true;
     }
    }
   }
   return false;
  }
  public bool OnMouseEnter(EventArgs e)
  {
   return false;
  }
  public bool OnMouseLeave(EventArgs e)
  {
   return false;
  }
  public bool OnKeyDown(System.Windows.Forms.KeyEventArgs e)
  {
   return false;
  }
  public bool OnKeyUp(System.Windows.Forms.KeyEventArgs e)
  {
   return false;
  }
 }
}
