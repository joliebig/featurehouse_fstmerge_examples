package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import javax.swing.*;
import java.awt.*;

public class OracleAliasPrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(OracleAliasPrefsPanel.class);

   JRadioButton radLoadAccessibleSchemasExceptSYS;
   JRadioButton radLoadAccessibleSchemasAndSYS;
   JRadioButton radLoadAllSchemas;

   JButton btnApplyNow;

   public OracleAliasPrefsPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,15,5),0,0);
      
      JLabel lblHeader = new JLabel(s_stringMgr.getString("OracleAliasPrefsPanel.header"));
      Font oldFont = lblHeader.getFont();
      Font newFont = new Font(oldFont.getName(), Font.BOLD, oldFont.getSize());
      lblHeader.setFont(newFont);
      add(lblHeader, gbc);


      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,30,5),0,0);
      
      
      
      
      
      
      
      
      
      String desc = s_stringMgr.getString("OraclePrefsPanel.Description");
      add(new MultipleLineLabel(desc), gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      
      radLoadAccessibleSchemasExceptSYS = new JRadioButton(s_stringMgr.getString("OracleAliasPrefsPanel.AccessibleButSys"));
      add(radLoadAccessibleSchemasExceptSYS, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      
      radLoadAccessibleSchemasAndSYS = new JRadioButton(s_stringMgr.getString("OracleAliasPrefsPanel.AccessibleAndSys"));
      add(radLoadAccessibleSchemasAndSYS, gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      
      radLoadAllSchemas= new JRadioButton(s_stringMgr.getString("OracleAliasPrefsPanel.All"));
      add(radLoadAllSchemas, gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,5,5),0,0);
      
      btnApplyNow = new JButton(s_stringMgr.getString("OracleAliasPrefsPanel.ApplyNow"));
      add(btnApplyNow, gbc);



      gbc = new GridBagConstraints(0,6,1,1,0,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0);
      add(new JPanel(), gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radLoadAccessibleSchemasExceptSYS);
      bg.add(radLoadAccessibleSchemasAndSYS);
      bg.add(radLoadAllSchemas);

   }

}
