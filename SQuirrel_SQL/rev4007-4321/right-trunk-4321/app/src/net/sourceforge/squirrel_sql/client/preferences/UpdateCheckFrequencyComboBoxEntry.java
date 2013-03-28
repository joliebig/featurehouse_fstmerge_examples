
package net.sourceforge.squirrel_sql.client.preferences;


public class UpdateCheckFrequencyComboBoxEntry {

   
   public enum Frequency {
      AT_STARTUP, WEEKLY
   }
   
   
   private Frequency _frequency = null;

   
   private String _displayName = null;

   
   public UpdateCheckFrequencyComboBoxEntry(Frequency frequency, String displayName) {
      _frequency = frequency;
      _displayName = displayName;
   }

   
   public String toString() {
      return _displayName;
   }

   
   public boolean isStartup() {
      return _frequency == Frequency.AT_STARTUP;
   }

   
   public boolean isWeekly() {
      return _frequency == Frequency.WEEKLY;
   }

}
