

package net.sourceforge.squirrel_sql.fw.gui.debug;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;



public class DebugEventListener implements AWTEventListener
{
	public void setEnabled(boolean enable) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		if (enable) {
			
			kit.addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK);
			
			
			ToolTipManager.sharedInstance().setDismissDelay(10000);			
		} else {
			kit.removeAWTEventListener(this);
		}
	}
	
	public void eventDispatched(AWTEvent event)
	{
		Object o = event.getSource();
		if (o instanceof JComponent && o != null)
		{
			JComponent source = (JComponent) o;
			switch (event.getID())
			{
			case MouseEvent.MOUSE_DRAGGED:
				printDebugInfo(source, event);
				break;
			case MouseEvent.MOUSE_ENTERED:
				printDebugInfo(source, event);
				setToolTipText(source, event);
				setBorder(source, event);
				break;
			case MouseEvent.MOUSE_EXITED:
				printDebugInfo(source, event);
				setBorder(source, event);
				break;
			}
		}
	}

	private void setBorder(JComponent source, AWTEvent event) {
		Border border = source.getBorder();
		switch (event.getID())
		{
		case MouseEvent.MOUSE_ENTERED:
			if (border != null)
			{
				source.setBorder(new DebugBorder(border));
			}
			break;			
		case MouseEvent.MOUSE_EXITED:
			if (border != null && border instanceof DebugBorder)
			{
				source.setBorder(((DebugBorder) border).getDelegate());
			}
			break;			
		}
	}
	
	private void setToolTipText(JComponent source, AWTEvent event) {
		
		Container parent = source.getParent();
		String sourceName = source.getName();
		String sourceClassName = source.getClass().toString();
		String parentClassName = parent == null ? null : parent.getClass().toString();
		
		StringBuilder toolTipText = new StringBuilder(getEventMessagePrefix(event));
		
		if (source instanceof AbstractButton) { 
			toolTipText.append("Button with parentClass=");
			toolTipText.append(parentClassName);
		} else {
			if (!StringUtilities.isEmpty(sourceName)) {
				toolTipText.append(sourceName);
			} else if (!StringUtilities.isEmpty(sourceClassName)) {
				toolTipText.append(sourceClassName);
			} 
		}
		source.setToolTipText(toolTipText.toString());
	}

	private void printDebugInfo(JComponent source, AWTEvent event)
	{
		Container parent = source.getParent();
		String sourceName = source.getName();
		String sourceClassName = source.getClass().toString();			
		String parentName = parent == null ? null : parent.getName();
		String parentClassName = parent == null ? null : parent.getClass().toString();
		
		StringBuilder msg = new StringBuilder(getEventMessagePrefix(event));
		msg.append("\n");
		msg.append("\t sourceName:").append(sourceName).append("\n");
		msg.append("\t sourceClassName:").append(sourceClassName).append("\n");
		msg.append("\t parentName:").append(parentName).append("\n");
		msg.append("\t parentClassName:").append(parentClassName);
		System.out.println(msg.toString());
	}
	
	private String getEventMessagePrefix(AWTEvent event) {
		String result = null;
		switch (event.getID()) {
		case MouseEvent.MOUSE_DRAGGED:
			result = "Mouse dragged: ";
			break;
		case MouseEvent.MOUSE_ENTERED:
			result = "Mouse entered: ";
			break;
		case MouseEvent.MOUSE_EXITED:
			result = "Mouse exited: ";
			break;
		default:
			result = "Unknown EventType: ";
		}
		return result;
	}
}
