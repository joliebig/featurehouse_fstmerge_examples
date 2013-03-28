package net.sourceforge.squirrel_sql.fw.gui;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class WindowStateBeanInfo extends SimpleBeanInfo
{

	
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] s_dscrs = new PropertyDescriptor[3];
			s_dscrs[0] = new PropertyDescriptor(WindowState.IPropertyNames.BOUNDS,
												WindowState.class,
												"getBounds", "setBounds");
			s_dscrs[1] = new PropertyDescriptor(WindowState.IPropertyNames.VISIBLE,
												WindowState.class,
												"isVisible", "setVisible");
			s_dscrs[2] = new PropertyDescriptor(WindowState.IPropertyNames.FRAME_EXTENDED_STATE,
					WindowState.class,
					"getFrameExtendedState", "setFrameExtendedState");
			
			return s_dscrs;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
