
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

import net.sourceforge.squirrel_sql.fw.util.Utilities;



public class InstallStatusEvent {

	
   private String _artifactName;
   
   
   private InstallEventType _type;
   
   
   private int numFilesToUpdate = 0;
   
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

	
	public void setNumFilesToUpdate(int numFilesToUpdate)
	{
		this.numFilesToUpdate = numFilesToUpdate;
	}

	
	public int getNumFilesToUpdate()
	{
		return numFilesToUpdate;
	}

   
}
