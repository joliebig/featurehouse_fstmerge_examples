package net.sourceforge.squirrel_sql.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class ScrollableDesktopPane extends JDesktopPane
{
	
	private MyComponentListener _listener = new MyComponentListener();

	
	public ScrollableDesktopPane()
	{
		super();
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
		}
		super.remove(comp);
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
