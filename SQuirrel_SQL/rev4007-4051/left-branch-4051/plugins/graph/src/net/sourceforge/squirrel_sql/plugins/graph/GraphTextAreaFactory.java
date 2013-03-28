package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public class GraphTextAreaFactory
{
   private ColumnTextArea _txtColumns;
   private ZoomableColumnTextArea _txtZoomColumns;

   public GraphTextAreaFactory(TableToolTipProvider toolTipProvider, Zoomer zoomer)
   {
      _txtColumns = new ColumnTextArea(toolTipProvider);
      _txtColumns.setEditable(false);
      _txtColumns.setBackground(new Color(255,255,204));

      _txtZoomColumns = new ZoomableColumnTextArea(toolTipProvider, zoomer);
      _txtZoomColumns.setBackground(new Color(255,255,204));

   }


   public JComponent getComponent(boolean zoomEnabled)
   {
      if(zoomEnabled)
      {
         return _txtZoomColumns;
      }
      else
      {
         return _txtColumns;
      }
   }

   
   public JComponent getBestReadyComponent()
   {

      if( getComponent(true).isShowing() ||
          (getComponent(true).isVisible() && null != getComponent(true).getGraphics())  )
      {
         return getComponent(true);
      }
      else if( getComponent(false).isShowing() ||
               (getComponent(false).isVisible() && null != getComponent(false).getGraphics()) )
      {
         return getComponent(false);
      }
      else
      {
         return null;
      }
   }


   public Graphics getGraphics()
   {
      JComponent bestReadyComponent = getBestReadyComponent();
      if(null == bestReadyComponent)
      {
         return null;
      }
      return bestReadyComponent.getGraphics();
   }

   public Font getFont()
   {
      JComponent bestReadyComponent = getBestReadyComponent();
      if(null == bestReadyComponent)
      {
         return null;
      }
      return bestReadyComponent.getGraphics().getFont();
   }

   public void setColumns(ColumnInfo[] columnInfos)
   {
      _txtColumns.setGraphColumns(columnInfos);
      _txtZoomColumns.setGraphColumns(columnInfos);
   }

   public void addMouseListener(MouseListener ml)
   {
      _txtColumns.addMouseListener(ml);
      _txtZoomColumns.addMouseListener(ml);
   }
}
