
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

import net.sourceforge.squirrel_sql.fw.util.Utilities;



public class InstallStatusEvent {

	
   String _artifactName;
   
   
   InstallEventType _type;
   
   public InstallStatusEvent(InstallEventType type) {
   	Utilities.checkNull("InstallStatusEvent.init", "type", type);
      this._type = type;
   }

   
   public String getArtifactName() {
      return _artifactName;
   }

   
   public void setArtifactName(String name) {
   	Utilities.checkNull("setArtifactName", "name", name);
      _artifactName = name;
   }

   
   public InstallEventType getType() {
      return _type;
   }

   
   public void setType(InstallEventType type) {
   	Utilities.checkNull("setType", "type", type);
      this._type = type;
   }
   
}
