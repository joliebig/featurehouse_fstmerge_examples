
package net.sourceforge.squirrel_sql.client.update.gui;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class ArtifactStatus implements Serializable {

   private transient static final long serialVersionUID = 3902196017013411091L;

   
   private transient static final StringManager s_stringMgr = 
      StringManagerFactory.getStringManager(ArtifactStatus.class);
   
   private interface i18n extends Serializable {
      
      String TRANSLATION_LABEL = s_stringMgr.getString("ArtifactStatus.translationLabel");
      
      
      String CORE_LABEL = s_stringMgr.getString("ArtifactStatus.coreLabel");
      
      
      String PLUGIN_LABEL = s_stringMgr.getString("ArtifactStatus.pluginLabel");
   }
      
   
   private String name = null;
   
   
   private String type;
   
   
   private boolean installed;
   
   private String displayType;
   
   
   private ArtifactAction artifactAction = ArtifactAction.NONE;

   
	private long size;

	
	private long checksum;
   
	public ArtifactStatus() {}
	
	
	public ArtifactStatus(ArtifactXmlBean artifactXmlBean) {
		this.name = artifactXmlBean.getName();
		this.installed = artifactXmlBean.isInstalled();
		this.size = artifactXmlBean.getSize();
		this.checksum = artifactXmlBean.getChecksum();
	}
	
   
   public String getName() {
      return name;
   }

   
   public void setName(String name) {
      this.name = name;
   }

   
   public String getType() {
      return type;
   }

   
   public void setType(String type) {
      this.type = type;
      if (type.equals("i18n")) {
         this.displayType = i18n.TRANSLATION_LABEL;
      }
      if (type.equals("core")) {
         this.displayType = i18n.CORE_LABEL;
      }
      if (type.equals("plugin")) {
         this.displayType = i18n.PLUGIN_LABEL;
      }      
   }

   public boolean isCoreArtifact() {
      return UpdateUtil.CORE_ARTIFACT_ID.equals(this.type);
   }
   
   public boolean isPluginArtifact() {
      return UpdateUtil.PLUGIN_ARTIFACT_ID.equals(this.type);
   }
   
   public boolean isTranslationArtifact() {
      return UpdateUtil.TRANSLATION_ARTIFACT_ID.equals(this.type);
   }
   
   
   public boolean isInstalled() {
      return installed;
   }

   
   public void setInstalled(boolean installed) {
      this.installed = installed;
   }

   
   public ArtifactAction getArtifactAction() {
      return artifactAction;
   }

   
   public void setArtifactAction(ArtifactAction artifactAction) {
      this.artifactAction = artifactAction;
   }

   
   public String getDisplayType() {
      return displayType;
   }

   
   public void setDisplayType(String displayType) {
      this.displayType = displayType;
   }

   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final ArtifactStatus other = (ArtifactStatus) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

	
	 public String toString()
	 {
	     final String TAB = "    ";
	     
	     String retValue = "";
	     
	     retValue = "ArtifactStatus ( "
	         + super.toString() + TAB
	         + "name = " + this.name + TAB
	         + "type = " + this.type + TAB
	         + "installed = " + this.installed + TAB
	         + "displayType = " + this.displayType + TAB
	         + "artifactAction = " + this.artifactAction + TAB
	         + " )";
	 
	     return retValue;
	 }
	 
	
	public void setSize(long size)
	{
		this.size = size;
	}

	
	public long getSize()
	{
		return size;
	}
	
	
	public void setChecksum(long checksum)
	{
		this.checksum = checksum;
	}

	
	public long getChecksum()
	{
		return checksum;
	}

   
   
   
}
