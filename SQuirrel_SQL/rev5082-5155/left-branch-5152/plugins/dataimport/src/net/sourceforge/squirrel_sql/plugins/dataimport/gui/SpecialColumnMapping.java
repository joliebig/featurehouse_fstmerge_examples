package net.sourceforge.squirrel_sql.plugins.dataimport.gui;


import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public enum SpecialColumnMapping {
	
	SKIP,
	
	NULL,
	
	FIXED_VALUE,
	
	AUTO_INCREMENT;
	
	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(SpecialColumnMapping.class);
	
	public String getVisibleString() {
		return stringMgr.getString("SpecialColumnMapping." + this.name());
	}

}
