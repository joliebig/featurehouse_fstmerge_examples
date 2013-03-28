
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;



public class InstallStatusEvent {

   String _artifactName;
   
   InstallEventType _type;
   
   public InstallStatusEvent(InstallEventType type) {
      this._type = type;
   }

   
   public String getArtifactName() {
      return _artifactName;
   }

   
   public void setArtifactName(String name) {
      _artifactName = name;
   }

   
   public InstallEventType getType() {
      return _type;
   }

   
   public void setType(InstallEventType type) {
      this._type = type;
   }
   
}
