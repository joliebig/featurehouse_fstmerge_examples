
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;


public class FirebirdManagerGrantDbObject {
	private String name;
	private String owner;
	private String description;
	
	public FirebirdManagerGrantDbObject(String name,
			String owner, String description) {
		this.name = name;
		this.owner = owner;
		this.description = description;
	}

	
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public String getOwner() {
		return owner;
	}

	
	public void setOwner(String owner) {
		this.owner = owner;
	}

	
	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
