
package net.sourceforge.squirrel_sql.client.update.gui;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.client.update.xmlbeans.EnumPersistenceDelegate;


public enum ArtifactAction implements Serializable {
   NONE,
   INSTALL,
   REMOVE;
   
   static { EnumPersistenceDelegate.installFor(values()[0].getClass()); }   
}

