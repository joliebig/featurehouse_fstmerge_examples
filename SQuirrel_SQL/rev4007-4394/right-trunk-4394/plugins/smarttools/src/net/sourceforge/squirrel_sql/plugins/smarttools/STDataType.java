
package net.sourceforge.squirrel_sql.plugins.smarttools;

public class STDataType {
	public static int USE_WHOLE_GROUP = -1;
	public static int GROUP_NULL = 0;
	public static int GROUP_INT = 1;
	public static int GROUP_NUMERIC = 2;
	public static int GROUP_CHAR = 3;
	public static int GROUP_DATE = 4;
	
	private int jdbcType;
	private String jdbcTypeName;
	private int group;
	
	public STDataType(int jdbcType, String jdbcTypeName, int group) {
		this.jdbcType = jdbcType;
		this.jdbcTypeName = jdbcTypeName;
		this.setGroup(group);
	}
	
	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}
	public int getJdbcType() {
		return jdbcType;
	}
	
	public void setJdbcTypeName(String jdbcTypeName) {
		this.jdbcTypeName = jdbcTypeName;
	}
	public String getJdbcTypeName() {
		return jdbcTypeName;
	}
	
	public void setGroup(int group) {
		this.group = group;
	}
	public int getGroup() {
		return group;
	}

	@Override
	public String toString() {
		return getJdbcTypeName();
	}
	
}
