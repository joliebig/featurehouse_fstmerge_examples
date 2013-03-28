package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import java.awt.*;

public class SessionStartupTimeHintDlg extends JDialog
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SessionStartupTimeHintDlg.class);

   JCheckBox chkDontShowAgain;
   JButton btnShowProps;
   JButton btnClose;


   public SessionStartupTimeHintDlg(JFrame owner, IApplication app)
   {
      
      super(owner, s_stringMgr.getString("SessionStartupTimeHintDlg.title"),true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,10,5,10), 0,0);
      
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("SessionStartupTimeHintDlg.text")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,10,5,10), 0,0);
      
      chkDontShowAgain = new JCheckBox(s_stringMgr.getString("SessionStartupTimeHintDlg.dontShowAgain"));
      getContentPane().add(chkDontShowAgain, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(app), gbc);
   }

   private JPanel createButtonPanel(IApplication app)
   {
      JPanel ret = new JPanel();
      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      
      btnShowProps = new JButton(s_stringMgr.getString("SessionStartupTimeHintDlg.showAliasProps"));
      btnShowProps.setIcon(app.getResources().getIcon(SquirrelResources.IImageNames.ALIAS_PROPERTIES));
      ret.add(btnShowProps, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      
      btnClose = new JButton(s_stringMgr.getString("SessionStartupTimeHintDlg.close"));
      ret.add(btnClose, gbc);

      return ret;
   }
}
