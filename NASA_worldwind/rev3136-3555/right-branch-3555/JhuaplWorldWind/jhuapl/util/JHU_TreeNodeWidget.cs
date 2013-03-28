using System;
using System.Drawing;
using System.Collections;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
namespace jhuapl.util
{
 public abstract class JHU_TreeNodeWidget : JHU_WidgetCollection, jhuapl.util.IWidget, jhuapl.util.IInteractive
 {
  public const int NODE_OFFSET = 5;
  public const int NODE_HEIGHT = 20;
  public const int NODE_INDENT = 15;
  public const int NODE_ARROW_SIZE = 15;
  public const int NODE_CHECKBOX_SIZE = 15;
  protected const int DEFAULT_OPACITY = 150;
  protected Sprite m_sprite;
  protected JHU_IconTexture m_iconTexture;
  protected Hashtable m_textures;
  protected string m_imageName = "";
  protected bool m_hasIcon = false;
  protected float XScale;
  protected float YScale;
  protected int m_itemOnColor = Color.White.ToArgb();
  protected int m_itemOffColor = Color.Gray.ToArgb();
  protected int m_mouseOverColor = Color.FromArgb(DEFAULT_OPACITY,160,160,160).ToArgb();
  protected int m_mouseOverOnColor = Color.White.ToArgb();
  protected int m_mouseOverOffColor = Color.Black.ToArgb();
  protected jhuapl.util.IWidget m_parentWidget = null;
  protected System.Drawing.Point m_location = new System.Drawing.Point(0,0);
  protected System.Drawing.Size m_size = new System.Drawing.Size(0,0);
  protected System.Drawing.Size m_ConsumedSize = new System.Drawing.Size(0,0);
  protected IWidgetCollection m_subNodes = new JHU_WidgetCollection();
  protected bool m_enabled = true;
  protected bool m_visible = true;
  protected bool m_countHeight = true;
  protected bool m_countWidth = true;
  protected object m_tag = null;
  protected string m_name = "";
  protected bool m_isInitialized = false;
  protected bool m_isExpanded = false;
  protected bool m_isMouseOver = false;
  protected bool m_isMouseDown = false;
  protected WorldWind.Renderable.RenderableObject m_renderableObject;
  protected bool m_isRadioButton = false;
  protected bool m_isChecked = true;
  protected bool m_enableCheck = true;
  protected static Microsoft.DirectX.Direct3D.Font m_drawingFont;
  protected static Microsoft.DirectX.Direct3D.Font m_wingdingsFont;
  protected static Microsoft.DirectX.Direct3D.Font m_worldwinddingsFont;
  protected int m_xOffset = 0;
  public string ImageName
  {
   get { return m_imageName; }
   set
   {
    m_imageName = value;
    m_isInitialized = false;
   }
  }
  public bool Expanded
  {
   get { return m_isExpanded; }
   set { m_isExpanded = value; }
  }
  public bool IsRadioButton
  {
   get { return m_isRadioButton; }
   set { m_isRadioButton = value; }
  }
  public bool EnableCheck
  {
   get { return m_enableCheck; }
   set { m_enableCheck = value; }
  }
  public bool IsChecked
  {
   get
   {
    if (m_enableCheck)
     m_isChecked = this.Enabled;
    return m_isChecked;
   }
   set
   {
    if (m_enableCheck)
     this.Enabled = value;
    m_isChecked = value;
   }
  }
  public WorldWind.Renderable.RenderableObject RenderableObject
  {
   get { return m_renderableObject; }
   set
   {
    m_renderableObject = value;
    if (m_renderableObject != null)
    {
     if(m_renderableObject.ParentList != null && m_renderableObject.ParentList.ShowOnlyOneLayer)
      m_isRadioButton = true;
    }
   }
  }
  public jhuapl.util.IWidget ParentWidget
  {
   get { return m_parentWidget; }
   set { m_parentWidget = value; }
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
   get { return this.AbsoluteLocation; }
  }
  public System.Drawing.Size ClientSize
  {
   get
   {
    System.Drawing.Size clientSize = new Size();
    if (m_parentWidget != null)
    {
     m_size.Width = m_parentWidget.ClientSize.Width;
    }
    clientSize.Width = m_size.Width;
    clientSize.Height = m_ConsumedSize.Height;
    return clientSize;
   }
   set { m_size = value; }
  }
  public System.Drawing.Size WidgetSize
  {
   get { return m_ConsumedSize; }
   set { m_ConsumedSize = value; }
  }
  public IWidgetCollection ChildWidgets
  {
   get { return m_subNodes; }
   set { m_subNodes = value; }
  }
  public bool Enabled
  {
   get
   {
    if (m_renderableObject != null)
     m_enabled = m_renderableObject.IsOn;
    return m_enabled;
   }
   set
   {
    m_enabled = value;
    if (m_renderableObject != null)
     m_renderableObject.IsOn = value;
   }
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
  public object Tag
  {
   get { return m_tag; }
   set { m_tag = value; }
  }
  public string Name
  {
   get
   {
    if ( (m_name.Length <= 0) && (m_renderableObject != null) )
    {
     return m_renderableObject.Name;
    }
    return m_name;
   }
   set
   {
    m_name = value;
   }
  }
  public JHU_TreeNodeWidget()
  {
   m_textures = JHU_Globals.getInstance().Textures;
   m_location.X = 0;
   m_location.Y = 0;
   m_size.Height = NODE_HEIGHT;
   m_size.Width = 100;
   m_ConsumedSize = m_size;
   m_isInitialized = false;
  }
  public JHU_TreeNodeWidget(string name) : this()
  {
   m_name = name;
  }
  public void Initialize (DrawArgs drawArgs)
  {
   if (m_drawingFont == null)
   {
    System.Drawing.Font localHeaderFont = new System.Drawing.Font("Arial", 12.0f, FontStyle.Italic | FontStyle.Bold);
    m_drawingFont = new Microsoft.DirectX.Direct3D.Font(drawArgs.device, localHeaderFont);
    System.Drawing.Font wingdings = new System.Drawing.Font("Wingdings", 12.0f);
    m_wingdingsFont = new Microsoft.DirectX.Direct3D.Font(drawArgs.device, wingdings);
    System.Drawing.Font worldwinddings = new System.Drawing.Font("World Wind dings", 12.0f);
    m_worldwinddingsFont = new Microsoft.DirectX.Direct3D.Font(drawArgs.device, worldwinddings);
   }
   if (m_imageName.Trim() != string.Empty)
   {
    object key = null;
    m_iconTexture = (JHU_IconTexture)m_textures[m_imageName];
    if(m_iconTexture==null)
    {
     key = m_imageName;
     m_iconTexture = new JHU_IconTexture( drawArgs.device, JHU_Globals.getInstance().BasePath + @"\Data\Icons\Interface\" + m_imageName );
    }
    if(m_iconTexture!=null)
    {
     m_iconTexture.ReferenceCount++;
     if(key!=null)
     {
      m_textures.Add(key, m_iconTexture);
     }
     if (m_size.Width == 0)
      m_size.Width = m_iconTexture.Width;
     if (m_size.Height == 0)
      m_size.Height = m_iconTexture.Height;
     this.XScale = (float)m_size.Width / m_iconTexture.Width;
     this.YScale = (float)m_size.Height / m_iconTexture.Height;
    }
    if (m_sprite == null)
     m_sprite = new Sprite(drawArgs.device);
   }
   m_isInitialized = true;
  }
  protected MouseClickAction m_leftClickAction;
  public MouseClickAction LeftClickAction
  {
   get { return m_leftClickAction; }
   set { m_leftClickAction = value; }
  }
  protected MouseClickAction m_rightClickAction;
  public MouseClickAction RightClickAction
  {
   get { return m_rightClickAction; }
   set { m_rightClickAction = value; }
  }
  protected MouseClickAction m_checkClickAction;
  public MouseClickAction CheckClickAction
  {
   get { return m_checkClickAction; }
   set { m_checkClickAction = value; }
  }
  protected MouseClickAction m_expandClickAction;
  public MouseClickAction ExpandClickAction
  {
   get { return m_expandClickAction; }
   set { m_expandClickAction = value; }
  }
  public bool OnMouseDown(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   if (m_visible)
   {
    if( e.X >= this.AbsoluteLocation.X &&
     e.X <= this.AbsoluteLocation.X + ClientSize.Width &&
     e.Y >= this.AbsoluteLocation.Y &&
     e.Y <= this.AbsoluteLocation.Y + NODE_HEIGHT)
    {
     m_isMouseDown = true;
     handled = true;
    }
    else
    {
     m_isMouseDown = false;
    }
   }
   if (!handled)
   {
    for(int i = 0; i < m_subNodes.Count; i++)
    {
     if(m_subNodes[i] is jhuapl.util.IInteractive)
     {
      jhuapl.util.IInteractive currentInteractive = m_subNodes[i] as jhuapl.util.IInteractive;
      handled = currentInteractive.OnMouseDown(e);
     }
     if (handled)
      continue;
    }
   }
   return handled;
  }
  public bool OnMouseUp(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   if (m_visible)
   {
    if( e.X >= this.AbsoluteLocation.X &&
     e.X <= this.AbsoluteLocation.X + ClientSize.Width &&
     e.Y >= this.AbsoluteLocation.Y &&
     e.Y <= this.AbsoluteLocation.Y + NODE_HEIGHT)
    {
     if (m_isMouseDown)
     {
      if ((e.X > this.AbsoluteLocation.X + m_xOffset) &&
       (e.X < this.AbsoluteLocation.X + m_xOffset + NODE_ARROW_SIZE))
      {
       this.Expanded = !this.Expanded;
       if (m_expandClickAction != null)
        m_expandClickAction(e);
      }
      else if (m_enableCheck &&
         (e.X > this.AbsoluteLocation.X + m_xOffset + NODE_ARROW_SIZE) &&
         (e.X < this.AbsoluteLocation.X + m_xOffset + NODE_ARROW_SIZE + NODE_CHECKBOX_SIZE))
      {
       this.Enabled = !this.Enabled;
       if (m_checkClickAction != null)
        m_checkClickAction(e);
      }
      else if ((e.Button == System.Windows.Forms.MouseButtons.Left) && (m_leftClickAction != null))
      {
       m_leftClickAction(e);
      }
      else if ((e.Button == System.Windows.Forms.MouseButtons.Right) && (m_rightClickAction != null))
      {
       m_rightClickAction(e);
      }
      handled = true;
     }
    }
   }
   if (!handled)
   {
    for(int i = 0; i < m_subNodes.Count; i++)
    {
     if(m_subNodes[i] is jhuapl.util.IInteractive)
     {
      jhuapl.util.IInteractive currentInteractive = m_subNodes[i] as jhuapl.util.IInteractive;
      handled = currentInteractive.OnMouseUp(e);
     }
     if (handled)
      continue;
    }
   }
   m_isMouseDown = false;
   return handled;
  }
  public bool OnMouseMove(System.Windows.Forms.MouseEventArgs e)
  {
   bool handled = false;
   if (m_visible)
   {
    if( e.X >= this.AbsoluteLocation.X &&
     e.X <= this.AbsoluteLocation.X + ClientSize.Width &&
     e.Y >= this.AbsoluteLocation.Y &&
     e.Y <= this.AbsoluteLocation.Y + NODE_HEIGHT)
    {
     if (!m_isMouseOver)
      this.OnMouseEnter(e);
     handled = true;
    }
    else
    {
     if (m_isMouseOver)
      this.OnMouseLeave(e);
    }
   }
   else
   {
    m_isMouseOver = false;
   }
   for(int i = 0; i < m_subNodes.Count; i++)
   {
    if(m_subNodes[i] is jhuapl.util.IInteractive)
    {
     jhuapl.util.IInteractive currentInteractive = m_subNodes[i] as jhuapl.util.IInteractive;
     if (currentInteractive.OnMouseMove(e))
      handled = true;
    }
   }
   return handled;
  }
  public bool OnMouseWheel(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
  public bool OnMouseEnter(EventArgs e)
  {
   m_isMouseOver = true;
   return false;
  }
  public bool OnMouseLeave(EventArgs e)
  {
   m_isMouseOver = false;
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
  public void Render(DrawArgs drawArgs)
  {
   int consumedPixels = 0;
   consumedPixels = this.Render(drawArgs, NODE_OFFSET, 0);
   m_ConsumedSize.Height = consumedPixels;
  }
  new public void Add(jhuapl.util.IWidget widget)
  {
   m_subNodes.Add(widget);
   widget.ParentWidget = this;
  }
  new public void Remove(jhuapl.util.IWidget widget)
  {
   m_subNodes.Remove(widget);
  }
  public abstract int Render(DrawArgs drawArgs, int xOffset, int yOffset);
 }
}
