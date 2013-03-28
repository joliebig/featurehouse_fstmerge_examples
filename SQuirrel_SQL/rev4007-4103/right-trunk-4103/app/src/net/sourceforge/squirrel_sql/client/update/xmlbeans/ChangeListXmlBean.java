
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;

public class ChangeListXmlBean implements Serializable {

   
   private List<ArtifactStatus> changes = null;
   
   
   public List<ArtifactStatus> getChanges() {
      return changes;
   }

   
   public void setChanges(List<ArtifactStatus> changes) {
      this.changes = changes;
   }
   
   
   
}
