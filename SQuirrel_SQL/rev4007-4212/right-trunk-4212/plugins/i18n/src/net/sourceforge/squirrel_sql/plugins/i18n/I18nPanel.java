package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.*;
import java.awt.*;

public class I18nPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(I18nPanel.class);

   TranslatorsPanel pnlTranslators;
   DevelopersPanel pnlDevelopers;


   JTabbedPane tabI18n = new JTabbedPane();

   JTextField txtNote;


   public I18nPanel(PluginResources resources)
   {
      pnlTranslators = new TranslatorsPanel(resources);
      pnlDevelopers = new DevelopersPanel(resources);


      GridBagConstraints gbc;

		
		tabI18n.add(s_stringMgr.getString("I18n.translators"), pnlTranslators);
		
		tabI18n.add(s_stringMgr.getString("I18n.developers"), pnlDevelopers);

      setLayout(new GridBagLayout());
      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,5),0,0);
      add(tabI18n, gbc);


      txtNote = new JTextField();
      txtNote.setText(s_stringMgr.getString("I18n.changeApplicationDefaultLocaleNote"));
      txtNote.setEditable(false);
      
      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5),0,0);
      add(txtNote, gbc);
   }
}
