using System;
using System.Drawing;
using System.Windows.Forms;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
namespace jhuapl.util
{
 public class JHU_FormWidget : JHU_WidgetCollection, jhuapl.util.IWidget, jhuapl.util.IInteractive
 {
  [Flags]
  public enum ResizeDirection : ushort
  {
   None = 0x00,
   Left = 0x01,
   Right = 0x02,
   Up = 0x04,
   Down = 0x08,
   UL = 0x05,
   UR = 0x06,
   DL = 0x09,
   DR = 0x0A
  }
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
  protected int m_scrollbarWidth = 20;
  protected int m_vScrollbarPos = 0;
  protected int m_vScrollbarHeight = 0;
  protected double m_vScrollbarPercent = 0.0;
  protected int m_vScrollbarGrabPosition = 0;
  protected bool m_showVScrollbar = false;
  protected bool m_isVScrolling = false;
  protected int m_scrollbarHeight = 20;
  protected int m_hScrollbarPos = 0;
  protected int m_hScrollbarWidth = 0;
  protected double m_hScrollbarPercent = 0.0;
  protected int m_hScrollbarGrabPosition = 0;
  protected bool m_showHScrollbar = false;
  protected bool m_isHScrolling = false;
  protected bool m_renderBody = true;
  protected Microsoft.DirectX.Direct3D.Font m_TextFont;
  protected Microsoft.DirectX.Direct3D.Font m_TitleFont;
  protected Microsoft.DirectX.Direct3D.Font m_wingdingsFont;
  protected Microsoft.DirectX.Direct3D.Font m_worldwinddingsFont;
  protected int resizeBuffer = 5;
  protected Vector2[] m_OutlineVertsHeader = new Vector2[5];
  protected Vector2[] m_OutlineVerts = new Vector2[5];
  protected bool m_isDragging = false;
  protected System.Drawing.Point m_LastMousePosition = new System.Drawing.Point(0,0);
  protected DateTime m_LastClickTime;
  protected ResizeDirection m_resize = ResizeDirection.None;
  protected int m_distanceFromTop = 0;
  protected int m_distanceFromBottom = 0;
  protected int m_distanceFromLeft = 0;
  protected int m_distanceFromRight = 0;
  public string Text = "";
  public bool HeaderEnabled = true;
  public bool AutoHideHeader = false;
  public System.Drawing.Size MinSize = new System.Drawing.Size(20, 100);
  public bool VerticalResizeEnabled = true;
  public bool HorizontalResizeEnabled = true;
  public bool VerticalScrollbarEnabled = true;
  public bool HorizontalScrollbarEnabled = true;
  public bool DestroyOnClose = false;
  public JHU_Enums.AnchorStyles Anchor = JHU_Enums.AnchorStyles.None;
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
  public JHU_FormWidget(string name)
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
   get
   {
    if ( (Anchor == JHU_Enums.AnchorStyles.Bottom) && (m_parentWidget != null))
    {
     if (m_location.Y - m_parentWidget.ClientSize.Height != m_distanceFromBottom)
     {
      m_location.Y = m_parentWidget.ClientSize.Height - m_distanceFromBottom;
     }
    }
    if ( (Anchor == JHU_Enums.AnchorStyles.Right) && (m_parentWidget != null))
    {
     if (m_location.X - m_parentWidget.ClientSize.Width != m_distanceFromRight)
     {
      m_location.X = m_parentWidget.ClientSize.Width - m_distanceFromRight;
     }
    }
    return m_location;
   }
   set
   {
    m_location = value;
    UpdateLocation();
   }
  }
  public System.Drawing.Point AbsoluteLocation
  {
   get
   {
    if(m_parentWidget != null)
    {
     return new System.Drawing.Point(
      Location.X + m_parentWidget.ClientLocation.X,
      Location.Y + m_parentWidget.ClientLocation.Y);
    }
    else
    {
     return this.Location;
    }
   }
  }
  public System.Drawing.Point ClientLocation
  {
   get
   {
    m_clientLocation = this.BodyLocation;
    if (m_showVScrollbar)
    {
     if (m_vScrollbarPercent < .01)
      m_vScrollbarPercent = .01;
     m_clientLocation.Y -= (int) (m_vScrollbarPos / m_vScrollbarPercent);
    }
    if (m_showHScrollbar)
    {
     if (m_hScrollbarPercent < .01)
      m_hScrollbarPercent = .01;
     m_clientLocation.X -= (int) (m_hScrollbarPos / m_hScrollbarPercent);
    }
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
    if (m_showHScrollbar)
     m_clientSize.Height -= m_scrollbarHeight;
    if (m_showVScrollbar)
     m_clientSize.Width -= m_scrollbarWidth;
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
   UpdateLocation();
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
   if(WorldWind.DrawArgs.LastMousePosition.X > widgetLeft - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetLeft + resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetTop - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetTop + resizeBuffer)
   {
    WorldWind.DrawArgs.MouseCursor = CursorType.Cross;
   }
   else if(WorldWind.DrawArgs.LastMousePosition.X > widgetRight - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetRight + resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetTop - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetTop + resizeBuffer)
   {
    WorldWind.DrawArgs.MouseCursor = CursorType.Cross;
   }
   else if(WorldWind.DrawArgs.LastMousePosition.X > widgetLeft - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetLeft + resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetBottom - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetBottom + resizeBuffer)
   {
    WorldWind.DrawArgs.MouseCursor = CursorType.Cross;
   }
   else if(WorldWind.DrawArgs.LastMousePosition.X > widgetRight - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetRight + resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetBottom - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetBottom + resizeBuffer)
   {
    WorldWind.DrawArgs.MouseCursor = CursorType.Cross;
   }
   else if(
    (WorldWind.DrawArgs.LastMousePosition.X > widgetLeft - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetLeft + resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetTop - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetBottom + resizeBuffer) ||
    (WorldWind.DrawArgs.LastMousePosition.X > widgetRight - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetRight + resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetTop - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetBottom + resizeBuffer))
   {
    WorldWind.DrawArgs.MouseCursor = CursorType.SizeWE;
   }
   else if(
    (WorldWind.DrawArgs.LastMousePosition.X > widgetLeft - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetRight + resizeBuffer&&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetTop - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetTop + resizeBuffer) ||
    (WorldWind.DrawArgs.LastMousePosition.X > widgetLeft- resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.X < widgetRight + resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y > widgetBottom - resizeBuffer &&
    WorldWind.DrawArgs.LastMousePosition.Y < widgetBottom + resizeBuffer))
   {
    WorldWind.DrawArgs.MouseCursor = CursorType.SizeWE;
   }
   m_currHeaderHeight = 0;
   if(HeaderEnabled || (AutoHideHeader &&
    WorldWind.DrawArgs.LastMousePosition.X >= widgetLeft &&
    WorldWind.DrawArgs.LastMousePosition.X <= widgetRight &&
    WorldWind.DrawArgs.LastMousePosition.Y >= widgetTop &&
    WorldWind.DrawArgs.LastMousePosition.Y <= widgetBottom))
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
    int widthLeft = m_size.Width - 20;
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
    m_worldwinddingsFont.DrawText(
     null,
     "E",
     new System.Drawing.Rectangle(this.AbsoluteLocation.X + m_size.Width - 18, this.AbsoluteLocation.Y+2, 20, m_currHeaderHeight),
     DrawTextFormat.None,
     m_TextColor.ToArgb());
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
    m_showVScrollbar = false;
    m_showHScrollbar = false;
    if ( (childrenHeight > bodyHeight) && (VerticalScrollbarEnabled) )
    {
     m_showVScrollbar = true;
     bodyWidth -= m_scrollbarWidth;
     if ( (childrenWidth > bodyWidth) && (HorizontalScrollbarEnabled) )
     {
      m_showHScrollbar = true;
      bodyHeight -= m_scrollbarHeight;
     }
    }
    else
    {
     if ( (childrenWidth > m_size.Width) && (HorizontalScrollbarEnabled) )
     {
      m_showHScrollbar = true;
      bodyHeight -= m_scrollbarHeight;
      if ( (childrenHeight > bodyHeight) && (VerticalScrollbarEnabled) )
      {
       m_showVScrollbar = true;
       bodyWidth -= m_scrollbarWidth;
      }
     }
    }
    if (m_showVScrollbar)
    {
     m_vScrollbarPercent = (double)bodyHeight/((double)childrenHeight);
     m_vScrollbarHeight = (int)(bodyHeight * m_vScrollbarPercent);
     if (m_vScrollbarPos < 0)
     {
      m_vScrollbarPos = 0;
     }
     else if (m_vScrollbarPos > bodyHeight - m_vScrollbarHeight)
     {
      m_vScrollbarPos = bodyHeight - m_vScrollbarHeight;
     }
     int color = (m_isVScrolling ? System.Drawing.Color.White.ToArgb() : System.Drawing.Color.Gray.ToArgb());
     JHU_Utilities.DrawBox(
      BodyLocation.X + bodyWidth + 2,
      BodyLocation.Y + m_vScrollbarPos + 1,
      m_scrollbarWidth - 3,
      m_vScrollbarHeight - 2,
      0.0f,
      color,
      drawArgs.device);
     m_OutlineVerts[0].X = AbsoluteLocation.X + m_size.Width - m_scrollbarWidth;
     m_OutlineVerts[0].Y = AbsoluteLocation.Y + m_currHeaderHeight;
     m_OutlineVerts[1].X = AbsoluteLocation.X + m_size.Width;
     m_OutlineVerts[1].Y = AbsoluteLocation.Y + m_currHeaderHeight;
     m_OutlineVerts[2].X = AbsoluteLocation.X + m_size.Width ;
     m_OutlineVerts[2].Y = AbsoluteLocation.Y + m_size.Height;
     m_OutlineVerts[3].X = AbsoluteLocation.X + m_size.Width - m_scrollbarWidth;
     m_OutlineVerts[3].Y = AbsoluteLocation.Y + m_size.Height;
     m_OutlineVerts[4].X = AbsoluteLocation.X + m_size.Width - m_scrollbarWidth;
     m_OutlineVerts[4].Y = AbsoluteLocation.Y + m_currHeaderHeight;
     JHU_Utilities.DrawLine(m_OutlineVerts, m_BorderColor.ToArgb(), drawArgs.device);
    }
    else
    {
     m_vScrollbarPos = 0;
    }
    if (m_showHScrollbar)
    {
     m_hScrollbarPercent = (double)bodyWidth/((double)childrenWidth);
     m_hScrollbarWidth = (int)(bodyWidth * m_hScrollbarPercent);
     if (m_hScrollbarPos < 0)
     {
      m_hScrollbarPos = 0;
     }
     else if (m_hScrollbarPos > bodyWidth - m_hScrollbarWidth)
     {
      m_hScrollbarPos = bodyWidth - m_hScrollbarWidth;
     }
     int color = (m_isHScrolling ? System.Drawing.Color.White.ToArgb() : System.Drawing.Color.Gray.ToArgb());
     JHU_Utilities.DrawBox(
      BodyLocation.X + m_hScrollbarPos + 1,
      BodyLocation.Y + bodyHeight + 2,
      m_hScrollbarWidth - 3,
      m_scrollbarHeight - 2,
      0.0f,
      color,
      drawArgs.device);
     m_OutlineVerts[0].X = AbsoluteLocation.X;
     m_OutlineVerts[0].Y = AbsoluteLocation.Y + bodyHeight + m_currHeaderHeight;
     m_OutlineVerts[1].X = AbsoluteLocation.X + m_size.Width;
     m_OutlineVerts[1].Y = AbsoluteLocation.Y + bodyHeight + m_currHeaderHeight;
     m_OutlineVerts[2].X = AbsoluteLocation.X + m_size.Width;
     m_OutlineVerts[2].Y = AbsoluteLocation.Y + m_size.Height;
     m_OutlineVerts[3].X = AbsoluteLocation.X;
     m_OutlineVerts[3].Y = AbsoluteLocation.Y + m_size.Height;
     m_OutlineVerts[4].X = AbsoluteLocation.X ;
     m_OutlineVerts[4].Y = AbsoluteLocation.Y + bodyHeight + m_currHeaderHeight;
     JHU_Utilities.DrawLine(m_OutlineVerts, m_BorderColor.ToArgb(), drawArgs.device);
    }
    else
    {
     m_hScrollbarPos = 0;
    }
    Viewport clientViewPort = new Viewport();
    clientViewPort.X = BodyLocation.X;
    clientViewPort.Y = BodyLocation.Y;
    clientViewPort.Width = ClientSize.Width;
    clientViewPort.Height = ClientSize.Height;
    if (this.m_parentWidget != null)
    {
     if (BodyLocation.X + ClientSize.Width > m_parentWidget.ClientSize.Width + m_parentWidget.ClientLocation.X)
      clientViewPort.Width = (m_parentWidget.ClientSize.Width + m_parentWidget.ClientLocation.X) - BodyLocation.X;
     if (BodyLocation.Y + ClientSize.Height > m_parentWidget.ClientSize.Height + m_parentWidget.ClientLocation.Y)
      clientViewPort.Height = (m_parentWidget.ClientSize.Height + m_parentWidget.ClientLocation.Y) - BodyLocation.Y;
    }
    Viewport defaultViewPort = drawArgs.device.Viewport;
    drawArgs.device.Viewport = clientViewPort;
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
    drawArgs.device.Viewport = defaultViewPort;
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
    m_isDragging = false;
    m_resize = ResizeDirection.None;
    m_isVScrolling = false;
    m_isHScrolling = false;
    if(e.X > widgetLeft - resizeBuffer &&
     e.X < widgetLeft + resizeBuffer &&
     e.Y > widgetTop - resizeBuffer &&
     e.Y < widgetTop + resizeBuffer)
    {
     if ((HorizontalResizeEnabled) && (VerticalResizeEnabled))
      m_resize = ResizeDirection.UL;
     else if (HorizontalResizeEnabled)
      m_resize = ResizeDirection.Left;
     else if (VerticalResizeEnabled)
      m_resize = ResizeDirection.Up;
    }
    else if(e.X > widgetRight - resizeBuffer &&
     e.X < widgetRight + resizeBuffer &&
     e.Y > widgetTop - resizeBuffer &&
     e.Y < widgetTop + resizeBuffer)
    {
     if ((HorizontalResizeEnabled) && (VerticalResizeEnabled))
      m_resize = ResizeDirection.UR;
     else if (HorizontalResizeEnabled)
      m_resize = ResizeDirection.Right;
     else if (VerticalResizeEnabled)
      m_resize = ResizeDirection.Up;
    }
    else if(e.X > widgetLeft - resizeBuffer &&
     e.X < widgetLeft + resizeBuffer &&
     e.Y > widgetBottom - resizeBuffer &&
     e.Y < widgetBottom + resizeBuffer)
    {
     if ((HorizontalResizeEnabled) && (VerticalResizeEnabled))
      m_resize = ResizeDirection.DL;
     else if (HorizontalResizeEnabled)
      m_resize = ResizeDirection.Left;
     else if (VerticalResizeEnabled)
      m_resize = ResizeDirection.Down;
    }
    else if(e.X > widgetRight - resizeBuffer &&
     e.X < widgetRight + resizeBuffer &&
     e.Y > widgetBottom - resizeBuffer &&
     e.Y < widgetBottom + resizeBuffer )
    {
     if ((HorizontalResizeEnabled) && (VerticalResizeEnabled))
      m_resize = ResizeDirection.DR;
     else if (HorizontalResizeEnabled)
      m_resize = ResizeDirection.Right;
     else if (VerticalResizeEnabled)
      m_resize = ResizeDirection.Down;
    }
    else if(e.X > AbsoluteLocation.X - resizeBuffer &&
     e.X < AbsoluteLocation.X + resizeBuffer &&
     e.Y > AbsoluteLocation.Y - resizeBuffer &&
     e.Y < AbsoluteLocation.Y + resizeBuffer + m_size.Height &&
     HorizontalResizeEnabled)
    {
     m_resize = ResizeDirection.Left;
    }
    else if(e.X > AbsoluteLocation.X - resizeBuffer + m_size.Width &&
     e.X < AbsoluteLocation.X + resizeBuffer + m_size.Width &&
     e.Y > AbsoluteLocation.Y - resizeBuffer &&
     e.Y < AbsoluteLocation.Y + resizeBuffer + m_size.Height &&
     HorizontalResizeEnabled)
    {
     m_resize = ResizeDirection.Right;
    }
    else if(e.X > AbsoluteLocation.X - resizeBuffer &&
     e.X < AbsoluteLocation.X + resizeBuffer + m_size.Width &&
     e.Y > AbsoluteLocation.Y - resizeBuffer &&
     e.Y < AbsoluteLocation.Y + resizeBuffer &&
     VerticalResizeEnabled)
    {
     m_resize = ResizeDirection.Up;
    }
    else if(e.X > AbsoluteLocation.X - resizeBuffer &&
     e.X < AbsoluteLocation.X + resizeBuffer + m_size.Width &&
     e.Y > AbsoluteLocation.Y - resizeBuffer + m_size.Height &&
     e.Y < AbsoluteLocation.Y + resizeBuffer + m_size.Height &&
     VerticalResizeEnabled)
    {
     m_resize = ResizeDirection.Down;
    }
    else if(HeaderEnabled &&
     e.X >= Location.X + m_size.Width - 18 &&
     e.X <= AbsoluteLocation.X + m_size.Width &&
     e.Y >= AbsoluteLocation.Y + 2 &&
     e.Y <= AbsoluteLocation.Y + m_currHeaderHeight - 2)
    {
     Visible = false;
     if (DestroyOnClose)
     {
      Enabled = false;
      JHU_WidgetCollection parentCollection = (JHU_WidgetCollection) m_parentWidget;
      if (parentCollection != null)
       parentCollection.Remove(this);
      this.Dispose();
     }
    }
    else if(HeaderEnabled &&
     e.X >= Location.X &&
     e.X <= AbsoluteLocation.X + m_size.Width &&
     e.Y >= AbsoluteLocation.Y &&
     e.Y <= AbsoluteLocation.Y + m_currHeaderHeight)
    {
     if (DateTime.Now > m_LastClickTime.AddSeconds(0.5))
     {
      m_isDragging = true;
      handled = true;
     }
     else
     {
      m_renderBody = !m_renderBody;
      if (AutoHideHeader && m_renderBody)
      {
       HeaderEnabled = false;
      }
      else
      {
       HeaderEnabled = true;
      }
     }
     m_LastClickTime = DateTime.Now;
    }
    if (inClientArea && m_renderBody)
    {
     if( m_showVScrollbar &&
      e.X > this.Right - m_scrollbarWidth &&
      (!m_showHScrollbar || e.Y < this.Bottom - m_scrollbarWidth) )
     {
      if (e.Y < this.BodyLocation.Y + m_vScrollbarPos)
      {
       m_vScrollbarPos -= m_clientSize.Height / 10;
      }
      else if (e.Y > this.BodyLocation.Y + m_vScrollbarPos + m_vScrollbarHeight)
      {
       m_vScrollbarPos += m_clientSize.Height / 10;
      }
      else
      {
       m_vScrollbarGrabPosition = e.Y - this.BodyLocation.Y;
       m_isVScrolling = true;
      }
      handled = true;
     }
     else if( m_showHScrollbar &&
      e.Y > this.Bottom - m_scrollbarWidth &&
      (!m_showVScrollbar || e.X < this.Right - m_scrollbarWidth) )
     {
      if (e.X < this.BodyLocation.X + m_hScrollbarPos)
      {
       m_hScrollbarPos -= m_clientSize.Width / 10;
      }
      else if (e.X > this.BodyLocation.X + m_hScrollbarPos + m_hScrollbarWidth)
      {
       m_hScrollbarPos += m_clientSize.Width / 10;
      }
      else
      {
       m_hScrollbarGrabPosition = e.X - this.BodyLocation.X;
       m_isHScrolling = true;
      }
      handled = true;
     }
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
   if(inClientArea || (m_resize != ResizeDirection.None))
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
   m_isVScrolling = false;
   m_isHScrolling = false;
   if (((m_isDragging) || (m_resize != ResizeDirection.None)) &&
    (e.Button == System.Windows.Forms.MouseButtons.Left))
   {
    m_isDragging = false;
    m_resize = ResizeDirection.None;
    return true;
   }
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
   if(m_isDragging)
   {
    m_location.X += deltaX;
    m_location.Y += deltaY;
    UpdateLocation();
    return true;
   }
   if (m_resize != ResizeDirection.None)
   {
    if ((ResizeDirection.Up & m_resize) > 0)
    {
     m_location.Y += deltaY;
     m_size.Height -= deltaY;
    }
    if ((ResizeDirection.Down & m_resize) > 0)
    {
     m_size.Height += deltaY;
    }
    if ((ResizeDirection.Right & m_resize) > 0)
    {
     m_size.Width += deltaX;
    }
    if ((ResizeDirection.Left & m_resize) > 0)
    {
     m_location.X += deltaX;
     m_size.Width -= deltaX;
    }
    if(m_size.Width < MinSize.Width)
    {
     m_size.Width = MinSize.Width;
    }
    if(m_size.Height < MinSize.Height)
    {
     m_size.Height = MinSize.Height;
    }
    UpdateLocation();
    return true;
   }
   if (m_isVScrolling)
   {
    m_vScrollbarPos = e.Y - m_vScrollbarGrabPosition - this.BodyLocation.Y;
    return true;
   }
   if (m_isHScrolling)
   {
    m_hScrollbarPos = e.X - m_hScrollbarGrabPosition - this.BodyLocation.X;
    return true;
   }
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
   if(e.X > this.Right || e.X < this.Left || e.Y < this.Top || e.Y > this.Bottom)
    return false;
   this.m_vScrollbarPos -= (e.Delta/10);
   return true;
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
  protected void UpdateLocation()
  {
   int height;
   int width;
   if (m_parentWidget != null)
   {
    height = m_parentWidget.ClientSize.Height;
    width = m_parentWidget.ClientSize.Width;
   }
   else
   {
    height = JHU_Globals.getInstance().WorldWindow.DrawArgs.screenHeight;
    width = JHU_Globals.getInstance().WorldWindow.DrawArgs.screenWidth;
   }
   if (Anchor != JHU_Enums.AnchorStyles.None)
   {
    m_distanceFromTop = m_location.Y;
    m_distanceFromLeft = m_location.X;
    m_distanceFromBottom = height - m_location.Y;
    m_distanceFromRight = width - m_location.X;
    if (m_distanceFromTop < 0) m_distanceFromTop = 0;
    if (m_distanceFromBottom < m_currHeaderHeight) m_distanceFromBottom = m_currHeaderHeight;
    if (m_distanceFromLeft < 0) m_distanceFromLeft = 0;
    if (m_distanceFromRight < m_currHeaderHeight) m_distanceFromRight = m_currHeaderHeight;
   }
   if(m_location.X < 0)
    m_location.X = 0;
   if(m_location.Y < 0)
    m_location.Y = 0;
   if(m_location.Y + m_headerHeight > height)
    m_location.Y = height - m_currHeaderHeight;
   if(m_location.X + m_headerHeight > width)
    m_location.X = width - m_currHeaderHeight;
  }
 }
}
