
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;

public class FirebirdManagerPrivilege {
    private boolean select = false;
    private boolean insert = false;
    private boolean update = false;
    private boolean delete = false;
    private boolean reference = false;
    private boolean execute = false;
    private boolean member = false;
    
    
    
    
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isExecute() {
		return execute;
	}
	public void setExecute(boolean execute) {
		this.execute = execute;
	}
	public boolean isInsert() {
		return insert;
	}
	public void setInsert(boolean insert) {
		this.insert = insert;
	}
	public boolean isMember() {
		return member;
	}
	public void setMember(boolean member) {
		this.member = member;
	}
	public boolean isReference() {
		return reference;
	}
	public void setReference(boolean reference) {
		this.reference = reference;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public boolean isUpdate() {
		return update;
	}
	public void setUpdate(boolean update) {
		this.update = update;
	}
    
} 
