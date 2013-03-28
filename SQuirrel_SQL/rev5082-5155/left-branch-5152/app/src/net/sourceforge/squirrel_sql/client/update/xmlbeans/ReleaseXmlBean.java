
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class ReleaseXmlBean implements Serializable
{

	private static final long serialVersionUID = -7311033877370497900L;

	
	private String name;

	
	private String version;

	private Date createTime;

	private Date lastModifiedTime;

	
	Set<ModuleXmlBean> modules = new HashSet<ModuleXmlBean>();

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'_'HH:mm:ss.SSSZ");

	public ReleaseXmlBean()
	{
		createTime = new Date();
		lastModifiedTime = createTime;
	}

	public ReleaseXmlBean(String name, String version)
	{
		this();
		this.name = name;
		this.version = version;
	}

	
	public String getName()
	{
		return name;
	}

	
	public void setName(String name)
	{
		this.name = name;
		lastModifiedTime = new Date();
	}

	
	public String getVersion()
	{
		return version;
	}

	
	public void setVersion(String version)
	{
		this.version = version;
		lastModifiedTime = new Date();
	}

	
	public Set<ModuleXmlBean> getModules()
	{
		return modules;
	}

	
	public void setModules(Set<ModuleXmlBean> modules)
	{
		this.modules = modules;
		lastModifiedTime = new Date();
	}

	
	public void addmodule(ModuleXmlBean module)
	{
		if (module == null) { return; }
		this.modules.add(module);
		lastModifiedTime = new Date();
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ReleaseXmlBean other = (ReleaseXmlBean) obj;
		if (name == null)
		{
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		if (version == null)
		{
			if (other.version != null) return false;
		}
		else if (!version.equals(other.version)) return false;
		return true;
	}

	
	public String getCreateTime()
	{

		return dateFormat.format(createTime);
	}

	
	public void setCreateTime(Date createTime)
	{
		if (createTime == null) {
			return;
		}
		this.createTime = createTime;
	}

	
	public String getLastModifiedTime()
	{
		return dateFormat.format(lastModifiedTime);
	}

	
	public void setLastModifiedTime(Date lastModifiedTime)
	{
		if (lastModifiedTime == null) { return; }
		this.lastModifiedTime = lastModifiedTime;
	}

	
	@Override
	public String toString()
	{
		final String TAB = "    ";

		StringBuilder retValue = new StringBuilder();

		retValue.append("ReleaseXmlBean ( ")
			.append(super.toString())
			.append(TAB)
			.append("name = ")
			.append(this.name)
			.append(TAB)
			.append("version = ")
			.append(this.version)
			.append(TAB)
			.append("createTime = ")
			.append(this.createTime)
			.append(TAB)
			.append("lastModifiedTime = ")
			.append(this.lastModifiedTime)
			.append(TAB)
			.append("modules = ")
			.append(this.modules)
			.append(TAB)
			.append("dateFormat = ")
			.append(this.dateFormat)
			.append(TAB)
			.append(" )");

		return retValue.toString();
	}

}
