package net.sourceforge.squirrel_sql.plugins.favs;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public final class FolderBeanInfo extends SimpleBeanInfo {


	
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors() {		try
		{
			PropertyDescriptor[] s_descriptors = new PropertyDescriptor[3];
			s_descriptors[0] = new PropertyDescriptor(Folder.IPropertyNames.ID, Folder.class, "getIdentifier", "setIdentifier");
			s_descriptors[1] = new PropertyDescriptor(Folder.IPropertyNames.NAME, Folder.class, "getName", "setName");
			s_descriptors[2] = new IndexedPropertyDescriptor(Folder.IPropertyNames.SUB_FOLDERS, Folder.class, "getSubFolders", "setSubFolders", "getSubFolder", "setSubFolder");

			return s_descriptors;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
