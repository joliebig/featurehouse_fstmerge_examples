
package net.sourceforge.squirrel_sql.plugins.derby.prefs;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;


public class DerbyPreferenceBean extends BaseQueryTokenizerPreferenceBean implements Cloneable, Serializable {

   static final long serialVersionUID = 5818886723165356478L;
   
   private boolean readClobsFully = true;

   public DerbyPreferenceBean() {
      super();
      statementSeparator = ";";
      procedureSeparator = "/";
      lineComment = "--";
      removeMultiLineComments = false;
      readClobsFully = true;
      installCustomQueryTokenizer = true;
   }

   
   public boolean isReadClobsFully() {
      return readClobsFully;
   }

   
   public void setReadClobsFully(boolean readClobsFully) {
      this.readClobsFully = readClobsFully;
   }

	
	@Override
	protected DerbyPreferenceBean clone()
	{
		return (DerbyPreferenceBean)super.clone();
	}

   
}
