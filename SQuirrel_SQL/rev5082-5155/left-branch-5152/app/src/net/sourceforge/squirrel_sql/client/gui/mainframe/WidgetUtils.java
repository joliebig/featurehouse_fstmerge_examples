package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.util.List;
import java.util.ArrayList;

public class WidgetUtils extends GUIUtils
{
   
   public static IWidget[] getOpenToolWindows(IWidget[] frames)
   {
      if (frames == null)
      {
         throw new IllegalArgumentException("null JInternalFrame[] passed");
      }
      List<IWidget> framesList = new ArrayList<IWidget>();
      for (int i = 0; i < frames.length; ++i)
      {
         IWidget fr = frames[i];
         if (fr.isToolWindow() && !fr.isClosed())
         {
            framesList.add(frames[i]);
         }
      }
      return framesList.toArray(new IWidget[framesList.size()]);
   }

   
   public static IWidget[] getOpenNonToolWindows(IWidget[] frames)
   {
      if (frames == null)
      {
         throw new IllegalArgumentException("null JInternalFrame[] passed");
      }
      List<IWidget> framesList = new ArrayList<IWidget>();
      for (int i = 0; i < frames.length; ++i)
      {
         if (!frames[i].isToolWindow() && !frames[i].isClosed())
         {
            framesList.add(frames[i]);
         }
      }
      return framesList.toArray(new IWidget[framesList.size()]);
   }

   
   public static IWidget[] getNonMinimizedNonToolWindows(IWidget[] frames)
   {
      if (frames == null)
      {
         throw new IllegalArgumentException("null JInternalFrame[] passed");
      }
      List<IWidget> framesList = new ArrayList<IWidget>();
      for (int i = 0; i < frames.length; ++i)
      {
         IWidget fr = frames[i];
         if (!fr.isToolWindow() && !fr.isClosed() && !fr.isIcon())
         {
            framesList.add(frames[i]);
         }
      }
      return framesList.toArray(new IWidget[framesList.size()]);
   }
}
