
package net.sourceforge.squirrel_sql.client.preferences;


public class UpdateChannelComboBoxEntry {

      
   public enum ChannelType {
      STABLE, SNAPSHOT
   }

   
   private ChannelType _channel = null;

   
   private String _displayName = null;

   
   public UpdateChannelComboBoxEntry(ChannelType channel, String displayName) {
      _channel = channel;
      _displayName = displayName;
   }

   
   public String toString() {
      return _displayName;
   }

   
   
   public boolean isStable() {
      return _channel == ChannelType.STABLE;
   }

      
   public boolean isSnapshot() {
      return _channel == ChannelType.SNAPSHOT;
   }

}
