package net.sourceforge.squirrel_sql.client.gui.mainframe;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class MainFrameWindowStateBeanInfo extends SimpleBeanInfo {
	private static final Class<MainFrameWindowState> s_actualClass = 
        MainFrameWindowState.class;

        
	private static volatile PropertyDescriptor[] s_dscrs;

	public MainFrameWindowStateBeanInfo() throws IntrospectionException {
		super();
		if (s_dscrs == null) {
			s_dscrs = new PropertyDescriptor[2];
			s_dscrs[0] = new PropertyDescriptor(MainFrameWindowState.IPropertyNames.ALIASES_WINDOW_STATE, s_actualClass, "getAliasesWindowState", "setAliasesWindowState");
			s_dscrs[1] = new PropertyDescriptor(MainFrameWindowState.IPropertyNames.DRIVERS_WINDOW_STATE, s_actualClass, "getDriversWindowState", "setDriversWindowState");
		}
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		try {
			BeanInfo superBeanInfo = Introspector.getBeanInfo(s_actualClass.getSuperclass());
			return new BeanInfo[] {
				superBeanInfo
			};
		} catch(IntrospectionException ex) {
			return new BeanInfo[0];
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return s_dscrs;
	}
}
