package net.sourceforge.squirrel_sql.plugins.dataimport.prefs;

import java.io.Serializable;


public class DataImportPreferenceBean implements Cloneable, Serializable {
	private static final long serialVersionUID = -2654355894514940588L;
	
	
    private boolean useTruncate = true;
    
    
	public DataImportPreferenceBean() {
		super();
	}

	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage()); 
		}
	}

    
    public void setUseTruncate(boolean useTruncate) {
        this.useTruncate = useTruncate;
    }

    
    public boolean isUseTruncate() {
        return useTruncate;
    }

}

