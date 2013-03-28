package net.sourceforge.squirrel_sql.fw.gui;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class WindowStateBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_dscrs;

	public WindowStateBeanInfo() throws IntrospectionException
	{
		super();
		if (s_dscrs == null)
		{
			s_dscrs = new PropertyDescriptor[3];
			s_dscrs[0] = new PropertyDescriptor(WindowState.IPropertyNames.BOUNDS,
												WindowState.class,
												"getBounds", "setBounds");
			s_dscrs[1] = new PropertyDescriptor(WindowState.IPropertyNames.VISIBLE,
												WindowState.class,
												"isVisible", "setVisible");
			s_dscrs[2] = new PropertyDescriptor(WindowState.IPropertyNames.FRAME_EXTENDED_STATE,
					WindowState.class,
					"getFrameExtendedState", "setFrameExtendedState");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
