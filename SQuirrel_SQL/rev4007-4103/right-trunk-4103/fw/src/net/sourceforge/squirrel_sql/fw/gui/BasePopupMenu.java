package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;






public class BasePopupMenu extends JPopupMenu
{
	
	public void show(MouseEvent evt)
	{

		Point pt = new Point( evt.getX(), evt.getY() );
		show(evt.getComponent(), pt.x, pt.y);
	}
}

