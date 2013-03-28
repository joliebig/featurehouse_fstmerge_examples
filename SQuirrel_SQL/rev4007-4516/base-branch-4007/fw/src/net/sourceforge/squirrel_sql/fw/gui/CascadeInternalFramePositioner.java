package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

public class CascadeInternalFramePositioner implements IInternalFramePositioner
{
	private int _x = INITIAL_POS;
	private int _y = INITIAL_POS;

	private static final int MOVE = 20;
	private static final int INITIAL_POS = 5;

	public CascadeInternalFramePositioner()
	{
		super();
	}

	public void positionInternalFrame(JInternalFrame child)
	{
		if (child == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}

      boolean toInitialPos = false;

		if (!child.isClosed())
		{
			if (child.getParent() != null)
			{
            Dimension childSize = child.getSize();

            if(0 == childSize.width || 0 == childSize.height)
            {
               toInitialPos = true;
            }
            else
            {
               Rectangle parentBounds = child.getParent().getBounds();
               if (_x + MOVE  + childSize.width >= parentBounds.width)
               {
                  _x = INITIAL_POS;
               }
               if (_y + MOVE + childSize.height >= parentBounds.height)
               {
                  _y = INITIAL_POS;
               }
            }
			}
			if (child.isIcon())
			{
				try
				{
					child.setIcon(false);
				}
				catch (PropertyVetoException ignore)
				{
					
				}
			}
			else if (child.isMaximum())
			{
				try
				{
					child.setMaximum(false);
				}
				catch (PropertyVetoException ignore)
				{
					
				}
			}

         if(toInitialPos)
         {
            child.setBounds(INITIAL_POS, INITIAL_POS, child.getWidth(), child.getHeight());
         }
         else
         {
            child.setBounds(_x, _y, child.getWidth(), child.getHeight());
            _x += MOVE;
            _y += MOVE;
         }
		}
	}
}
