
package net.sourceforge.squirrel_sql.client.preferences;

import net.sourceforge.squirrel_sql.client.update.UpdateCheckFrequency;


public class UpdateCheckFrequencyComboBoxEntry {
   
   
   private UpdateCheckFrequency _frequency = null;

   
   private String _displayName = null;

   
   public UpdateCheckFrequencyComboBoxEntry(UpdateCheckFrequency frequency, String displayName) {
      _frequency = frequency;
      _displayName = displayName;
   }

   
   public String toString() {
      return _displayName;
   }

   
   public UpdateCheckFrequency getUpdateCheckFrequencyEnum() {
   	return _frequency;
   }
   
}
