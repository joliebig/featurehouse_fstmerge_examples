using System;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
namespace WorldWind.Widgets
{
 public class LayerManager : Widgets.Form
 {
  TreeView tv = new TreeView();
  public LayerManager()
  {
   this.Text = "Layer Manager";
   this.Name = "Layer Manager";
   this.AutoHideHeader = false;
   this.HeaderColor = System.Drawing.Color.FromArgb(
    120,
    System.Drawing.Color.Coral.R,
    System.Drawing.Color.Coral.G,
    System.Drawing.Color.Coral.B);
   this.BackgroundColor = System.Drawing.Color.FromArgb(
    100, 0, 0, 0);
   tv.ParentWidget = this;
   this.ChildWidgets.Add(tv);
   ClientSize = new System.Drawing.Size(300, 200);
  }
  private LayerManagerTreeNode getTreeNodeFromIRenderable(WorldWind.Renderable.RenderableObject renderable)
  {
   LayerManagerTreeNode rootNode = new LayerManagerTreeNode(renderable.Name);
   rootNode.Tag = renderable;
   if(renderable is WorldWind.Renderable.RenderableObjectList)
   {
    WorldWind.Renderable.RenderableObjectList rol = (WorldWind.Renderable.RenderableObjectList)renderable;
    if(rol.ChildObjects != null && rol.ChildObjects.Count > 0)
    {
     for(int childIndex = 0; childIndex < rol.ChildObjects.Count; childIndex++)
     {
      rootNode.Children.Add(getTreeNodeFromIRenderable((WorldWind.Renderable.RenderableObject)rol.ChildObjects[childIndex]));
     }
    }
   }
   return rootNode;
  }
  private void mergeNodes(LayerManagerTreeNode source, LayerManagerTreeNode dest)
  {
   if(source.Text != dest.Text)
   {
    dest.Text = source.Text;
   }
   for(int i = 0; i < source.Children.Count; i++)
   {
    if(dest.Children.Count <= i)
    {
     dest.Children.Add(source.Children[i] as LayerManagerTreeNode);
    }
    else
    {
     mergeNodes(source.Children[i] as LayerManagerTreeNode, dest.Children[i] as LayerManagerTreeNode);
    }
   }
  }
  public override void Render(DrawArgs drawArgs)
  {
   tv.ClientLocation = new System.Drawing.Point(0, 20);
   tv.ClientSize = new System.Drawing.Size(ClientSize.Width, ClientSize.Height - 20);
   if(drawArgs.CurrentWorld != null)
   {
    for(int renderableIndex = 0; renderableIndex < drawArgs.CurrentWorld.RenderableObjects.ChildObjects.Count; renderableIndex++)
    {
     LayerManagerTreeNode currentNode = getTreeNodeFromIRenderable((WorldWind.Renderable.RenderableObject)drawArgs.CurrentWorld.RenderableObjects.ChildObjects[renderableIndex]);
     if(tv.Nodes.Count <= renderableIndex)
     {
      tv.Nodes.Add(currentNode);
     }
     else
     {
      mergeNodes(currentNode, tv.Nodes[renderableIndex] as LayerManagerTreeNode);
     }
    }
   }
   base.Render(drawArgs);
  }
 }
 public class TreeView : IWidget, IInteractive
 {
  IWidgetCollection m_ChildWidgets = new WidgetCollection();
  int m_NodeXOffset = 5;
  int m_NodeYOffset = 0;
  int m_NodeSpacing = 0;
  System.Drawing.Point m_Location = new System.Drawing.Point(0,0);
  System.Collections.ArrayList m_Nodes = new System.Collections.ArrayList();
  System.Drawing.Size m_Size = new System.Drawing.Size(300, 300);
  IWidget m_ParentWidget = null;
  bool m_Enabled = true;
  bool m_Visible = true;
  object m_Tag = null;
  string m_Name = "";
  public TreeView()
  {
  }
  public string Name
  {
   get
   {
    return m_Name;
   }
   set
   {
    m_Name = value;
   }
  }
  public System.Collections.ArrayList Nodes
  {
   get
   {
    return m_Nodes;
   }
   set
   {
    m_Nodes = value;
   }
  }
  public IWidget ParentWidget
  {
   get
   {
    return m_ParentWidget;
   }
   set
   {
    m_ParentWidget = value;
   }
  }
  public bool Visible
  {
   get
   {
    return m_Visible;
   }
   set
   {
    m_Visible = value;
   }
  }
  public object Tag
  {
   get
   {
    return m_Tag;
   }
   set
   {
    m_Tag = value;
   }
  }
  public IWidgetCollection ChildWidgets
  {
   get
   {
    return m_ChildWidgets;
   }
   set
   {
    m_ChildWidgets = value;
   }
  }
  public System.Drawing.Size ClientSize
  {
   get
   {
    return m_Size;
   }
   set
   {
    m_Size = value;
   }
  }
  public bool Enabled
  {
   get
   {
    return m_Enabled;
   }
   set
   {
    m_Enabled = value;
   }
  }
  public System.Drawing.Point ClientLocation
  {
   get
   {
    return m_Location;
   }
   set
   {
    m_Location = value;
   }
  }
  public int NodeHeight
  {
   get
   {
    int nodeHeight = 0;
    for(int i = 0; i < m_Nodes.Count; i++)
    {
     TreeNode currentNode = m_Nodes[i] as TreeNode;
     nodeHeight += currentNode.Height;
    }
    return nodeHeight;
   }
  }
  public System.Drawing.Point AbsoluteLocation
  {
   get
   {
    if(m_ParentWidget != null)
    {
     return new System.Drawing.Point(
      m_Location.X + m_ParentWidget.AbsoluteLocation.X,
      m_Location.Y + m_ParentWidget.AbsoluteLocation.Y);
    }
    else
    {
     return m_Location;
    }
   }
  }
  public void Render(DrawArgs drawArgs)
  {
   int totalHeight = getNodesHeight();
   double percentHeight = (double)ClientSize.Height / totalHeight;
   bool showScrollbar = (totalHeight > ClientSize.Height ? true : false);
   if(showScrollbar)
   {
    int scrollbarHeight = (int)(ClientSize.Height * percentHeight);
    if(relativeScrollBarPosition < 0)
    {
     relativeScrollBarPosition = 0;
    }
    else if(relativeScrollBarPosition > ClientSize.Height - scrollbarHeight)
    {
     relativeScrollBarPosition = ClientSize.Height - scrollbarHeight;
    }
    int color = (isScrolling ? System.Drawing.Color.White.ToArgb() : System.Drawing.Color.Gray.ToArgb());
    Widgets.Utilities.DrawBox(
     AbsoluteLocation.X + ClientSize.Width - ScrollBarWidth + 2,
     AbsoluteLocation.Y + relativeScrollBarPosition + 1,
     ScrollBarWidth - 3,
     scrollbarHeight - 2,
     0.0f,
     color,
     drawArgs.device);
   }
   if(m_Nodes.Count > 0)
   {
    int currentHeight = m_NodeYOffset;
    for(int nodeIndex = 0; nodeIndex < m_Nodes.Count; nodeIndex++)
    {
     TreeNode currentNode = m_Nodes[nodeIndex] as TreeNode;
     currentNode.Location = new System.Drawing.Point(AbsoluteLocation.X + m_NodeXOffset, AbsoluteLocation.Y + currentHeight);
     currentNode.Width = m_Size.Width - m_NodeXOffset - ScrollBarWidth;
     if(showScrollbar)
     {
      currentNode.Render(drawArgs, (int)(-(relativeScrollBarPosition * (1.0/percentHeight))), AbsoluteLocation.Y, m_Size.Height - currentHeight, isScrolling);
     }
     else
     {
      currentNode.Render(drawArgs, 0, AbsoluteLocation.Y, m_Size.Height - currentHeight, false);
     }
     currentHeight += currentNode.Height + m_NodeSpacing;
    }
   }
  }
  private void updateScrollBar(System.Drawing.Point mousePosition)
  {
  }
  int relativeScrollBarPosition = 0;
  int ScrollBarWidth = 20;
  bool isScrolling = false;
  System.Drawing.Point m_LastMousePosition = System.Drawing.Point.Empty;
  public bool OnMouseDown(System.Windows.Forms.MouseEventArgs e)
  {
   if(e.X > AbsoluteLocation.X + ClientSize.Width || e.X < AbsoluteLocation.X || e.Y < AbsoluteLocation.Y || e.Y > AbsoluteLocation.Y + ClientSize.Height)
    return false;
   if(e.X > AbsoluteLocation.X + ClientSize.Width - 5 && e.X < AbsoluteLocation.X + ClientSize.Width + 5)
   {
    return true;
   }
   if(e.Y < AbsoluteLocation.Y)
    return false;
   m_LastMousePosition = new System.Drawing.Point(e.X, e.Y);
   if(e.X > AbsoluteLocation.X + ClientSize.Width - ScrollBarWidth)
   {
    int nodesHeight = getNodesHeight();
    if(nodesHeight > ClientSize.Height)
    {
     double percentHeight = (double)ClientSize.Height / nodesHeight;
     if(percentHeight > 1)
      percentHeight = 1;
     int scrollBarHeight = (int)(percentHeight * ClientSize.Height);
     if(e.Y > relativeScrollBarPosition + AbsoluteLocation.Y &&
      e.Y < relativeScrollBarPosition + AbsoluteLocation.Y + scrollBarHeight)
     {
      isScrolling = true;
     }
    }
   }
   else
   {
    for(int nodeIndex = 0; nodeIndex < m_Nodes.Count; nodeIndex++)
    {
     TreeNode currentNode = m_Nodes[nodeIndex] as TreeNode;
     currentNode.OnMouseDown(e);
    }
   }
   return true;
  }
  private int getNodesHeight()
  {
   int nodesHeight = 0;
   for(int i = 0; i < m_Nodes.Count; i++)
   {
    TreeNode currentNode = m_Nodes[i] as TreeNode;
    nodesHeight += currentNode.Height;
   }
   return nodesHeight;
  }
  public bool OnMouseUp(System.Windows.Forms.MouseEventArgs e)
  {
   if(this.isScrolling)
   {
    isScrolling = false;
    return true;
   }
   for(int nodeIndex = 0; nodeIndex < m_Nodes.Count; nodeIndex++)
   {
    TreeNode currentNode = m_Nodes[nodeIndex] as TreeNode;
    currentNode.OnMouseUp(e);
   }
   if(e.X > AbsoluteLocation.Y &&
    e.X < AbsoluteLocation.X + ClientSize.Width &&
    e.Y > AbsoluteLocation.Y &&
    e.Y < AbsoluteLocation.Y + ClientSize.Height)
    return true;
   else
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
  public bool OnKeyPress(System.Windows.Forms.KeyPressEventArgs e)
  {
   return false;
  }
  public bool OnMouseEnter(EventArgs e)
  {
   return false;
  }
  public bool OnMouseMove(System.Windows.Forms.MouseEventArgs e)
  {
   if(e.X > AbsoluteLocation.X + ClientSize.Width || e.X < AbsoluteLocation.X || e.Y < AbsoluteLocation.Y || e.Y > AbsoluteLocation.Y + ClientSize.Height)
    return false;
   if(isScrolling)
   {
    int deltaY = m_LastMousePosition.Y - e.Y;
    m_LastMousePosition = new System.Drawing.Point(e.X, e.Y);
    int totalHeight = getNodesHeight();
    double percent = (double)ClientSize.Height / totalHeight;
    int scrollBarHeight = (int)(ClientSize.Height * percent);
    relativeScrollBarPosition -= deltaY;
    if(relativeScrollBarPosition < 0)
    {
     relativeScrollBarPosition = 0;
    }
    else if(relativeScrollBarPosition > ClientSize.Height - scrollBarHeight)
    {
     relativeScrollBarPosition = ClientSize.Height - scrollBarHeight;
    }
    return true;
   }
   if(Math.Abs(e.X - AbsoluteLocation.X + ClientSize.Width ) < 5 )
   {
    DrawArgs.MouseCursor = CursorType.SizeWE;
    return true;
   }
   if(e.X > AbsoluteLocation.X + ClientSize.Width)
    return true;
   foreach(TreeNode tn in m_Nodes)
    tn.OnMouseMove(e);
   return true;
  }
  public bool OnMouseLeave(EventArgs e)
  {
   return false;
  }
  public bool OnMouseWheel(System.Windows.Forms.MouseEventArgs e)
  {
   return false;
  }
 }
 public class LayerManagerTreeNode : TreeNode
 {
  public LayerManagerTreeNode(string text) : base(text)
  {
   this.m_ContextMenu = new System.Windows.Forms.ContextMenu();
   m_ContextMenu.MenuItems.Add("dummy");
  }
 }
 public class TreeNode
 {
  bool m_IsExpanded = false;
  int m_NodeSpacing = 0;
  int m_NodeIndent = 15;
  System.Drawing.Color m_ForeColor = System.Drawing.Color.White;
  System.Drawing.Point m_Location = new System.Drawing.Point(0,0);
  int m_Width = 100;
  string m_Text = "";
  Font m_NodeFont = null;
  object m_Tag = null;
  System.Collections.ArrayList m_Children = new System.Collections.ArrayList();
  public TreeNode(string text)
  {
   m_Text = text;
  }
  public TreeNode(string text, TreeNode[] children)
  {
   m_Text = text;
   foreach(TreeNode tn in children)
   {
    m_Children.Add(tn);
   }
  }
  public void OnMouseDown(System.Windows.Forms.MouseEventArgs e)
  {
  }
  protected System.Windows.Forms.ContextMenu m_ContextMenu = new System.Windows.Forms.ContextMenu();
  public void OnMouseUp(System.Windows.Forms.MouseEventArgs e)
  {
   if(e.X >= Location.X && e.X <= Location.X + 15 &&
    e.Y >= Location.Y && e.Y <= Location.Y + 20 &&
    m_Children.Count > 0)
   {
    m_IsExpanded = !m_IsExpanded;
   }
   else if(DrawArgs.LastMousePosition.X >= m_Location.X &&
    DrawArgs.LastMousePosition.X <= m_Location.X + m_Width &&
    DrawArgs.LastMousePosition.Y >= m_Location.Y &&
    DrawArgs.LastMousePosition.Y <= m_Location.Y + 20)
   {
    m_ContextMenu.Show(DrawArgs.ParentControl, new System.Drawing.Point(e.X, e.Y));
   }
   if(m_Children != null && m_Children.Count > 0)
   {
    foreach(TreeNode childNode in m_Children)
    {
     childNode.OnMouseUp(e);
    }
   }
  }
  public void OnMouseMove(System.Windows.Forms.MouseEventArgs e)
  {
  }
  public string Text
  {
   get
   {
    return m_Text;
   }
   set
   {
    m_Text = value;
   }
  }
  public object Tag
  {
   get
   {
    return m_Tag;
   }
   set
   {
    m_Tag = value;
   }
  }
  public System.Drawing.Point Location
  {
   get
   {
    return new System.Drawing.Point(m_Location.X, m_Location.Y + this.nodeScrollOffset);
   }
   set
   {
    m_Location = value;
   }
  }
  public System.Collections.ArrayList Children
  {
   get
   {
    return m_Children;
   }
   set
   {
    m_Children = value;
   }
  }
  public int Width
  {
   get
   {
    return m_Width;
   }
   set
   {
    m_Width = value;
   }
  }
  public int Height
  {
   get
   {
    Font displayFont = null;
    if(m_NodeFont == null)
     return 10;
    else
     displayFont = m_NodeFont;
    System.Drawing.Rectangle rect = displayFont.MeasureString(null, m_Text, DrawTextFormat.None, m_ForeColor);
    int currentHeight = rect.Height + m_NodeSpacing;
    if(m_IsExpanded && m_Children != null && m_Children.Count > 0)
    {
     for(int nodeIndex = 0; nodeIndex < m_Children.Count; nodeIndex++)
     {
      TreeNode currentNode = m_Children[nodeIndex] as TreeNode;
      currentHeight += currentNode.Height + m_NodeSpacing;
     }
    }
    return currentHeight;
   }
  }
  private int nodeScrollOffset = 0;
  public void Render(DrawArgs drawArgs, int scrollPos, int top, int renderHeight, bool isScrolling)
  {
   Font displayFont = null;
   if(m_NodeFont == null)
   {
    displayFont = drawArgs.defaultDrawingFont;
    m_NodeFont = drawArgs.defaultDrawingFont;
   }
   else
    displayFont = m_NodeFont;
   System.Drawing.Rectangle displayRect = displayFont.MeasureString(null, m_Text, DrawTextFormat.None, m_ForeColor);
   if(m_Location.Y + scrollPos >= top &&
    renderHeight - scrollPos > 0)
   {
    bool renderHighlight = false;
    if(!isScrolling && DrawArgs.LastMousePosition.X >= m_Location.X &&
     DrawArgs.LastMousePosition.X <= m_Location.X + m_Width &&
     DrawArgs.LastMousePosition.Y >= m_Location.Y + scrollPos&&
     DrawArgs.LastMousePosition.Y <= m_Location.Y + scrollPos + displayRect.Height)
    {
     renderHighlight = true;
    }
    string prefix = "";
    if(m_Children.Count > 0)
    {
     if(!m_IsExpanded)
     {
      prefix = "+";
     }
     else
     {
      prefix = "-";
     }
    }
    if(renderHighlight)
    {
     Widgets.Utilities.DrawBox(m_Location.X, m_Location.Y + scrollPos, m_Width,
      (renderHeight - scrollPos > displayRect.Height ? displayRect.Height : renderHeight - scrollPos),
      0.0f,
      System.Drawing.Color.FromArgb(150, 255, 255, 255).ToArgb(),
      drawArgs.device);
    }
    displayFont.DrawText(
     null,
     prefix + m_Text,
     new System.Drawing.Rectangle(m_Location.X, m_Location.Y + scrollPos, m_Width, renderHeight - scrollPos),
     DrawTextFormat.None,
     m_ForeColor);
    nodeScrollOffset = scrollPos;
   }
   if(m_IsExpanded)
   {
    int currentHeight = displayRect.Height + m_NodeSpacing;
    for(int nodeIndex = 0; nodeIndex < m_Children.Count; nodeIndex++)
    {
     TreeNode currentNode = m_Children[nodeIndex] as TreeNode;
     currentNode.Location = new System.Drawing.Point(m_Location.X + m_NodeIndent, m_Location.Y + currentHeight);
     currentNode.Width = m_Width - m_NodeIndent;
     currentNode.Render(drawArgs, scrollPos, top, renderHeight - currentHeight, isScrolling);
     currentHeight += currentNode.Height + m_NodeSpacing;
    }
   }
  }
 }
}
