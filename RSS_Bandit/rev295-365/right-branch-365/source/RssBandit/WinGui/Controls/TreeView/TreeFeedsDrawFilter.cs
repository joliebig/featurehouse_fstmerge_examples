using System;
using System.Drawing;
using Infragistics.Win;
using Infragistics.Win.UltraWinTree;
using RssBandit.WinGui.Controls;
using RssBandit.WinGui.Utility;
using ExpansionIndicatorUIElement = Infragistics.Win.UltraWinTree.ExpansionIndicatorUIElement;
namespace RssBandit.WinGui
{
 public class TreeFeedsDrawFilter : IUIElementDrawFilter
 {
  public TreeFeedsDrawFilter() {}
  DrawPhase IUIElementDrawFilter.GetPhasesToFilter( ref UIElementDrawParams drawParams )
  {
   UltraTreeNode treeNode = drawParams.Element.GetContext( typeof(UltraTreeNode), true ) as UltraTreeNode;
   if (treeNode != null) {
    if (treeNode.Level == 0) {
     if(drawParams.Element is EditorWithTextDisplayTextUIElement )
      return DrawPhase.BeforeDrawForeground;
     if(drawParams.Element is PreNodeAreaUIElement )
      return DrawPhase.BeforeDrawForeground;
     if(drawParams.Element is EditorWithTextUIElement )
      return DrawPhase.BeforeDrawForeground;
     if(drawParams.Element is NodeSelectableAreaUIElement )
      return DrawPhase.BeforeDrawForeground | DrawPhase.AfterDrawElement;
    } else if (treeNode.Level > 0) {
     if(drawParams.Element is NodeSelectableAreaUIElement )
      return DrawPhase.AfterDrawElement;
    }
   }
   return DrawPhase.None;
  }
  bool IUIElementDrawFilter.DrawElement( DrawPhase drawPhase, ref UIElementDrawParams drawParams )
  {
   UltraTreeNode treeNode = drawParams.Element.GetContext( typeof(UltraTreeNode), true ) as UltraTreeNode;
   if ( treeNode != null )
   {
     TreeFeedsNodeBase feedsNode = treeNode as TreeFeedsNodeBase;
     if (drawPhase == DrawPhase.AfterDrawElement &&
      feedsNode != null && feedsNode.Control != null)
     {
      int unread = feedsNode.UnreadCount;
      if (unread > 0)
      {
       int clickableAreaExtenderImageWidth = feedsNode.Control.RightImagesSize.Width;
       string st = String.Format("({0})", unread);
       UIElement uiElement = drawParams.Element;
       Rectangle ur = uiElement.Rect;
       using (Brush unreadColorBrush = new SolidBrush(FontColorHelper.UnreadCounterColor)) {
        drawParams.Graphics.DrawString(st, FontColorHelper.UnreadCounterFont,
         unreadColorBrush, ur.X + ur.Width - clickableAreaExtenderImageWidth,
         ur.Y, StringFormat.GenericDefault);
       }
       return true;
      }
      return false;
     }
    if(treeNode.Level==0)
    {
     RectangleF initialRect = drawParams.Element.RectInsideBorders;
     Rectangle r = new Rectangle((int)initialRect.Left,(int)initialRect.Top,(int)initialRect.Width,(int)initialRect.Height);
     r.Width = treeNode.Control.DisplayRectangle.Width+300;
     r.Height = treeNode.ItemHeightResolved;
     if(drawParams.Element is EditorWithTextDisplayTextUIElement )
     {
      return false;
     }
     if(drawParams.Element is PreNodeAreaUIElement)
     {
      r.Width++;
     }
     if(drawParams.Element is EditorWithTextUIElement)
     {
      r.Y--;
      r.X--;
      r.Width++;
     }
     if(drawParams.Element is NodeSelectableAreaUIElement)
     {
      r.Width++;
     }
     TreeFeedsNodeGroupHeaderPainter.PaintOutlook2003Header( drawParams.Graphics, r);
     return true;
    }
   }
   return false;
  }
 }
}
