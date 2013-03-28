package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.CascadeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;

import javax.swing.*;


public class ScrollableDesktopPane extends JDesktopPane implements IDesktopContainer
{

   
   private final ILogger s_log = LoggerController.createLogger(ScrollableDesktopPane.class);


	private MyComponentListener _listener = new MyComponentListener();

   private final IInternalFramePositioner _internalFramePositioner = new CascadeInternalFramePositioner();
   private IApplication _app;


   
	public ScrollableDesktopPane(IApplication app)
	{
		super();
      _app = app;
   }


	protected void paintComponent(Graphics g)
	{
		setPreferredSize(getRequiredSize());
		super.paintComponent(g);
	}

	public void remove(Component comp)
	{
		if (comp != null)
		{
			comp.removeComponentListener(_listener);
         super.remove(comp);
		}
		revalidate();
		repaint();
	}

	protected void addImpl(Component comp, Object constraints, int index)
	{
		if (comp != null)
		{
			comp.addComponentListener(_listener);
			revalidate();
		}
		super.addImpl(comp, constraints, index);
	}

	
	public Dimension getRequiredSize()
	{
		JInternalFrame[] frames = getAllFrames();
		int maxX = 0;
		int maxY = 0;
		for (int i = 0; i < frames.length; ++i)
		{
			if (frames[i].isVisible())
			{
				JInternalFrame frame = frames[i];
				int x = frame.getX() + frame.getWidth();
				if (x > maxX)
				{
					maxX = x;
				}
				int y = frame.getY() + frame.getHeight();
				if (y > maxY)
				{
					maxY = y;
				}
			}
		}
		return new Dimension(maxX, maxY);
	}



   public IWidget[] getAllWidgets()
   {
      JInternalFrame[] jInternalFrames = getAllFrames();
      IWidget[] ret = new IWidget[jInternalFrames.length];
      for (int i = 0; i < jInternalFrames.length; i++)
      {
         ret[i] = ((InternalFrameDelegate)jInternalFrames[i]).getWidget();
      }
      return ret;
   }

   public IWidget getSelectedWidget()
   {
      InternalFrameDelegate d = (InternalFrameDelegate) getSelectedFrame();
      return d.getWidget();
   }


   public JComponent getComponent()
   {
      return this;
   }

   public void addWidget(DialogWidget widget)
   {
      if (null != widget)
      {
         JInternalFrame delegate = (JInternalFrame) widget.getDelegate();
         addInternalFrame(delegate);
      }
   }

   public void addWidget(DockWidget widget)
   {
      JInternalFrame delegate = (JInternalFrame) widget.getDelegate();
      addInternalFrame(delegate);
   }

   public void addWidget(TabWidget widget)
   {
      JInternalFrame delegate = (JInternalFrame) widget.getDelegate();
      addInternalFrame(delegate);
   }

   private void addInternalFrame(JInternalFrame delegate)
   {
      beforeAdd(delegate);
      super.add(delegate);
      afterAdd(delegate);
   }


   private void afterAdd(JInternalFrame child)
   {
      if (!GUIUtils.isToolWindow(child))
      {
         _internalFramePositioner.positionInternalFrame(child);
      }



      
      if (!GUIUtils.isToolWindow(child))
      {
         if (child.isMaximizable() && _app.getSquirrelPreferences().getMaximizeSessionSheetOnOpen())
         {
            try
            {
               child.setMaximum(true);
            }
            catch (PropertyVetoException ex)
            {
               s_log.error("Unable to maximize window", ex);
            }
         }
      }
   }

   private void beforeAdd(JInternalFrame child)
   {
      if (!GUIUtils.isToolWindow(child))
      {
         Dimension cs = getSize();
         
         
         cs.setSize((int) (cs.width * 0.8d), (int) (cs.height * 0.8d));
         child.setSize(cs);
      }
   }




   public void putClientProperty(String key, String value)
   {
      super.putClientProperty(key, value);
   }

   public void setDesktopManager(SquirrelDesktopManager squirrelDesktopManager)
   {
      super.setDesktopManager(new DesktopManagerWrapper(squirrelDesktopManager));
   }

   private final class MyComponentListener implements ComponentListener
	{
		public void componentHidden(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}

		public void componentMoved(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}

		public void componentResized(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}

		public void componentShown(ComponentEvent evt)
		{
			ScrollableDesktopPane.this.revalidate();
		}
	}
}
