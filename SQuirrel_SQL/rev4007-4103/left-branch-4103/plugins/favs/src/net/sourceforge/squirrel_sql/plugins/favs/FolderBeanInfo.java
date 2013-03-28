package net.sourceforge.squirrel_sql.plugins.favs;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public final class FolderBeanInfo extends SimpleBeanInfo {

	private static PropertyDescriptor[] s_descriptors;

	public FolderBeanInfo() throws IntrospectionException {
		super();
		if (s_descriptors == null) {
			s_descriptors = new PropertyDescriptor[3];
			s_descriptors[0] = new PropertyDescriptor(Folder.IPropertyNames.ID, Folder.class, "getIdentifier", "setIdentifier");
			s_descriptors[1] = new PropertyDescriptor(Folder.IPropertyNames.NAME, Folder.class, "getName", "setName");
			s_descriptors[2] = new IndexedPropertyDescriptor(Folder.IPropertyNames.SUB_FOLDERS, Folder.class, "getSubFolders", "setSubFolders", "getSubFolder", "setSubFolder");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors() {		return s_descriptors;
	}
}
