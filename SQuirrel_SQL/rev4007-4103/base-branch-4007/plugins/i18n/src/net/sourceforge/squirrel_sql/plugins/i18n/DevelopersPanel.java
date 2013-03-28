package net.sourceforge.squirrel_sql.plugins.i18n;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DevelopersPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DevelopersPanel.class);


   JTextField txtSourceDir = new JTextField();
   JButton btnChooseSourceDir;
   JButton btnAppendI18nInCode;


   public DevelopersPanel(PluginResources resources)
   {
      GridBagConstraints gbc;

      setLayout(new GridBagLayout());

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      
		add(new JLabel(s_stringMgr.getString("I18n.SourceDir")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,5),0,0);
      add(txtSourceDir, gbc);

      btnChooseSourceDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      add(btnChooseSourceDir, gbc);

      gbc = new GridBagConstraints(0,1,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
		
		
		
		
		
		
		
		
		
		
		
		MultipleLineLabel lblDescription = new MultipleLineLabel(s_stringMgr.getString("I18n.appendCodeDescription"));
      add(lblDescription, gbc);

      gbc = new GridBagConstraints(0,2,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
		
      btnAppendI18nInCode = new JButton(s_stringMgr.getString("I18n.appendI18nStringsProps"));
      add(btnAppendI18nInCode, gbc);      
      
      JPanel pnlDist = new JPanel();
      gbc = new GridBagConstraints(0,3,3,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      add(pnlDist, gbc);

   }


}
