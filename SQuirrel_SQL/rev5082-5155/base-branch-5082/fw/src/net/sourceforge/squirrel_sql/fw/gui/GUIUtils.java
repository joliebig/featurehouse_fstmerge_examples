package net.sourceforge.squirrel_sql.fw.gui;


import net.sourceforge.squirrel_sql.fw.util.BaseRuntimeException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GUIUtils
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(GUIUtils.class);


   private static JFrame _mainFrame;

   
   public static void centerWithinParent(Window wind)
   {
      if (wind == null)
      {
         throw new IllegalArgumentException("null Window passed");
      }
      final Container parent = wind.getParent();
      if (parent != null && parent.isVisible())
      {
         center(wind, new Rectangle(parent.getLocationOnScreen(),
               parent.getSize()));
      }
      else
      {
         centerWithinScreen(wind);
      }
   }

	
	public static void centerWithinDesktop(JInternalFrame frame)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}
		final Container parent = frame.getDesktopPane();
		if (parent != null && parent.isVisible())
		{
			center(frame, new Rectangle(new Point(0, 0), parent.getSize()));
		}
	}

	
	public static void centerWithinScreen(Window wind)
	{
		if (wind == null)
		{
			throw new IllegalArgumentException("null Window passed");
		}
		final Toolkit toolKit = Toolkit.getDefaultToolkit();
		final Rectangle rcScreen = new Rectangle(toolKit.getScreenSize());
		final Dimension windSize = wind.getSize();
		final Dimension parentSize = new Dimension(rcScreen.width, rcScreen.height);
		if (windSize.height > parentSize.height)
		{
			windSize.height = parentSize.height;
		}
		if (windSize.width > parentSize.width)
		{
			windSize.width = parentSize.width;
		}
		center(wind, rcScreen);
	}

	public static void moveToFront(final JInternalFrame fr)
	{
		if (fr != null)
		{
			processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					fr.moveToFront();
					fr.setVisible(true);
					try
					{
						fr.setSelected(true);
                  if(fr.isIcon())
                  {
                     fr.setIcon(false);
                  }
                  fr.setSelected(true);
					}
					catch (PropertyVetoException ex)
					{
						s_log.error("Error bringing internal frame to the front", ex);
					}
               fr.requestFocus();
				}
			});
		}
	}

	
	public static Frame getOwningFrame(Component comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("null Component passed");
		}

		if (comp instanceof Frame)
		{
			return (Frame) comp;
		}
		return getOwningFrame(SwingUtilities.windowForComponent(comp));
	}

	
	public static boolean isToolWindow(JInternalFrame frame)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}

		final Object obj = frame.getClientProperty("JInternalFrame.isPalette");
		return obj != null && obj == Boolean.TRUE;
	}

	
	public static void makeToolWindow(JInternalFrame frame, boolean isToolWindow)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}
		frame.putClientProperty("JInternalFrame.isPalette",
								isToolWindow ? Boolean.TRUE : Boolean.FALSE);
	}

	
	public static void setJButtonSizesTheSame(JButton[] btns)
	{
		if (btns == null)
		{
			throw new IllegalArgumentException("null JButton[] passed");
		}

		
		final Dimension maxSize = new Dimension(0, 0);
		for (int i = 0; i < btns.length; ++i)
		{
			final JButton btn = btns[i];
			final FontMetrics fm = btn.getFontMetrics(btn.getFont());
			Rectangle2D bounds = fm.getStringBounds(btn.getText(), btn.getGraphics());
			int boundsHeight = (int) bounds.getHeight();
			int boundsWidth = (int) bounds.getWidth();
			maxSize.width = boundsWidth > maxSize.width ? boundsWidth : maxSize.width;
			maxSize.height = boundsHeight > maxSize.height ? boundsHeight : maxSize.height;
		}

		Insets insets = btns[0].getInsets();
		maxSize.width += insets.left + insets.right;
		maxSize.height += insets.top + insets.bottom;

		for (int i = 0; i < btns.length; ++i)
		{
			JButton btn = btns[i];
			btn.setPreferredSize(maxSize);
		}
	}

   public static boolean isWithinParent(Component wind)
	{
		if (wind == null)
		{
			throw new IllegalArgumentException("Null Component passed");
		}

		Rectangle windowBounds = wind.getBounds();
		Component parent = wind.getParent();
		Rectangle parentRect = null;
		if (parent != null)
		{
			parentRect = new Rectangle(parent.getSize());
		}
		else
		{
			
			parentRect = getScreenBoundsFor(windowBounds);
		}
		
		

			
			
		
			
		
		if (windowBounds.x < (parentRect.x - 20)
				|| windowBounds.y < (parentRect.y - 20))
		{
			return false;
		}
		return true;
	}

	public static Rectangle getScreenBoundsFor(Rectangle rc)
	{
        final GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        final List<GraphicsConfiguration> configs = 
            new ArrayList<GraphicsConfiguration>();

        for (int i = 0; i < gds.length; i++)
        {
            GraphicsConfiguration gc = gds[i].getDefaultConfiguration();
            if (rc.intersects(gc.getBounds()))
            {
            	configs.add(gc);
            }
        }
        
        GraphicsConfiguration selected = null;
        if (configs.size() > 0)
        {
            for (Iterator<GraphicsConfiguration> it = configs.iterator(); it.hasNext();)
            {
            	GraphicsConfiguration gcc = it.next();
                if (selected == null)
                    selected = gcc;
                else
                {
                    if (gcc.getBounds().contains(rc.x + 20, rc.y + 20))
                    {
                    	selected = gcc;
                    	break;
                    }
                }
            }
        }
        else
        {
            selected = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }

        int x = selected.getBounds().x;
        int y = selected.getBounds().y;
        int w = selected.getBounds().width;
        int h = selected.getBounds().height;
        
        return new Rectangle(x,y,w,h); 
	}
	
	public static void processOnSwingEventThread(Runnable todo)
	{
		processOnSwingEventThread(todo, false);
	}

	public static void processOnSwingEventThread(Runnable todo, boolean wait)
	{
		if (todo == null)
		{
			throw new IllegalArgumentException("Runnable == null");
		}

		if (wait)
		{
			if (SwingUtilities.isEventDispatchThread())
			{
				todo.run();
			}
			else
			{
				try
				{
					SwingUtilities.invokeAndWait(todo);
				}
				catch (InvocationTargetException ex)
				{
					throw new BaseRuntimeException(ex);
				}
				catch (InterruptedException ex)
				{
					throw new BaseRuntimeException(ex);
				}
			}
		}
		else
		{
            if (SwingUtilities.isEventDispatchThread()) {
                todo.run();
            } else {
                SwingUtilities.invokeLater(todo);
            }
		}
	}

	
	private static void center(Component wind, Rectangle rect)
	{
		if (wind == null || rect == null)
		{
			throw new IllegalArgumentException("null Window or Rectangle passed");
		}
		Dimension windSize = wind.getSize();
		int x = ((rect.width - windSize.width) / 2) + rect.x;
		int y = ((rect.height - windSize.height) / 2) + rect.y;
		if (y < rect.y)
		{
			y = rect.y;
		}
		wind.setLocation(x, y);
	}

   
   public static void setMainFrame(JFrame mainFrame)
   {
      _mainFrame = mainFrame;
   }

   public static JFrame  getMainFrame()
   {
      return _mainFrame;
   }
   
   
   public static String getWrappedLine(String line, int lineLength) {
       if (line.length() <= lineLength) {
           return line;
       }
       StringBuffer result = new StringBuffer();
       char[] lineChars = line.toCharArray();
       int lastBreakCharIdx = -1;
       ArrayList<Integer> breakPoints = new ArrayList<Integer>();
       
       
       for (int i = 0; i < lineChars.length; i++) {
           char curr = lineChars[i];
           if (curr == ' ' || curr == ',') {
               lastBreakCharIdx = i;
           }
           if (i > 0 && (i % lineLength == 0) && lastBreakCharIdx != -1) {
               breakPoints.add(Integer.valueOf(lastBreakCharIdx));
           }
       }
       if (lastBreakCharIdx != lineChars.length) {
           breakPoints.add(Integer.valueOf(lineChars.length));
       }
       int lastBreakPointIdx = 0;
       for (Iterator<Integer> iter = breakPoints.iterator(); iter.hasNext();) {
           int breakPointIdx = (iter.next()).intValue() + 1;
           if (breakPointIdx > line.length()) {
               breakPointIdx = line.length();
           }
           String part = line.substring(lastBreakPointIdx, breakPointIdx);
           result.append(part.trim());
           if (!part.trim().endsWith("\\n")) { 
               result.append("\n");
           }
           lastBreakPointIdx = breakPointIdx;
       }
       return result.toString();
   }
}
