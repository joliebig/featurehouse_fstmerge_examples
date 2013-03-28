using System;
using System.Collections;
using System.Diagnostics;
using System.Drawing;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.Menu;
using System.Windows.Forms;
namespace jhuapl.util
{
 public class JHU_Icons : WorldWind.Renderable.RenderableObjectList
 {
  protected Sprite m_sprite;
  protected bool m_mouseOver;
  protected bool m_needToInitChildren = true;
  protected ArrayList m_childrenToInit;
  protected JHU_Icon mouseOverIcon;
  public JHU_Icons(string name) : base(name)
  {
   m_mouseOver = true;
   isInitialized = false;
   m_needToInitChildren = true;
   m_childrenToInit = new ArrayList();
  }
  public void AddIcon(JHU_Icon icon)
  {
   this.Add(icon);
  }
  public override void Add(RenderableObject ro)
  {
   m_children.Add(ro);
   m_childrenToInit.Add(ro);
   m_needToInitChildren = true;
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   if(!isOn)
    return;
   if(!isInitialized)
   {
    JHU_Globals.getInstance().WorldWindow.MouseUp += new MouseEventHandler(OnMouseUp);
    m_sprite = new Sprite(drawArgs.device);
    m_needToInitChildren = true;
    m_childrenToInit.Clear();
   }
   InitializeChildren(drawArgs);
   isInitialized = true;
  }
  protected void InitializeChildren(DrawArgs drawArgs)
  {
   if (m_needToInitChildren)
   {
    if (m_childrenToInit.Count != 0)
    {
     foreach(RenderableObject ro in m_childrenToInit)
     {
      if(ro.IsOn)
       ro.Initialize(drawArgs);
     }
     m_childrenToInit.Clear();
    }
    else
    {
     foreach(RenderableObject ro in m_children)
     {
      if(ro.IsOn)
       ro.Initialize(drawArgs);
      continue;
     }
    }
    m_needToInitChildren = false;
   }
  }
  public override void Dispose()
  {
   if (isInitialized)
   {
    m_sprite.Dispose();
    m_sprite = null;
    JHU_Globals.getInstance().WorldWindow.MouseUp -= new MouseEventHandler(OnMouseUp);
    isInitialized = false;
    m_needToInitChildren = true;
    m_childrenToInit.Clear();
   }
   base.Dispose();
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   int closestIconDistanceSquared = int.MaxValue;
   JHU_Icon closestIcon = null;
   foreach(RenderableObject ro in m_children)
   {
    if(!ro.IsOn)
     continue;
    if(!ro.isSelectable)
     continue;
    JHU_Icon icon = ro as JHU_Icon;
    if(icon == null)
    {
     if (ro.PerformSelectionAction(drawArgs))
      return true;
    }
    else
    {
     if(drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
     {
      Vector3 projectedPoint = drawArgs.WorldCamera.Project(icon.Position);
      int dx = DrawArgs.LastMousePosition.X - (int)projectedPoint.X;
      int dy = DrawArgs.LastMousePosition.Y - (int)projectedPoint.Y;
      if( icon.SelectionRectangle.Contains( dx, dy ) )
      {
       int distanceSquared = dx*dx + dy*dy;
       if(distanceSquared < closestIconDistanceSquared)
       {
        closestIconDistanceSquared = distanceSquared;
        closestIcon = icon;
       }
      }
     }
    }
   }
   if (closestIcon != null)
    if (closestIcon.PerformSelectionAction(drawArgs))
     return true;
   return false;
  }
  public bool PerformRMBAction(MouseEventArgs e)
  {
   int closestIconDistanceSquared = int.MaxValue;
   JHU_Icon closestIcon = null;
   DrawArgs drawArgs = JHU_Globals.getInstance().WorldWindow.DrawArgs;
   foreach(RenderableObject ro in m_children)
   {
    if(!ro.IsOn)
     continue;
    if(!ro.isSelectable)
     continue;
    JHU_Icon icon = ro as JHU_Icon;
    if(icon != null)
    {
     if(drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
     {
      Vector3 projectedPoint = drawArgs.WorldCamera.Project(icon.Position);
      int dx = e.X - (int)projectedPoint.X;
      int dy = e.Y - (int)projectedPoint.Y;
      if( icon.SelectionRectangle.Contains( dx, dy ) )
      {
       int distanceSquared = dx*dx + dy*dy;
       if(distanceSquared < closestIconDistanceSquared)
       {
        closestIconDistanceSquared = distanceSquared;
        closestIcon = icon;
       }
      }
     }
    }
   }
   if (closestIcon != null)
   {
    if (closestIcon.PerformRMBAction(e))
    {
     return true;
    }
   }
   return false;
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(!isOn)
    return;
   if(!isInitialized)
    this.Initialize(drawArgs);
   if(m_needToInitChildren)
    this.InitializeChildren(drawArgs);
   int closestIconDistanceSquared = int.MaxValue;
   JHU_Icon closestIcon = null;
   try
   {
    m_sprite.Begin(SpriteFlags.AlphaBlend);
    if (m_mouseOver)
    {
     foreach(RenderableObject ro in m_children)
     {
      if(!ro.IsOn)
       continue;
      JHU_Icon icon = ro as JHU_Icon;
      if(icon==null)
      {
       ro.Render(drawArgs);
      }
      else
      {
       if(drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
       {
                                Vector3 translationVector = new Vector3(
                                (float)(icon.Position.X - drawArgs.WorldCamera.ReferenceCenter.X),
                                (float)(icon.Position.Y - drawArgs.WorldCamera.ReferenceCenter.Y),
                                (float)(icon.Position.Z - drawArgs.WorldCamera.ReferenceCenter.Z));
                                Vector3 projectedPoint = drawArgs.WorldCamera.Project(translationVector);
        int dx = DrawArgs.LastMousePosition.X - (int)projectedPoint.X;
        int dy = DrawArgs.LastMousePosition.Y - (int)projectedPoint.Y;
        if( icon.SelectionRectangle.Contains( dx, dy ) )
        {
         int distanceSquared = dx*dx + dy*dy;
         if(distanceSquared < closestIconDistanceSquared)
         {
          closestIconDistanceSquared = distanceSquared;
          closestIcon = icon;
         }
        }
        if(icon != mouseOverIcon)
         icon.FastRender(drawArgs, m_sprite, projectedPoint);
       }
       icon.UpdateHookForm();
      }
     }
     if(mouseOverIcon != null)
     {
      mouseOverIcon.MouseOverRender(drawArgs, m_sprite, drawArgs.WorldCamera.Project(mouseOverIcon.Position));
     }
     mouseOverIcon = closestIcon;
    }
    else
    {
     foreach(RenderableObject ro in m_children)
     {
      if(!ro.IsOn)
       continue;
      JHU_Icon icon = ro as JHU_Icon;
      if(icon==null)
      {
       ro.Render(drawArgs);
      }
      else
      {
       if(drawArgs.WorldCamera.ViewFrustum.ContainsPoint(icon.Position))
       {
        icon.FastRender(drawArgs, m_sprite, drawArgs.WorldCamera.Project(icon.Position));
       }
       icon.UpdateHookForm();
      }
     }
    }
   }
   catch(Exception ex)
   {
    System.Console.WriteLine(ex.Message.ToString());
   }
   finally
   {
    m_sprite.End();
   }
  }
  public void OnMouseUp(object sender, MouseEventArgs e)
  {
   if (e.Button == MouseButtons.Right)
   {
    this.PerformRMBAction(e);
   }
   return;
  }
  public override void BuildContextMenu(ContextMenu menu)
  {
   MenuItem mouseoverMenuItem = new MenuItem("Disable Mouse Over", new EventHandler(IconMouseOverMenuItem_Click));
   if (!m_mouseOver)
    mouseoverMenuItem.Text = "Enable Mouse Over";
   menu.MenuItems.Add(mouseoverMenuItem);
  }
  void IconMouseOverMenuItem_Click(object sender, EventArgs s)
  {
   m_mouseOver = ! m_mouseOver;
  }
 }
}
