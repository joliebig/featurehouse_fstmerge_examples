using System;
using System.Drawing;
using System.Collections;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Renderable;
namespace jhuapl.util
{
 public class JHU_SimpleTreeNodeWidget : JHU_TreeNodeWidget
 {
  public JHU_SimpleTreeNodeWidget() :base()
  {
  }
  public JHU_SimpleTreeNodeWidget(string name) : base(name)
  {
  }
  public override int Render(DrawArgs drawArgs, int xOffset, int yOffset)
  {
   m_ConsumedSize.Height = 0;
   if (m_visible)
   {
    if (!m_isInitialized)
     this.Initialize(drawArgs);
    m_ConsumedSize.Height = NODE_HEIGHT;
    m_location.Y = yOffset;
    m_xOffset = xOffset;
    int color = this.Enabled ? m_itemOnColor : m_itemOffColor;
    Rectangle bounds = new Rectangle(this.AbsoluteLocation, new System.Drawing.Size(this.ClientSize.Width, NODE_HEIGHT));
    if (m_isMouseOver)
    {
     if (!Enabled)
      color = m_mouseOverOffColor;
     JHU_Utilities.DrawBox(
      bounds.X,
      bounds.Y,
      bounds.Width,
      bounds.Height,
      0.0f,
      m_mouseOverColor,
      drawArgs.device);
    }
    bounds.X = this.AbsoluteLocation.X + xOffset;
    bounds.Width = NODE_ARROW_SIZE;
    if (m_subNodes.Count > 0)
    {
     m_worldwinddingsFont.DrawText(
      null,
      (this.m_isExpanded ? "L" : "A"),
      bounds,
      DrawTextFormat.None,
      color);
    }
    bounds.Width = NODE_CHECKBOX_SIZE;
    bounds.X += NODE_ARROW_SIZE;
    string checkSymbol;
    if (m_isRadioButton)
    {
     checkSymbol = this.IsChecked ? "O" : "P";
    }
    else
    {
     checkSymbol = this.IsChecked ? "N" : "F";
    }
    m_worldwinddingsFont.DrawText(
     null,
     checkSymbol,
     bounds,
     DrawTextFormat.NoClip,
     color);
    Rectangle stringBounds = drawArgs.defaultDrawingFont.MeasureString(null, Name, DrawTextFormat.NoClip, 0);
    m_size.Width = NODE_ARROW_SIZE + NODE_CHECKBOX_SIZE + 5 + stringBounds.Width;
    m_ConsumedSize.Width = m_size.Width;
    bounds.Y += 2;
    bounds.X += NODE_CHECKBOX_SIZE + 5;
    bounds.Width = stringBounds.Width;
    drawArgs.defaultDrawingFont.DrawText(
     null,
     Name,
     bounds,
     DrawTextFormat.None,
     color);
    if (m_isExpanded)
    {
     int newXOffset = xOffset + NODE_INDENT;
     for (int i = 0; i < m_subNodes.Count; i++)
     {
      if (m_subNodes[i] is JHU_TreeNodeWidget)
      {
       m_ConsumedSize.Height += ((JHU_TreeNodeWidget)m_subNodes[i]).Render(drawArgs, newXOffset, m_ConsumedSize.Height);
      }
      else
      {
       System.Drawing.Point newLocation = m_subNodes[i].Location;
       newLocation.Y = m_ConsumedSize.Height;
       newLocation.X = newXOffset;
       m_ConsumedSize.Height += m_subNodes[i].WidgetSize.Height;
       m_subNodes[i].Location = newLocation;
       m_subNodes[i].Render(drawArgs);
      }
      if (m_subNodes[i].WidgetSize.Width + newXOffset > m_ConsumedSize.Width)
      {
       m_ConsumedSize.Width = m_subNodes[i].WidgetSize.Width + newXOffset ;
      }
     }
    }
   }
   return m_ConsumedSize.Height;
  }
 }
}
