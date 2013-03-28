

package net.sourceforge.squirrel_sql.fw.completion;

import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;

import javax.swing.SwingUtilities;
import java.awt.Component;
import javax.swing.JViewport;



public class PopupManager
{
   private JComponent _popupParent = null;


	
	public static final Placement Above = new Placement("Above"); 

	
	public static final Placement Below = new Placement("Below"); 

	
	public static final Placement Largest = new Placement("Largest"); 

	
	public static final Placement AbovePreferred = new Placement("AbovePreferred"); 

	
	public static final Placement BelowPreferred = new Placement("BelowPreferred"); 

   public PopupManager(JComponent popupParent)
   {
      _popupParent = popupParent;
   }

   public void install(
		JComponent popup, Rectangle cursorBounds, Placement placement)
	{

		
		Rectangle bounds = computeBounds(popup, _popupParent,
			cursorBounds, placement);

		if (bounds != null)
		{
			
			bounds = SwingUtilities.convertRectangle(_popupParent, bounds,
				_popupParent.getRootPane().getLayeredPane());
			popup.setBounds(bounds);

		}
		else
		{ 
			popup.setVisible(false);
		}

		
		if (popup != null)
		{
			removeFromRootPane(popup);
		}
		if (popup != null)
		{
			installToRootPane(popup);
		}
	}


   
	private void installToRootPane(JComponent c)
	{
		JRootPane rp = _popupParent.getRootPane();
		if (rp != null)
		{
			rp.getLayeredPane().add(c, JLayeredPane.POPUP_LAYER, 0);
		}
	}

	
	private void removeFromRootPane(JComponent c)
	{
		JRootPane rp = c.getRootPane();
		if (rp != null)
		{
			rp.getLayeredPane().remove(c);
		}
	}

	
	protected static Rectangle computeBounds(JComponent popup,
														  JComponent view, Rectangle cursorBounds, Placement placement)
	{

		Rectangle ret;
		Component viewParent = view.getParent();
		if (viewParent instanceof JViewport)
		{
			Rectangle viewBounds = ((JViewport) viewParent).getViewRect();
			Rectangle translatedCursorBounds = (Rectangle) cursorBounds.clone();
			translatedCursorBounds.translate(-viewBounds.x, -viewBounds.y);

			ret = computeBounds(popup, viewBounds.width, viewBounds.height,
				translatedCursorBounds, placement);

			if (ret != null)
			{ 
				ret.translate(viewBounds.x, viewBounds.y);
			}

		}
		else
		{ 
			ret = computeBounds(popup, view.getWidth(), view.getHeight(),
				cursorBounds, placement);
		}

		return ret;
	}

	
	protected static Rectangle computeBounds(JComponent popup,
														  int viewWidth, int viewHeight, Rectangle cursorBounds, Placement placement)
	{

		if (placement == null)
		{
			throw new NullPointerException("placement cannot be null"); 
		}

		
		int aboveCursorHeight = cursorBounds.y;
		int belowCursorY = cursorBounds.y + cursorBounds.height;
		int belowCursorHeight = viewHeight - belowCursorY;

		
		if (placement == Largest)
		{
			placement = (aboveCursorHeight < belowCursorHeight)
				? Below
				: Above;

		}
		else if (placement == AbovePreferred
			&& aboveCursorHeight > belowCursorHeight 
		)
		{
			placement = Above;

		}
		else if (placement == BelowPreferred
			&& belowCursorHeight > aboveCursorHeight 
		)
		{
			placement = Below;
		}

		Rectangle popupBounds = null;

		while (true)
		{ 
			popup.putClientProperty(Placement.class, placement);

			int height = (placement == Above || placement == AbovePreferred)
				? aboveCursorHeight
				: belowCursorHeight;

			popup.setSize(viewWidth, height);
			popupBounds = popup.getBounds();

			Placement updatedPlacement = (Placement) popup.getClientProperty(Placement.class);

			if (updatedPlacement != placement)
			{ 
				if (placement == AbovePreferred && updatedPlacement == null)
				{
					placement = Below;
					continue;

				}
				else if (placement == BelowPreferred && updatedPlacement == null)
				{
					placement = Above;
					continue;
				}
			}

			if (updatedPlacement == null)
			{
				popupBounds = null;
			}

			break;
		}

		if (popupBounds != null)
		{
			
			popupBounds.x = Math.min(cursorBounds.x, viewWidth - popupBounds.width);

			popupBounds.y = (placement == Above || placement == AbovePreferred)
				? (aboveCursorHeight - popupBounds.height)
				: belowCursorY;
		}

		return popupBounds;
	}


   
	public static final class Placement
	{

		private final String representation;

		private Placement(String representation)
		{
			this.representation = representation;
		}

		public String toString()
		{
			return representation;
		}

	}
}

