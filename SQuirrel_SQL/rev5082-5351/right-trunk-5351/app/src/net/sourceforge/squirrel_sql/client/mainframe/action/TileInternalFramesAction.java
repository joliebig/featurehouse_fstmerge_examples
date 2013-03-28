package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.client.gui.mainframe.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.IApplication;


public abstract class TileInternalFramesAction extends BaseAction implements IHasJDesktopPane
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TileInternalFramesAction.class);

	
	private IDesktopContainer _desktop;
   private IApplication _app;

   
	public TileInternalFramesAction(IApplication app)
	{
      super(s_stringMgr.getString("TileInternalFramesAction.title"));
      _app = app;
   }

   
	public void setDesktopContainer(IDesktopContainer value)
	{
		_desktop = value;
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		if (_desktop != null && _app.getDesktopStyle().isInternalFrameStyle())
		{
			IWidget[] widgets = WidgetUtils.getNonMinimizedNonToolWindows(_desktop.getAllWidgets());
			final int cells = widgets.length;
			if (cells > 0)
			{
				final RowColumnCount rcc = getRowColumnCount(cells);
				final int rows = rcc._rowCount;
				final int cols = rcc._columnCount;


				final Dimension desktopSize = _desktop.getSize();
				final int width = desktopSize.width / cols;
				final int height = desktopSize.height / rows;
				int xPos = 0;
				int yPos = 0;

				for (int y = 0; y < rows; ++y)
				{
					for (int x = 0; x < cols; ++x)
					{
						final int idx = y + (x * rows);
						if (idx >= cells)
						{
							break;
						}
						JInternalFrame frame = widgets[idx].getInternalFrame();
						if (!frame.isClosed())
						{
							if (frame.isIcon())
							{
								try
								{
									frame.setIcon(false);
								} catch (PropertyVetoException ignore)
								{
									
								}
							}
							else if (frame.isMaximum())
							{
								try
								{
									frame.setMaximum(false);
								}
								catch (PropertyVetoException ignore)
								{
									
								}
							}

							frame.reshape(xPos, yPos, width, height);
							xPos += width;
						}
					}
					xPos = 0;
					yPos += height;
				}
			}
		}
	}

	
	protected abstract RowColumnCount getRowColumnCount(int internalFrameCount);

   public final static class RowColumnCount
	{
		protected final int _rowCount;
		protected final int _columnCount;

		public RowColumnCount(int rowCount, int columnCount)
		{
			_rowCount = rowCount;
			_columnCount = columnCount;
		}
	}
}
